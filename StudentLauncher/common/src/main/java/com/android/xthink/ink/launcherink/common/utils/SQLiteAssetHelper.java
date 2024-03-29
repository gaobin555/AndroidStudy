/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/4/15
 */
public class SQLiteAssetHelper extends SQLiteOpenHelper {
    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
    private static final String ASSET_DB_PATH = "databases";

    private final Context mContext;
    private final String mName;
    private final CursorFactory mFactory;
    private final int mNewVersion;

    private SQLiteDatabase mDatabase = null;
    private boolean mIsInitializing = false;

    private String mDatabasePath;

    private String mAssetPath;

    private String mUpgradePathFormat;

    private int mForcedUpgradeVersion = 0;

    /**
     * Create a helper object to create, open, and/or manage a database in
     * a specified location.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context          to use to open or create the database
     * @param name             of the database file
     * @param storageDirectory to store the database file upon creation; caller must
     *                         ensure that the specified absolute path is available and can be written to
     * @param factory          to use for creating cursor objects, or null for the default
     * @param version          number of the database (starting at 1); if the database is older,
     *                         SQL file(s) contained within the application assets folder will be used to
     *                         upgrade the database
     */
    public SQLiteAssetHelper(Context context, String name, String storageDirectory, CursorFactory factory, int version) {
        super(context, name, factory, version);

        if (version < 1) throw new IllegalArgumentException("Version must be >= 1, was " + version);
        if (name == null) throw new IllegalArgumentException("Database name cannot be null");

        mContext = context;
        mName = name;
        mFactory = factory;
        mNewVersion = version;

        mAssetPath = ASSET_DB_PATH + "/" + name;
        if (storageDirectory != null) {
            mDatabasePath = storageDirectory;
        } else {
            mDatabasePath = context.getApplicationInfo().dataDir + "/databases";
        }
        mUpgradePathFormat = ASSET_DB_PATH + "/" + name + "_upgrade_%s-%s.sql";
    }

    /**
     * Create a helper object to create, open, and/or manage a database in
     * the application's default private data directory.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                SQL file(s) contained within the application assets folder will be used to
     *                upgrade the database
     */
    public SQLiteAssetHelper(Context context, String name, CursorFactory factory, int version) {
        this(context, name, null, factory, version);
    }

    /**
     * Create and/or open a database that will be used for reading and writing.
     * The first time this is called, the database will be extracted and copied
     * from the application's assets folder.
     * <p>
     * <p>Once opened successfully, the database is cached, so you can
     * call this method every time you need to write to the database.
     * (Make sure to call {@link #close} when you no longer need the database.)
     * Errors such as bad permissions or a full disk may cause this method
     * to fail, but future attempts may succeed if the problem is fixed.</p>
     * <p>
     * <p class="caution">Database upgrade may take a long time, you
     * should not call this method from the application main thread, including
     * from {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
     *
     * @return a read/write database object valid until {@link #close} is called
     * @throws SQLiteException if the database cannot be opened for writing
     */
    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
            return mDatabase;  // The database is already open for business
        }

        if (mIsInitializing) {
            throw new IllegalStateException("getWritableDatabase called recursively");
        }

        // If we have a read-only database open, someone could be using it
        // (though they shouldn't), which would cause a lock to be held on
        // the file, and our attempts to open the database read-write would
        // fail waiting for the file lock.  To prevent that, we acquire the
        // lock on the read-only database, which shuts out other users.

        boolean success = false;
        SQLiteDatabase db = null;
        //if (mDatabase != null) mDatabase.lock();
        try {
            mIsInitializing = true;
            //if (mName == null) {
            //    db = SQLiteDatabase.create(null);
            //} else {
            //    db = mContext.openOrCreateDatabase(mName, 0, mFactory);
            //}
            db = createOrOpenDatabase(false);

            int version = db.getVersion();

            // do force upgrade
            if (version != 0 && version < mForcedUpgradeVersion) {
                db = createOrOpenDatabase(true);
                db.setVersion(mNewVersion);
                version = db.getVersion();
            }

            if (version != mNewVersion) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        if (version > mNewVersion) {
                            Log.w(TAG, "Can't downgrade read-only database from version " +
                                    version + " to " + mNewVersion + ": " + db.getPath());
                        }
                        onUpgrade(db, version, mNewVersion);
                    }
                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            onOpen(db);
            success = true;
            return db;
        } finally {
            mIsInitializing = false;
            if (success) {
                if (mDatabase != null) {
                    try {
                        mDatabase.close();
                    } catch (Exception e) {
                    }
                    //mDatabase.unlock();
                }
                mDatabase = db;
            } else {
                //if (mDatabase != null) mDatabase.unlock();
                if (db != null) db.close();
            }
        }

    }

    /**
     * Create and/or open a database.  This will be the same object returned by
     * {@link #getWritableDatabase} unless some problem, such as a full disk,
     * requires the database to be opened read-only.  In that case, a read-only
     * database object will be returned.  If the problem is fixed, a future call
     * to {@link #getWritableDatabase} may succeed, in which case the read-only
     * database object will be closed and the read/write object will be returned
     * in the future.
     * <p>
     * <p class="caution">Like {@link #getWritableDatabase}, this method may
     * take a long time to return, so you should not call it from the
     * application main thread, including from
     * {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
     *
     * @return a database object valid until {@link #getWritableDatabase}
     * or {@link #close} is called.
     * @throws SQLiteException if the database cannot be opened
     */
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (mDatabase != null && mDatabase.isOpen()) {
            return mDatabase;  // The database is already open for business
        }

        if (mIsInitializing) {
            throw new IllegalStateException("getReadableDatabase called recursively");
        }

        try {
            return getWritableDatabase();
        } catch (SQLiteException e) {
            if (mName == null) throw e;  // Can't open a temp database read-only!
            Log.e(TAG, "Couldn't open " + mName + " for writing (will try read-only):", e);
        }

        SQLiteDatabase db = null;
        try {
            mIsInitializing = true;
            String path = mContext.getDatabasePath(mName).getPath();
            db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY);
            if (db.getVersion() != mNewVersion) {
                throw new SQLiteException("Can't upgrade read-only database from version " +
                        db.getVersion() + " to " + mNewVersion + ": " + path);
            }

            onOpen(db);
            Log.w(TAG, "Opened " + mName + " in read-only mode");
            mDatabase = db;
            return mDatabase;
        } finally {
            mIsInitializing = false;
            if (db != null && db != mDatabase) db.close();
        }
    }

    /**
     * Close any open database object.
     */
    @Override
    public synchronized void close() {
        if (mIsInitializing) throw new IllegalStateException("Closed during initialization");

        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    @Override
    public final void onConfigure(SQLiteDatabase db) {
        // not supported!
    }

    @Override
    public final void onCreate(SQLiteDatabase db) {
        // do nothing - createOrOpenDatabase() is called in
        // getWritableDatabase() to handle database creation.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "Upgrading database " + mName + " from version " + oldVersion + " to " + newVersion + "...");

        ArrayList<String> paths = new ArrayList<>();
        getUpgradeFilePaths(oldVersion, newVersion - 1, newVersion, paths);

        if (paths.isEmpty()) {
            Log.e(TAG, "no upgrade script path from " + oldVersion + " to " + newVersion);
            throw new SQLiteAssetException("no upgrade script path from " + oldVersion + " to " + newVersion);
        }

        Collections.sort(paths, new VersionComparator());
        for (String path : paths) {
            try {
                Log.w(TAG, "processing upgrade: " + path);
                InputStream is = mContext.getAssets().open(path);
                String sql = FileUtils.convertStreamToString(is);
                if (sql != null) {
                    List<String> cmds = FileUtils.splitSqlScript(sql, ';');
                    for (String cmd : cmds) {
                        //Log.d(TAG, "cmd=" + cmd);
                        if (cmd.trim().length() > 0) {
                            db.execSQL(cmd);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.w(TAG, "Successfully upgraded database " + mName + " from version " + oldVersion + " to " + newVersion);

    }

    @Override
    public final void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // not supported!
    }

    /**
     * Bypass the upgrade process (for each increment up to a given version) and simply
     * overwrite the existing database with the supplied asset file.
     *
     * @param version bypass upgrade up to this version number - should never be greater than the
     *                latest database version.
     * @deprecated use {@link #setForcedUpgrade} instead.
     */
    @Deprecated
    public void setForcedUpgradeVersion(int version) {
        setForcedUpgrade(version);
    }

    /**
     * Bypass the upgrade process (for each increment up to a given version) and simply
     * overwrite the existing database with the supplied asset file.
     *
     * @param version bypass upgrade up to this version number - should never be greater than the
     *                latest database version.
     */
    public void setForcedUpgrade(int version) {
        mForcedUpgradeVersion = version;
    }

    /**
     * Bypass the upgrade process for every version increment and simply overwrite the existing
     * database with the supplied asset file.
     */
    public void setForcedUpgrade() {
        setForcedUpgrade(mNewVersion);
    }

    private SQLiteDatabase createOrOpenDatabase(boolean force) throws SQLiteAssetException {

        // test for the existence of the db file first and don't attempt open
        // to prevent the error trace in log on API 14+
        SQLiteDatabase db = null;
        File file = new File(mDatabasePath + "/" + mName);
        if (file.exists()) {
            db = returnDatabase();
        }
        //SQLiteDatabase db = returnDatabase();

        if (db != null) {
            // database already exists
            if (force) {
                Log.w(TAG, "forcing database upgrade!");
                copyDatabaseFromAssets();
                db = returnDatabase();
            }
            return db;
        } else {
            // database does not exist, copy it from assets and return it
            copyDatabaseFromAssets();
            db = returnDatabase();
            return db;
        }
    }

    private SQLiteDatabase returnDatabase() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(mDatabasePath + "/" + mName, mFactory, SQLiteDatabase.OPEN_READWRITE);
            Log.i(TAG, "successfully opened database " + mName);
            return db;
        } catch (SQLiteException e) {
            Log.w(TAG, "could not open database " + mName + " - " + e.getMessage());
            return null;
        }
    }

    private void copyDatabaseFromAssets() throws SQLiteAssetException {
        Log.w(TAG, "copying database from assets...");

        String path = mAssetPath;
        String dest = mDatabasePath + "/" + mName;
        InputStream is;
        boolean isZip = false;

        try {
            // try uncompressed
            is = mContext.getAssets().open(path);
        } catch (IOException e) {
            // try zip
            try {
                is = mContext.getAssets().open(path + ".zip");
                isZip = true;
            } catch (IOException e2) {
                // try gzip
                try {
                    is = mContext.getAssets().open(path + ".gz");
                } catch (IOException e3) {
                    SQLiteAssetException se = new SQLiteAssetException("Missing " + mAssetPath + " file (or .zip, .gz archive) in assets, or target folder not writable");
                    se.setStackTrace(e3.getStackTrace());
                    throw se;
                }
            }
        }

        try {
            File f = new File(mDatabasePath + "/");
            if (!f.exists()) {
                f.mkdir();
            }
            FileUtils.writeExtractedFileToDisk(is, new FileOutputStream(dest));

            Log.w(TAG, "database copy complete");

        } catch (IOException e) {
            SQLiteAssetException se = new SQLiteAssetException("Unable to write " + dest + " to data directory");
            se.setStackTrace(e.getStackTrace());
            throw se;
        }
    }

    private InputStream getUpgradeSQLStream(int oldVersion, int newVersion) {
        String path = String.format(mUpgradePathFormat, oldVersion, newVersion);
        try {
            return mContext.getAssets().open(path);
        } catch (IOException e) {
            Log.w(TAG, "missing database upgrade script: " + path);
            return null;
        }
    }

    private void getUpgradeFilePaths(int baseVersion, int start, int end, ArrayList<String> paths) {

        int a;
        int b;

        InputStream is = getUpgradeSQLStream(start, end);
        if (is != null) {
            String path = String.format(mUpgradePathFormat, start, end);
            paths.add(path);
            //Log.d(TAG, "found script: " + path);
            a = start - 1;
            b = start;
            is = null;
        } else {
            a = start - 1;
            b = end;
        }

        if (a >= baseVersion) {
            getUpgradeFilePaths(baseVersion, a, b, paths); // recursive call
        }
    }

    /**
     * An exception that indicates there was an error with SQLite asset retrieval or parsing.
     */
    @SuppressWarnings("serial")
    public static class SQLiteAssetException extends SQLiteException {

        public SQLiteAssetException() {
        }

        public SQLiteAssetException(String error) {
            super(error);
        }
    }

    private class VersionComparator implements Comparator<String> {

        private Pattern pattern = Pattern
                .compile(".*_upgrade_([0-9]+)-([0-9]+).*");

        /**
         * Compares the two specified upgrade script strings to determine their
         * relative ordering considering their two version numbers. Assumes all
         * database names used are the same, as this function only compares the
         * two version numbers.
         *
         * @param file0
         *            an upgrade script file name
         * @param file1
         *            a second upgrade script file name to compare with file0
         * @return an integer < 0 if file0 should be applied before file1, 0 if
         *         they are equal (though that shouldn't happen), and > 0 if
         *         file0 should be applied after file1.
         *
         * @exception SQLiteAssetException
         *                thrown if the strings are not in the correct upgrade
         *                script format of:
         *                <code>databasename_fromVersionInteger_toVersionInteger</code>
         */
        @Override
        public int compare(String file0, String file1) {
            Matcher m0 = pattern.matcher(file0);
            Matcher m1 = pattern.matcher(file1);

            if (!m0.matches()) {
                Log.w(TAG, "could not parse upgrade script file: " + file0);
                throw new SQLiteAssetException("Invalid upgrade script file");
            }

            if (!m1.matches()) {
                Log.w(TAG, "could not parse upgrade script file: " + file1);
                throw new SQLiteAssetException("Invalid upgrade script file");
            }

            int v0_from = Integer.valueOf(m0.group(1));
            int v1_from = Integer.valueOf(m1.group(1));
            int v0_to = Integer.valueOf(m0.group(2));
            int v1_to = Integer.valueOf(m1.group(2));

            if (v0_from == v1_from) {
                // 'from' versions match for both; check 'to' version next

                if (v0_to == v1_to) {
                    return 0;
                }

                return v0_to < v1_to ? -1 : 1;
            }

            return v0_from < v1_from ? -1 : 1;
        }
    }

}

