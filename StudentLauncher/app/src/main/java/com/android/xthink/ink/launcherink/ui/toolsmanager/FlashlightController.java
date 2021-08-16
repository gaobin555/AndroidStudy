package com.android.xthink.ink.launcherink.ui.toolsmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FlashlightController {
    private static final String TAG = "FlashlightController";
    private static final int DISPATCH_ERROR = 0;
    private static final int DISPATCH_CHANGED = 1;
    private static final int DISPATCH_AVAILABILITY_CHANGED = 2;

    private CameraManager mCameraManager;
    private Context mContext;
    /** Call {@link #ensureHandler()} before using */
    private Handler mHandler;

    /** Lock on mListeners when accessing */
    private final ArrayList<WeakReference<FlashlightListener>> mListeners = new ArrayList<>(1);

    /** Lock on {@code this} when accessing */
    private boolean mFlashlightEnabled;

    private String mCameraId;
    private boolean mTorchAvailable;

    // battery low policy, add by jint@x-thinks.com at 20180930 start +++
    private final int LOW_BATTERY_THRESHOLD_FLASH = 15;// Low battery 15 for threshold flash, torch icon is grayed.
//    private boolean mBatteryLow = false;
    // end +++

    public FlashlightController(Context context) {
        mContext = context;
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);

//        registerReceiver();// add register battery intent for torch status, add by jint@x-thinks.com at 20180930 +++
        tryInitCamera();
    }
    // add battery intent for torch icon, add by jint@x-thinks.com at 20180929 +++
//    private void registerReceiver() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        mContext.registerReceiver(this, filter);
//    }

//    @Override
//    public void onReceive(final Context context, Intent intent) {
//        final String action = intent.getAction();
//        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
//            int batteryLevel = intent.getIntExtra("level", 0);
//            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//            // BATTERY_STATUS_INVALID=0, BATTERY_STATUS_UNKNOWN=1, BATTERY_STATUS_CHARGING=2, BATTERY_STATUS_DISCHARGING=3, BATTERY_STATUS_NOT_CHARGING=4, BATTERY_STATUS_FULL=5
//            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
//
//            Log.i(TAG, "batteryLevel:" + batteryLevel + ", level:" + level + ", status:" + status + ", mBatteryLow:" + mBatteryLow);
//            if (level <= LOW_BATTERY_THRESHOLD_FLASH) {
//                mBatteryLow = true;
//            } else {
//                mBatteryLow = false;
//            }
//        }
//    }
    // end +++
    private void tryInitCamera() {
        try {
            mCameraId = getCameraId();
        } catch (Throwable e) {
            Log.e(TAG, "Couldn't initialize.", e);
            return;
        }

        if (mCameraId !=null) {
            ensureHandler();
            mCameraManager.registerTorchCallback(mTorchCallback, mHandler);
        }
    }
    public void setFlashlight(boolean enabled) {
        boolean pendingError = false;
        synchronized (this) {
            if (mCameraId == null) return;
            if (mFlashlightEnabled != enabled) {
                mFlashlightEnabled = enabled;
                try {
                    mCameraManager.setTorchMode(mCameraId, enabled);
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Couldn't set torch mode", e);
                    mFlashlightEnabled = false;
                    pendingError = true;
                }
            }
        }
        dispatchModeChanged(mFlashlightEnabled);
        if (pendingError) {
            dispatchError();
        }
    }

    public boolean hasFlashlight() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    // add Toast for low battery level warning, by gaob@x-thinks.com 20190128 start +++
//    public boolean isLowBattery() {
//        return mBatteryLow;
//    }
    // end +++

    public synchronized boolean isEnabled() {
        return mFlashlightEnabled;
    }

    public synchronized boolean isAvailable() {
        // torch icon should be unavailabe and grayed when battery level <= 15%, add by jint@x-thinks.com at 20180930 start +++
        // add Toast for low battery level warning, by gaob@x-thinks.com 20190128 start +++
//        if (mBatteryLow)
//            return false;
        // end +++
        // end +++
        return mTorchAvailable;
    }

    public void addCallback(FlashlightListener l) {
        synchronized (mListeners) {
            if (mCameraId == null) {
                tryInitCamera();
            }
            cleanUpListenersLocked(l);
            mListeners.add(new WeakReference<>(l));
            l.onFlashlightAvailabilityChanged(mTorchAvailable);
            l.onFlashlightChanged(mFlashlightEnabled);
        }
    }

    public void removeCallback(FlashlightListener l) {
        synchronized (mListeners) {
            cleanUpListenersLocked(l);
        }
    }

    private String getCameraId() throws CameraAccessException {
        String[] ids = mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable
                    && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }

    private synchronized void ensureHandler() {
        if (mHandler == null) {
            HandlerThread thread = new HandlerThread(TAG, 1);
            thread.start();
            mHandler = new Handler(thread.getLooper());
        }
    }

    private final CameraManager.TorchCallback mTorchCallback =
            new CameraManager.TorchCallback() {

                @Override
                public void onTorchModeUnavailable(String cameraId) {
                    if (TextUtils.equals(cameraId, mCameraId)) {
                        setCameraAvailable(false);
                    }
                }

                @Override
                public void onTorchModeChanged(String cameraId, boolean enabled) {
                    if (TextUtils.equals(cameraId, mCameraId)) {
                        setCameraAvailable(true);
                        setTorchMode(enabled);
                    }
                }

                private void setCameraAvailable(boolean available) {
                    boolean changed;
                    synchronized (FlashlightController.this) {
                        changed = mTorchAvailable != available;
                        mTorchAvailable = available;
                    }
                    if (changed) {
                        Log.d(TAG, "dispatchAvailabilityChanged(" + available + ")");
                        dispatchAvailabilityChanged(available);
                    }
                }

                private void setTorchMode(boolean enabled) {
                    boolean changed;
                    synchronized (FlashlightController.this) {
                        changed = mFlashlightEnabled != enabled;
                        mFlashlightEnabled = enabled;
                    }
                    if (changed) {
                        Log.d(TAG, "dispatchModeChanged(" + enabled + ")");
                        dispatchModeChanged(enabled);
                    }
                }
            };

    private void dispatchModeChanged(boolean enabled) {
        dispatchListeners(DISPATCH_CHANGED, enabled);
    }

    private void dispatchError() {
        dispatchListeners(DISPATCH_CHANGED, false /* argument (ignored) */);
    }

    private void dispatchAvailabilityChanged(boolean available) {
        dispatchListeners(DISPATCH_AVAILABILITY_CHANGED, available);
    }

    private void dispatchListeners(int message, boolean argument) {
        synchronized (mListeners) {
            final int N = mListeners.size();
            boolean cleanup = false;
            for (int i = 0; i < N; i++) {
                FlashlightListener l = mListeners.get(i).get();
                if (l != null) {
                    if (message == DISPATCH_ERROR) {
                        l.onFlashlightError();
                    } else if (message == DISPATCH_CHANGED) {
                        l.onFlashlightChanged(argument);
                    } else if (message == DISPATCH_AVAILABILITY_CHANGED) {
                        l.onFlashlightAvailabilityChanged(argument);
                    }
                } else {
                    cleanup = true;
                }
            }
            if (cleanup) {
                cleanUpListenersLocked(null);
            }
        }
    }

    private void cleanUpListenersLocked(FlashlightListener listener) {
        for (int i = mListeners.size() - 1; i >= 0; i--) {
            FlashlightListener found = mListeners.get(i).get();
            if (found == null || found == listener) {
                mListeners.remove(i);
            }
        }
    }

    public interface FlashlightListener {

        /**
         * Called when the flashlight was turned off or on.
         * @param enabled true if the flashlight is currently turned on.
         */
        void onFlashlightChanged(boolean enabled);


        /**
         * Called when there is an error that turns the flashlight off.
         */
        void onFlashlightError();

        /**
         * Called when there is a change in availability of the flashlight functionality
         * @param available true if the flashlight is currently available.
         */
        void onFlashlightAvailabilityChanged(boolean available);
    }
}
