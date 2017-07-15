package com.brian.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;


/**
 * 用于创建快捷方式的工具类
 */
public class ShortcutUtil {
    /**
     * 快捷方式表单名
     */
    private static final String PREFER_NAME         = "InstallShortcut";
    
    /**
     * 是否安装了快捷方式
     */
    private static final String ATTR_NAME_INSTALL   = "install";
    
    
    /**
     * 添加程序快捷方式（只添加一次）
     * @param context       context
     * @param appName       应用名
     * @param iconResId     图标的资源ID
     * @param packageName   程序的packageName
     * @param loadingActivityClassName  启动页面的类名
     */
    public static void installShortcut(Context context, String appName, int iconResId,
                                       String packageName, String loadingActivityClassName) {

        if (!isInstallShotcut(context)) {

            Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconResId);

            // 设置快捷方式的目标intent
            Intent desIntent = new Intent();
            desIntent.setAction("android.intent.action.MAIN");
            desIntent.addCategory("android.intent.category.LAUNCHER");
            desIntent.setClassName(packageName, loadingActivityClassName);
            desIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

            // 添加快捷方式
            Intent installIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            installIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, desIntent);
            installIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
            installIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            installIntent.putExtra("duplicate", false); // 禁止重复添加，否则用户清空数据之后，再运行会出现两个图标
            context.sendBroadcast(installIntent);

            // 记录已经添加过快捷方式
            SharedPreferences settings = context.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(ATTR_NAME_INSTALL, true);
            editor.apply();
        }
    }
    
    /**
     * 删除快捷方式
     * @param context
     */
    public static void uninstallShortcut(Context context, String appName, String packageName, String loadingActivityClassName) {
        
        // 设置快捷方式的目标intent
        Intent desIntent = new Intent();
        desIntent.setAction("android.intent.action.MAIN");
        desIntent.addCategory("android.intent.category.LAUNCHER");
        desIntent.setClassName(packageName, loadingActivityClassName);
        desIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        
        // 添加快捷方式
        Intent installIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        installIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, desIntent);
        installIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        context.sendBroadcast(installIntent);
        
        // 记录已经添加过快捷方式
        SharedPreferences settings = context.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(ATTR_NAME_INSTALL, false);
        editor.apply();
    }
    
        
    /**
     * 是否安装了快捷方式
     * 
     * @param context
     * @return
     */
    public static boolean isInstallShotcut(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
        return settings.getBoolean(ATTR_NAME_INSTALL, false);
    }
}
