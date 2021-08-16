/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 服务权限工具
 *
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/5/23
 */
public class ServiceAccessUtils {

    private final static android.text.TextUtils.SimpleStringSplitter sStringColonSplitter =
            new android.text.TextUtils.SimpleStringSplitter(':');

    /**
     * 判断辅助服务是否有权限
     *
     * @param context 上下文
     * @param clazz   辅助服务类
     */
    public static boolean isAccessibilityEnable(Context context, Class clazz) {
        int userId = getMyUserId();
        Set<ComponentName> enabledServices = getEnabledServicesForUser(
                context, userId, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        ComponentName toggledService = new ComponentName(context.getPackageName(), clazz.getName());
        return enabledServices.contains(toggledService);
    }

    /**
     * 设置通知栏服务开启
     *
     * @param context 上下文
     * @param clazz   服务的类名
     */
    public static void setNotificationServiceEnable(Context context, Class clazz) {

        final String serviceKey = "enabled_notification_listeners";

        // 找到所有的开启的服务
        Set<ComponentName> enabledServices = getEnabledServices(
                context, serviceKey);

        // 创建要开启的服务信息
        ComponentName toggledService = new ComponentName(context.getPackageName(), clazz.getName());

        // 将要开启的服务加入到所有已经开启的服务字符串中。
        if (enabledServices.isEmpty()) {
            enabledServices = new HashSet<>();
        }
        enabledServices.add(toggledService);
        StringBuilder sb = null;
        // 拼接
        for (ComponentName cn : enabledServices) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(':');
            }
            sb.append(cn.flattenToString());
        }

        // 手动修改设置，需要system uid的权限
        try {
            Settings.Secure.putString(context.getContentResolver(), serviceKey,
                    sb != null ? sb.toString() : "");
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置辅助服务开启与否
     *
     * @param context 上下文
     * @param clazz   辅助服务类
     * @param enabled 开启or关闭
     */
    public static void setAccessibilityEnable(Context context, Class clazz, boolean enabled) {

        int userId = getMyUserId();

        Set<ComponentName> enabledServices = getEnabledServicesForUser(
                context, userId, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        ComponentName toggledService = new ComponentName(context.getPackageName(), clazz.getName());

        if (enabledServices.isEmpty()) {
            enabledServices = new HashSet<>();
        }

        // Determine enabled services and accessibility state.
        boolean accessibilityEnabled = false;
        if (enabled) {
            enabledServices.add(toggledService);
            // Enabling at least one service enables accessibility.
            accessibilityEnabled = true;
        } else {
            enabledServices.remove(toggledService);
            // Check how many enabled and installed services are present.
            Set<ComponentName> installedServices = getInstalledServices(context);
            for (ComponentName enabledService : enabledServices) {
                if (installedServices.contains(enabledService)) {
                    // Disabling the last service disables accessibility.
                    accessibilityEnabled = true;
                    break;
                }
            }
        }

        // Update the enabled services setting.
        StringBuilder enabledServicesBuilder = new StringBuilder();
        // Keep the enabled services even if they are not installed since we
        // have no way to know whether the application restore process has
        // completed. In general the system should be responsible for the
        // clean up not settings.
        for (ComponentName enabledService : enabledServices) {
            enabledServicesBuilder.append(enabledService.flattenToString());
            enabledServicesBuilder.append(
                    ':');
        }
        final int enabledServicesBuilderLength = enabledServicesBuilder.length();
        if (enabledServicesBuilderLength > 0) {
            enabledServicesBuilder.deleteCharAt(enabledServicesBuilderLength - 1);
        }
        try {
            putStringForUser(context,
                    enabledServicesBuilder.toString(), userId, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    /**
     * 得到有权限的Service
     *
     * @param context 上下文
     * @param userId  userid
     * @return 辅助服务
     */
    public static Set<ComponentName> getEnabledServicesForUser(Context context, int userId, String serviceKey) {
        final String enabledServicesSetting = getStringForUser(context, userId, serviceKey);
        if (enabledServicesSetting == null) {
            return Collections.emptySet();
        }

        final Set<ComponentName> enabledServices = new HashSet<>();
        final android.text.TextUtils.SimpleStringSplitter colonSplitter = sStringColonSplitter;
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            final String componentNameString = colonSplitter.next();
            final ComponentName enabledService = ComponentName.unflattenFromString(
                    componentNameString);
            if (enabledService != null) {
                enabledServices.add(enabledService);
            }
        }

        return enabledServices;
    }


    private static Set<ComponentName> getEnabledServices(Context context, String serviceKey) {
        final Set<ComponentName> enabledServices = new HashSet<>();
        final String flat = Settings.Secure.getString(context.getContentResolver(), serviceKey);
        if (flat != null && !"".equals(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    enabledServices.add(cn);
                }
            }
        }
        return enabledServices;
    }

    private static Set<ComponentName> getInstalledServices(Context context) {
        final Set<ComponentName> installedServices = new HashSet<>();
        installedServices.clear();

        AccessibilityManager accessibilityManager = getAccessibilityManager(context);
        if (accessibilityManager == null) {
            return installedServices;
        }

        final List<AccessibilityServiceInfo> installedServiceInfos =
                accessibilityManager.getInstalledAccessibilityServiceList();
        if (installedServiceInfos == null) {
            return installedServices;
        }

        for (final AccessibilityServiceInfo info : installedServiceInfos) {
            final ResolveInfo resolveInfo = info.getResolveInfo();
            final ComponentName installedService = new ComponentName(
                    resolveInfo.serviceInfo.packageName,
                    resolveInfo.serviceInfo.name);
            installedServices.add(installedService);
        }
        return installedServices;
    }

    private static AccessibilityManager getAccessibilityManager(Context context) {

        try {
            Method method;
            method = AccessibilityManager.class.getDeclaredMethod("getInstance", Context.class);
            return (AccessibilityManager) method.invoke(AccessibilityManager.class, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getStringForUser(Context context, int userId, String serviceKey) {

        //ContentResolver resolver, String name, int userHandle

        try {
            Method method = Settings.Secure.class.getDeclaredMethod("getStringForUser",
                    ContentResolver.class, String.class, int.class);
            return (String) method.invoke(Settings.Secure.class, context.getContentResolver(), serviceKey,
                    userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void putStringForUser(Context context, String toPutService, int userId, String serviceKey) {
        try {
            Method method = Settings.Secure.class.getDeclaredMethod("putStringForUser",
                    ContentResolver.class, String.class, String.class, int.class);
            method.invoke(Settings.Secure.class, context.getContentResolver(),
                    serviceKey,
                    toPutService, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getMyUserId() {
        Method method = null;
        try {
            method = UserHandle.class.getDeclaredMethod("myUserId");
            Integer userId = (Integer) method.invoke(UserHandle.class);
            if (userId != null) {
                return userId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

