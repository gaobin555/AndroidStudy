package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.xthink.ink.launcherink.BuildConfig;
import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.bean.PluginInfoBean;
import com.android.xthink.ink.launcherink.common.utils.ApplicationUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 插件Fragment
 * Created by liyuyan on 2016/12/22.
 */

public class PluginFragment extends BasePagerFragment {

    public static final String mPageName = "PluginFragment";
    private static final String TAG = "FragmentLife";
    private static final String KEY_PLUGIN_INFO = "key_plugin_info";
    public ViewGroup mContainerView;
    private View mPluginView;
    private PluginInfoBean mPluginInfoBean;
    private Context mPluginContext;

    public static PluginFragment newInstance(@NonNull PluginInfoBean pluginInfoBean) {
        MyLog.d(TAG, "newInstance" + pluginInfoBean.getPackageName());
        Bundle args = new Bundle();
        args.putParcelable(KEY_PLUGIN_INFO, pluginInfoBean);

        PluginFragment fragment = new PluginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        mContainerView = (ViewGroup) inflater.inflate(R.layout.fragment_reader, container, false);
        return mContainerView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mPluginInfoBean = arguments == null ? null : (PluginInfoBean) arguments.getParcelable(KEY_PLUGIN_INFO);
        if (mPluginInfoBean == null) {
            throw new IllegalArgumentException("You should use the static factory method newInstance to pass a PluginInfoBean param");
        }
        MyLog.d(TAG, "onCreate " + mPluginInfoBean.getPackageName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.d(TAG, "onDestroy " + mPluginInfoBean.getPackageName());
        callMethod(mPluginView, "onDestroy");
    }

    @Override
    protected void initData() {

    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        // 正面A屏暂时不加载插件
        if (BuildConfig.FLAVOR.equals("dev")) {
            return;
        }
        loadPlugin(mPluginInfoBean.getPackageName(), mPluginInfoBean.getLayoutName());
    }

    @Override
    public void switchPage(boolean isVisibleToUser) {
        super.switchPage(isVisibleToUser);
        if (isVisibleToUser) {
            MyLog.d(TAG, "switchPage:" + mPluginInfoBean.getPackageName());
            callMethod(mPluginView, "switchToThisPage");
        } else {
            callMethod(mPluginView, "switchOffThisPage");
        }

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        MyLog.i(TAG, "onConfigurationChanged");
        keepFontSize(mPluginContext);
        super.onConfigurationChanged(newConfig);
    }

    private void loadPlugin(String packageName, String layoutName) {
        MyLog.i(TAG, "loadPlugin begin: " + packageName);
        long beginTime = System.currentTimeMillis();
        Context parentContext = getContext();
        View v = null;
        try {
            mPluginContext = parentContext.createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

            keepDensitySize(mPluginContext);
            keepFontSize(mPluginContext);

            ApplicationUtils.makePluginApplication(mPluginContext);     //目前该加载插件耗时6百多ms以上,减少时间的方式目前还没找到

            int id = mPluginContext.getResources().getIdentifier(layoutName, "layout", packageName);
            v = LayoutInflater.from(mPluginContext).inflate(id, mContainerView, false);

            setActivity(mPluginContext, getActivity());
            mPluginView = v;
            v.setTag(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyLog.i(TAG, "loadPlugin end: " + packageName + ",耗时:" + (System.currentTimeMillis() - beginTime));

        if (v != null) {
            mContainerView.addView(v);
            callMethod(mPluginView, "onAdd");
            return;
        }
        Toast.makeText(parentContext.getApplicationContext(), "加载插件失败", Toast.LENGTH_SHORT).show();
    }

    /**
     * 反射将Activity加入到context的mOuterContext中。
     * 注意：
     * 1.如果在inflate插件之前调用此方法，可能将导致插件资源文件错乱。所以等inflate完成之后，加入到宿主之前，注入Activity。
     * 2.由于第一点，导致，在插件中，构造方法中的context不包含Activity。onAttachedToWindow回调中context就包含Activity了。
     * 3.目前测试没有什么问题，测试时间较短，不知道会不会导致其他问题发生。
     *
     * @param context  这是一个ContextImpl对象，待注入Activity
     * @param activity 这是待注入的Activity。
     */
    private void setActivity(Context context, Activity activity) {
        try {
            Class contextImplClass = Class.forName("android.app.ContextImpl");
            Field outerContextField = contextImplClass.getDeclaredField("mOuterContext");
            outerContextField.setAccessible(true);
            outerContextField.set(context, activity);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getPageId() {
        if (mPluginInfoBean != null) {
            return mPluginInfoBean.getId();
        } else {
            MyLog.d("pageId", super.getPageId() + "");
            return super.getPageId();
        }
    }

    private void callMethod(Object object, String methodName) {
        if (object == null) {
            return;
        }
        MyLog.i(TAG, "callMethod: 00000000000" + methodName);
        long beginTime = System.currentTimeMillis();
        try {
            Method d = object.getClass().getMethod(methodName);
            d.invoke(object);
        } catch (Exception e) {
//            e.printStackTrace();
            MyLog.i(TAG, "plugin no method:" + methodName);
        }
        MyLog.i(TAG, "plugin method end:" + methodName + ",耗时:" + (System.currentTimeMillis() - beginTime));
    }

    @Override
    public void onResume() {
        super.onResume();
        callMethod(mPluginView, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        callMethod(mPluginView, "onPause");
    }

/*    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.onFragmentResume();
        } else {
            this.onFragmentPause();
        }
    }*/

    @Override
    public void onStop() {
        super.onStop();
        callMethod(mPluginView, "onStop");
    }

    /**
     * 用于App统计
     */

    public void onFragmentResume(Context context) {
        MyLog.d(TAG, "cy--=fragment onResume=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
        /**
         * BaoliYota begin, add
         * what(reason) App统计 CY统计
         * liuwenrong, 1.0, 2017/7/6 */
        if (mPluginInfoBean != null) {
//            CYAnalysis.onResume(context, mPageName + "_" + mPluginInfoBean.getName());
        } else {
//            CYAnalysis.onResume(context, mPageName + "_");
        }
    }

    public void onFragmentPause(Context context) {
        MyLog.d(TAG, "cy--=fragment onPause=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
        /**
         * BaoliYota begin, add
         * what(reason) App统计 CY统计
         * liuwenrong, 1.0, 2017/7/6 */
//        CYAnalysis.onPause(context);
    }

    private void keepFontSize(Context context) {
        if (context == null) {
            return;
        }
        //update resource
        Resources res = context.getResources();

        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
    }

    private void keepDensitySize(Context context) {
        if (context == null) {
            return;
        }
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        float density = dm.density;
        if (density != 2.0f) {
            DisplayMetrics normalDisplay = getContext().getResources().getDisplayMetrics();
            dm.setTo(normalDisplay);
        }
    }
}
