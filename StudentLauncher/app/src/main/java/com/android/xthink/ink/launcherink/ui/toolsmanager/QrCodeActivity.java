package com.android.xthink.ink.launcherink.ui.toolsmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xthink.ink.launcherink.base.TitleBaseActivity;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.android.xthink.ink.launcherink.utils.Tool;
import com.eink.swtcon.SwtconControl;
import com.king.zxing.util.CodeUtils;

import com.android.xthink.ink.launcherink.R;

public class QrCodeActivity extends TitleBaseActivity {
    private static final String TAG = "ToolsManagerActivity";
    private static int MAX_COUNT = 6;
    private int mQrcodeClickCount = 0;
    private int mTextClickCount = 0;

    public static void start(Context context) {
        Intent starter = new Intent(context, QrCodeActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_GLD16);
        context.startActivity(starter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qcode;
    }

    @Override
    protected void initView() {
        ImageView qrcode_imageview = findViewById(R.id.qrcode);
        TextView textView = findViewById(R.id.t_context);
        String imei = Tool.getIMEI(this);
        TextView textImei = findViewById(R.id.t_imei);
        Log.d(TAG, "imei = " + imei);
        textImei.setText(imei);
        qrcode_imageview.setOnClickListener(this);
        textView.setOnClickListener(this);

        try {
            Bitmap qrcode = CodeUtils.createQRCode(imei, 400);
            qrcode_imageview.setImageBitmap(qrcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {}

    @Override
    protected void setListener() {}

    @Override
    protected String getTitleText() {
        return getString(R.string.qrcode_title);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isApkExist("com.xthink.factorytest")) {
            return;
        }
        int id = v.getId();
        if (id == R.id.qrcode) {
            mQrcodeClickCount ++;
            if (mQrcodeClickCount == MAX_COUNT) {
                Toast.makeText(this, "继续点击两次文本区域将启动工模程序！", Toast.LENGTH_LONG).show();
            } else if (mQrcodeClickCount > MAX_COUNT) {
                mQrcodeClickCount = 0;
            }
            Log.d(TAG, "onClick qrcode mQrcodeClickCount = " + mQrcodeClickCount);
            if (mTextClickCount != MAX_COUNT) {
                mTextClickCount = 0;
            } else {
                checkSecretCode();
            }
        } else if (id == R.id.t_context) {
            mTextClickCount ++;
            if (mTextClickCount == MAX_COUNT) {
                Toast.makeText(this, "继续点击两次二维码区域将启动工模程序！", Toast.LENGTH_LONG).show();
            } else if (mTextClickCount > MAX_COUNT) {
                mTextClickCount = 0;
            }
            Log.d(TAG, "onClick text mTextClickCount = " + mTextClickCount);
            if (mQrcodeClickCount != MAX_COUNT) {
                mQrcodeClickCount = 0;
            } else {
                checkSecretCode();
            }
        }
    }

    private void checkSecretCode() {
        if (mTextClickCount == MAX_COUNT && mQrcodeClickCount == 2) {
            Log.d(TAG,"启动工模程序");
            launcherActivity("com.xthink.factorytest", "com.xthink.factorytest.cipher_mmi");
            finish();
        }

        if (mTextClickCount == 2 && mQrcodeClickCount == MAX_COUNT) {
            Log.d(TAG,"启动老化测试");
            launcherActivity("com.xthink.factorytest", "com.xthink.factorytest.main_runin");
            finish();
        }
    }

    private void launcherActivity(String packageName, String classNmae) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName comp = new ComponentName(packageName, classNmae);
        intent.setComponent(comp);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
        startActivity(intent);
    }

    private boolean isApkExist(String packageName){
        PackageManager pm = getPackageManager();
        PackageInfo pInfo = null;
        try{
            pInfo = pm.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
        }catch(PackageManager.NameNotFoundException e){
            Log.e(TAG,packageName + " not found..");
            return false;
        }catch(Exception xe){
            return false;
        }
        return true;
    }
}
