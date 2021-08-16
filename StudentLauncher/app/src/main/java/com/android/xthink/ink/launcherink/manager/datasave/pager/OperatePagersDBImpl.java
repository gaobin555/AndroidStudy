package com.android.xthink.ink.launcherink.manager.datasave.pager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.xthink.ink.launcherink.GlobalDataCache;
import com.android.xthink.ink.launcherink.bean.InkPageEditBean;
import com.android.xthink.ink.launcherink.bean.InkPageFullInfoBean;
import com.android.xthink.ink.launcherink.bean.InkPageInfoBean;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.DbUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.init.InitAppHelper;
import com.android.xthink.ink.launcherink.manager.InkDbHelper;

import java.util.ArrayList;

/**
 * 页面数据库的一些操作方法
 * Created by liyuyan on 2016/12/26.
 */

public class OperatePagersDBImpl {

    private static final String TAG = "OperatePagersDBImpl";

    private Context mContext;

    public OperatePagersDBImpl(Context mContext) {
        this.mContext = mContext;
    }

    public void setPagerIsSelected(int id, boolean isSelected) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_is_selected", isSelected);
        db.update(InkConstants.JV_PAGER_DATA, contentValues, "item_id = ?", new String[]{id + ""});
        close(null, db);
    }

    /**
     * 修改Home
     *
     * @param newPageId 新的Home
     * @param oldPageId 旧的Home,提供旧的id就不用再额外去查了。
     */
    public void updateHomePage(int newPageId, int oldPageId) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();

        // 设置新的Home
        String sqlSetNewHome = "update " + InkConstants.JV_PAGER_DATA +
                " set " + PagerConstants.ITEM_IS_HOME_PAGE + " = 1" +
                " where " + PagerConstants.ITEM_ID + " = " + newPageId;
        MyLog.i(TAG, "updateHomePage, sqlSetNewHome" + sqlSetNewHome);
        db.execSQL(sqlSetNewHome);

        // 去掉旧的Home
        String sqlCancelOldHome = "update " + InkConstants.JV_PAGER_DATA +
                " set " + PagerConstants.ITEM_IS_HOME_PAGE + " = 0" +
                " where " + PagerConstants.ITEM_ID + " = " + oldPageId;
        MyLog.i(TAG, "updateHomePage, sqlCancelOldHome: " + sqlCancelOldHome);
        db.execSQL(sqlCancelOldHome);

        db.close();
    }

    /**
     * 更新home
     *
     * @param db        数据库对象
     * @param newPageId 新的home
     */
    public void updateHomePage(SQLiteDatabase db, int newPageId) {

        boolean exist = DbUtils.isRowExist(db, InkConstants.JV_PAGER_DATA, PagerConstants.ITEM_ID, String.valueOf(newPageId));
        if (!exist) {
            MyLog.i(TAG, "onUpgrade: new home is not exist");
            return;
        }
        MyLog.i(TAG, "onUpgrade: new home is:" + InitAppHelper.getInstance().getCnPageNameById(newPageId));

        String resetOldHomeSql = "update " + InkConstants.JV_PAGER_DATA +
                " set " + PagerConstants.ITEM_IS_HOME_PAGE + "=" + PagerConstants.PAGE_FALSE +
                " where " + PagerConstants.ITEM_IS_HOME_PAGE + "=" + PagerConstants.PAGE_TRUE;
        MyLog.i(TAG, "onUpgrade: resetOldHomeSql:" + resetOldHomeSql);
        db.execSQL(resetOldHomeSql);

        String setNewHomeSql = "update " + InkConstants.JV_PAGER_DATA +
                " set " + PagerConstants.ITEM_IS_HOME_PAGE + "=" + PagerConstants.PAGE_TRUE +
                " where " + PagerConstants.ITEM_ID + "=" + newPageId;
        MyLog.i(TAG, "onUpgrade: setNewHomeSql:" + setNewHomeSql);
        db.execSQL(setNewHomeSql);

    }

    /**
     * 得到某个位置的pager页面
     */
    public String queryPagerTitle(int id) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("select item_name from " + InkConstants.JV_PAGER_DATA + " where item_id = '" + id + "'", null);
        String title = "";
        //游标移到第一条记录准备获取数据
        if (cursor.moveToFirst()) {
            // 获取数据中的LONG类型数据
            title = cursor.getString(0);
        }
        close(cursor, db);
        return title;
    }

    /**
     * 得到某个位置的pager页面
     */
    public ArrayList<Integer> queryShowPagersId() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + InkConstants.JV_PAGER_DATA + " where item_is_home_page = '" + "1" + "'", null);
        while (cursor.moveToNext()) {
            arrayList.add(Integer.parseInt(cursor.getString(cursor.getColumnIndex(PagerConstants.ITEM_ID))));
        }
        close(cursor, db);
        return arrayList;
    }

    public String queryImageUrl(int id) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("select item_image_url from " + InkConstants.JV_PAGER_DATA + " where item_id = '" + id + "'", null);
        String item_image_url = "";
        //游标移到第一条记录准备获取数据
        if (cursor.moveToFirst()) {
            // 获取数据中的LONG类型数据
            item_image_url = (String) cursor.getString(0);
        }
        close(cursor, db);
        return item_image_url;
    }

    public boolean queryPagerIsSelected(int id) {
        int i = 0;
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("select item_is_selected from " + InkConstants.JV_PAGER_DATA + " where item_id = '" + id + "'", null);
        if (cursor.moveToFirst()) {
            // 获取数据中的LONG类型数据
            i = cursor.getInt(0);
        }
        close(cursor, db);
        return i == 1;
    }


    //遍历数据库,取出所有未冻结条目信息 add by renxu
    /**
     * 获得所有活动条目信息
     *
     * @return 所有页面信息
     */
    /**
     * public TreeMap<Integer,JvPagerBean> getActiveItemInfo() {
     * <p>
     * String sql = "select * from " + InkConstants.JV_PAGER_DATA;
     * SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
     * TreeMap<Integer,JvPagerBean> pointList = new TreeMap<>();
     * JvPagerBean pagerBean = null;
     * Cursor cursor = db.rawQuery(sql, null);
     * <p>
     * while (cursor.moveToNext()) {
     * pagerBean = new JvPagerBean();
     * pagerBean.item_is_home_page = ;
     * <p>
     * pagerBean.item_info = cursor.getString(cursor.getColumnIndex(PagerConstants.ITEM_INFO));
     * <p>
     * pagerBean.item_image_url = cursor.getString(cursor.getColumnIndex(PagerConstants.ITEM_IMAGE_URL));
     * <p>
     * <p>
     * pagerBean.item_location = cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_LOCATION));
     * <p>
     * <p>
     * pointList.put(pagerBean.item_location,pagerBean);
     * }
     * <p>
     * close(cursor, db);
     * return pointList;
     * }
     */

    public ArrayList<InkPageEditBean> queryAllPages() {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        ArrayList<InkPageEditBean> result = queryAllPages(db);
        close(db);
        return result;
    }

    public ArrayList<InkPageEditBean> queryAllPages(SQLiteDatabase db) {
        String sql = "select * from " + InkConstants.JV_PAGER_DATA + " order by "
                + PagerConstants.ITEM_INDEX + " asc";
        Cursor cursor = db.rawQuery(sql, null);
        if (null == cursor || cursor.getCount() <= 0) {
            close(cursor);
            return null;
        }
        ArrayList<InkPageEditBean> allPages = new ArrayList<>();
        while (cursor.moveToNext()) {
            InkPageEditBean page = getEditPage(cursor);
            allPages.add(page);
        }

        close(cursor);
        return allPages;
    }

    private InkPageEditBean getEditPage(Cursor cursor) {

        InkPageEditBean mEditPage = new InkPageEditBean();
        mEditPage.setItemId(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_ID)));
        mEditPage.setName(cursor.getString(cursor.getColumnIndex(PagerConstants.ITEM_NAME)));
        mEditPage.setTitle(cursor.getString(cursor.getColumnIndex(PagerConstants.ITEM_TITLE)));
        mEditPage.setIndex(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_INDEX)));

        mEditPage.setDefaultHomePage(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_IS_DEFAULT_HOME_PAGE)) > 0);
        mEditPage.setHomePage(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_IS_HOME_PAGE)) > 0);
        mEditPage.setProtected(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_IS_PROTECTED)) > 0);
        mEditPage.setShow(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_IS_SELECTED)) > 0);
        mEditPage.setEditable(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_IS_EDITABLE)) > 0);
        return mEditPage;
    }

    public int queryPageIndex(int id) {

        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + PagerConstants.ITEM_INDEX + " from " + InkConstants.JV_PAGER_DATA + " where item_id = '" + id + "'", null);
        int index = -1;
        //游标移到第一条记录准备获取数据
        if (cursor.moveToFirst()) {
            // 获取数据中的LONG类型数据
            index = cursor.getInt(0);
        }
        close(cursor, db);
        return index;
    }

    public int getHomePageIndex() {
        String sql = "select " + PagerConstants.ITEM_INDEX + " from " + InkConstants.JV_PAGER_DATA + " where "
                + PagerConstants.ITEM_IS_HOME_PAGE + " = " + "1"; //TODO 1是首页，0不是首页，要定义常量或改为boolean类型
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (null == cursor || 0 >= cursor.getCount()) {
            close(cursor, db);
            return GlobalDataCache.getInstance().getDefaultHomeScreenIndex();
        }

        cursor.moveToFirst();
        int homeIndex = cursor.getInt(0);
        close(cursor, db);
        return homeIndex;
    }

    public ArrayList<InkPageInfoBean> querySelectedPagesInfo() {
        String sql = "select " + PagerConstants.ITEM_INDEX
                + ", " + PagerConstants.ITEM_ID
                + ", " + PagerConstants.ITEM_IS_HOME_PAGE
                + ", " + PagerConstants.ITEM_NAME
                + ", " + PagerConstants.ITEM_IS_DEFAULT_HOME_PAGE
                + " from " + InkConstants.JV_PAGER_DATA
                + " where "
                + PagerConstants.ITEM_IS_SELECTED
                + " == " + "1"
                + " order by "
                + PagerConstants.ITEM_INDEX
                + " asc";//TODO 1是已选中，0未选中，要定义常量或改为boolean类型
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (null == cursor || 0 >= cursor.getCount()) {
            close(cursor, db);
            return null;
        }

        ArrayList<InkPageInfoBean> pagesInfo = new ArrayList<InkPageInfoBean>();

        while (cursor.moveToNext()) {
            InkPageInfoBean pageInfo = new InkPageInfoBean();
            pageInfo.setHomePage(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_IS_HOME_PAGE)) > 0);
            pageInfo.setIndex(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_INDEX)));
            pageInfo.setItemId(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_ID)));
            pageInfo.setName(cursor.getString(cursor.getColumnIndex(PagerConstants.ITEM_NAME)));
            pageInfo.setDefaultHomePage(cursor.getInt(cursor.getColumnIndex(PagerConstants.ITEM_IS_DEFAULT_HOME_PAGE)) > 0);
            pagesInfo.add(pageInfo);
        }

        close(cursor, db);
        return pagesInfo;
    }

    /**
     * 设置页面状态
     *
     * @param itemId     page id
     * @param isHome     is home page
     * @param isSelected is page selected
     */
    public void updatePagerSate(int itemId, boolean isHome, boolean isSelected) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_is_home_page", isHome);
        contentValues.put("item_is_selected", isSelected);
        db.update(InkConstants.JV_PAGER_DATA, contentValues, "item_id = ?",
                new String[]{String.valueOf(itemId)});
        close(null, db);
    }

    private void close(Cursor cursor, SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    private void close(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }

    /**
     * 更新选中状态
     *
     * @param pageId   id，唯一标示page
     * @param selected 选中与否
     */
    public void updatePageSelectedById(int pageId, boolean selected) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        updatePageSelectedById(db, pageId, selected);
        db.close();
    }

    /**
     * 更新选中状态
     *
     * @param db       数据库
     * @param pageId   id，唯一标示page
     * @param selected 选中与否
     */
    public void updatePageSelectedById(SQLiteDatabase db, int pageId, boolean selected) {
        db.execSQL("update "
                + InkConstants.JV_PAGER_DATA
                + " set " + PagerConstants.ITEM_IS_SELECTED
                + "=" + String.valueOf(selected ? 1 : 0)
                + " where " + PagerConstants.ITEM_ID
                + " =?", new String[]{String.valueOf(pageId)});
    }

    /**
     * 批量修改所有page的选中状态
     */
    public void updateAllPageSelected(SQLiteDatabase db, boolean selected) {
        db.execSQL("update "
                + InkConstants.JV_PAGER_DATA
                + " set " + PagerConstants.ITEM_IS_SELECTED
                + "=" + String.valueOf(selected ? 1 : 0));
    }

    public void updateChangedEditPage(ArrayList<InkPageEditBean> allChangedPages, boolean isHomePageChanged,
                                      InkPageEditBean soureHomePage, InkPageEditBean targetHomePage) {

        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        try {
            db.beginTransaction();

            if (null != allChangedPages && 0 < allChangedPages.size()) {
                for (InkPageEditBean page : allChangedPages) {
                    db.execSQL("update "
                            + InkConstants.JV_PAGER_DATA
                            + " set " + PagerConstants.ITEM_IS_SELECTED
                            + "=" + String.valueOf(page.isShow() ? 1 : 0)
                            + " where " + PagerConstants.ITEM_ID
                            + " =?", new String[]{String.valueOf(page.getItemId())});
                }
            }

            if (isHomePageChanged) {
                db.execSQL("update "
                        + InkConstants.JV_PAGER_DATA
                        + " set " + PagerConstants.ITEM_IS_HOME_PAGE
                        + " = " + String.valueOf(soureHomePage.isHomePage() ? 1 : 0)
                        + " where " + PagerConstants.ITEM_ID
                        + " == " + String.valueOf(soureHomePage.getItemId()));
                db.execSQL("update "
                        + InkConstants.JV_PAGER_DATA
                        + " set " + PagerConstants.ITEM_IS_HOME_PAGE
                        + " = " + String.valueOf(targetHomePage.isHomePage() ? 1 : 0)
                        + " where " + PagerConstants.ITEM_ID
                        + " == " + String.valueOf(targetHomePage.getItemId()));
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        close(null, db);
    }

    public void updateChangedEditPage(ArrayList<InkPageEditBean> allChangedPages) {

        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        try {
            db.beginTransaction();

            if (null != allChangedPages && 0 < allChangedPages.size()) {
                for (InkPageEditBean page : allChangedPages) {
                    db.execSQL("update "
                            + InkConstants.JV_PAGER_DATA
                            + " set " + PagerConstants.ITEM_IS_SELECTED
                            + "=" + String.valueOf(page.isShow() ? 1 : 0)
                            + " where " + PagerConstants.ITEM_ID
                            + " =?", new String[]{String.valueOf(page.getItemId())});
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        close(null, db);
    }

    /**
     * 更新插件数据库
     *
     * @param bean
     */
    private boolean updatePage(InkPageFullInfoBean bean) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        try {
            if (null != bean && 0 < bean.getItemId()) {
                db.execSQL("update "
                        + InkConstants.JV_PAGER_DATA + " set "
                        + PagerConstants.ITEM_TITLE
                        + "='" + bean.getTitle() + "', "
                        + PagerConstants.ITEM_NAME
                        + "='" + bean.getName() + "', "
                        + PagerConstants.ITEM_INDEX
                        + "=" + bean.getIndex() + ", "
                        + PagerConstants.ITEM_IS_SELECTED
                        + "=" + (bean.isSelected() ? "1" : "0") + ", "
                        + PagerConstants.ITEM_IS_PROTECTED
                        + "=" + (bean.isProtected() ? "1" : "0") + ", "
                        + PagerConstants.ITEM_IS_DEFAULT_HOME_PAGE
                        + "=" + (bean.isDefaultHomePage() ? "1" : "0") + ", "
                        + PagerConstants.ITEM_IS_HOME_PAGE
                        + "=" + (bean.isHomePage() ? "1" : "0") + ", "
                        + PagerConstants.ITEM_IS_EDITABLE
                        + "=" + (bean.isEditable() ? "1" : "0")
                        + " where " + PagerConstants.ITEM_ID
                        + "=" + String.valueOf(bean.getItemId()));
            }
            db.close();
            MyLog.e("updatePage succeed:", bean.getItemId() + "");
            return true;
        } catch (Exception e) {
            MyLog.e("updatePage error:", e.toString());
            return false;
        }
    }

    /**
     * 向插件数据库中插入数据
     *
     * @param bean 数据bean
     */
    public boolean insertPage(InkPageFullInfoBean bean) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        try {
            if (null != bean && 0 < bean.getItemId()) {
                String insertColumn = "insert into " + InkConstants.JV_PAGER_DATA
                        + "(" + PagerConstants.ITEM_ID + ", "
                        + PagerConstants.ITEM_TITLE + ", "
                        + PagerConstants.ITEM_NAME + ", "
                        + PagerConstants.ITEM_INDEX + ", "
                        + PagerConstants.ITEM_IS_SELECTED + ", "
                        + PagerConstants.ITEM_IS_PROTECTED + ", "
                        + PagerConstants.ITEM_IS_DEFAULT_HOME_PAGE + ", "
                        + PagerConstants.ITEM_IS_HOME_PAGE + ", "
                        + PagerConstants.ITEM_IS_EDITABLE + ") ";

                db.execSQL(
                        insertColumn + "Values(" +
                                bean.getItemId() + ", '" +
                                bean.getTitle() + "', '" +
                                bean.getName() + "', " +
                                bean.getIndex() + ", " +
                                (bean.isSelected() ? "1" : "0") + ", " +
                                (bean.isProtected() ? "1" : "0") + ", " +
                                (bean.isDefaultHomePage() ? "1" : "0") + ", " +
                                (bean.isHomePage() ? "1" : "0") + ", " +
                                (bean.isEditable() ? "1" : "0") + ")"
                );
            }
            db.close();
            return true;
        } catch (Exception e) {
            MyLog.e("insertPage error:", e.toString());
            return false;
        }

    }

    /**
     * 判断页面中是否存在指定数据
     *
     * @param itemId
     * @return
     */
    public boolean queryPageExist(int itemId) {
        String sql = "select * from " + InkConstants.JV_PAGER_DATA +
                " where item_id = '" + itemId + "'";
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (null == cursor || cursor.getCount() <= 0) {
            close(cursor, db);
            MyLog.d("queryPageExist false:", itemId + "");
            return false;
        }
        close(cursor, db);
        MyLog.d("queryPageExist true:", itemId + "");
        return true;
    }

    /**
     * 添加一个插件数据,数据库中有就更新原有的
     *
     * @param bean
     */
    public void addPageData(InkPageFullInfoBean bean) {
        if (queryPageExist(bean.getItemId())) {
            updatePage(bean);
        } else {
            insertPage(bean);
        }
    }
}
