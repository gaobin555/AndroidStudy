/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.home.multitask.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.android.xthink.ink.launcherink.IMultitaskAidlInterface;
import com.android.xthink.ink.launcherink.ui.home.fragment.manager.PagerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 请描述功能
 *
 * @author liyuyan
 * @version 1.0, 2017/7/31
 */

public class MultitaskService extends Service {

    private List ids = new ArrayList();

    private IMultitaskAidlInterface.Stub mIMultitaskAidlInterface = new IMultitaskAidlInterface.Stub() {

        @Override
        public List getPageIds() throws RemoteException {
            ids.clear();
            ids.addAll(PagerHelper.getAllIds());
            return ids;
        }

        @Override
        public void setPageById(int id) throws RemoteException {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIMultitaskAidlInterface;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}