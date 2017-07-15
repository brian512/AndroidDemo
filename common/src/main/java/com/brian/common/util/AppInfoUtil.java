package com.brian.common.util;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.brian.common.Env;

import java.util.List;
import java.util.Locale;


/**
 * 获取当前应用的基本信息
 * PS：渠道号、产品ID需要用到values\package_config.xml
 * PS：因为这个类与业务有一点关系，做好后续可以再调整一下位置
 */
public class AppInfoUtil {

    public static final String URL_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=com.joyodream.rokk";
    public static final String URL_DOWNLOAD_APK = "https://s3-ap-southeast-1.amazonaws.com/rokkapk/rokk_app.apk";
    public static final String URL_DOWNLOAD_APK_ZH = "http://qpimage.impingo.me/RokkAPP.apk";

    /**
     * 获取客户端版本号
     */
    private static String sVersion;

    public static String getVersion(Context context) {
        // 只获取一次
        if (sVersion == null) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pinfo = pm.getPackageInfo(getPackageName(context), PackageManager.GET_CONFIGURATIONS);
                sVersion = pinfo.versionName;
                sVersionCode = pinfo.versionCode;
            } catch (NameNotFoundException e) {
                sVersion = "1.0.0.0"; // 异常情况
            }
        }
        return sVersion;
    }

    /**
     * 获取客户端版本号
     */
    private static int sVersionCode;

    public static int getVersionCode(Context context) {
        // 只获取一次
        if (sVersionCode == 0) {
            try {
                PackageManager pm = context.getPackageManager();
                String packageName = context.getPackageName();
                PackageInfo pinfo = pm.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
                sVersion = pinfo.versionName;
                sVersionCode = pinfo.versionCode;
            } catch (NameNotFoundException e) {
                sVersionCode = 0; // 异常情况
            }
        }
        return sVersionCode;
    }



    /**
     * 获取当前系统的语言,默认是英语
     */
    private static String sLanguage;

    public static String getLanguage() {
        if (sLanguage == null) {
            Locale locale = Env.getContext().getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if (TextUtils.isEmpty(language)) {
                sLanguage = "en";
            } else {
                sLanguage = language;
            }
        }
        return sLanguage;
    }

    public static boolean isChinese() {
        return sIsChinese;
    }

    /**
     * 获得packageName
     */
    public static String sPackageName;

    public static String getPackageName(Context context) {
        if (TextUtils.isEmpty(sPackageName)) {
            sPackageName = context.getPackageName();
        }
        return sPackageName;
    }


    /**
     * 判断app是否安装在手机上
     */
    public static boolean isAppInstall(String appName){

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("video/*");//"image/*");
        boolean installed = false;

        // 得到所有能处理ACTION_SEND的应用程序包名
        List<ResolveInfo> resInfo = Env.getContext().getPackageManager().queryIntentActivities(share, 0);

        if (resInfo != null && !resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(appName) || info.activityInfo.name.toLowerCase().contains(appName)) {
                    installed =  true;
                }
            }
        } else {
            JDLog.logError("can't get app list");
        }

        return installed;
    }

    /**
     * 获取应用名称
     */
    public static String sAppName;

    public static String getAppName(Context context) {
        if (sAppName == null) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(context), 0);
                sAppName = (String) packageManager.getApplicationLabel(applicationInfo);
            } catch (PackageManager.NameNotFoundException e) {
                JDLog.printError(e);
            }
        }
        return sAppName;
}

    /**
     * 客户端渠道名称
     */
    public static String sChannelName;

    /**
     * 获取客户端渠道号
     */
    public static String sChannelID;

    /**
     * 获取客户端产品号
     */
    public static String sProductID;

    /**
     * 是否为中文环境
     */
    public static boolean sIsChinese = false;
}
