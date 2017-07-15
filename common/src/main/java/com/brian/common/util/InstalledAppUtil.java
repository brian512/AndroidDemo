package com.brian.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 判断应用是否能安装
 * Created by huamm on 2017/4/25 0025.
 */

public class InstalledAppUtil {

    public static final String PKG_NAME_QQ = "com.tencent.mobileqq";
    public static final String PKG_NAME_QQ_LITE = "com.tencent.qqlite";
    public static final String PKG_NAME_QQ_HD = "com.tencent.minihd.qq";
    public static final String PKG_NAME_QZONE = "com.qzone";
    public static final String PKG_NAME_WEIXIN = "com.tencent.mm";
    public static final String PKG_NAME_WEIBO = "com.sina.weibo";
    public static final String PKG_NAME_ZHIFUBAO = "com.eg.android.AlipayGphone";
    public static final String PKG_NAME_FACEBOOK = "com.facebook.katana";
    public static final String PKG_NAME_MESSENGER = "com.facebook.orca";
    public static final String PKG_NAME_TWITTER = "com.twitter.android";
    public static final String PKG_NAME_INSTAGRAM = "com.instagram.android";
    public static final String PKG_NAME_LINE = "jp.naver.line.android";
    public static final String PKG_NAME_YOUTUBE = "com.google.android.youtube";
    public static final String PKG_NAME_UCBROWSER = "com.uc.browser";
    public static final String PKG_NAME_360WEISHI = "com.qihoo360.mobilesafe";
    public static final String PKG_NAME_RENRENWANG = "com.renren.mobile.android";
    public static final String PKG_NAME_TTPOD = "com.sds.android.ttpod";
    public static final String PKG_NAME_JD = "com.jingdong.app.mall";
    public static final String PKG_NAME_TMALL = "com.tmall.wireless";
    public static final String PKG_NAME_TAOBAO = "com.taobao.taobao";

    public static boolean isQQInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_QQ);
    }

    public static boolean isWeixinInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_WEIXIN);
    }

    public static boolean isWeiboInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_WEIBO);
    }

    public static boolean isZhifubaoInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_ZHIFUBAO);
    }

    public static boolean isFacebookInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_FACEBOOK);
    }

    public static boolean isMessengerInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_MESSENGER);
    }

    public static boolean isTwitterInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_TWITTER);
    }

    public static boolean isInstagramInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_INSTAGRAM);
    }

    public static boolean isLineInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_LINE);
    }

    public static boolean isYoutubeInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_YOUTUBE);
    }

    public static boolean isUCBrowserInstalled(Context context) {
        return isAppInstalled(context, PKG_NAME_UCBROWSER);
    }

    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        }catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            JDLog.printError(e);
        }
        if(packageInfo ==null){
            return false;
        }else{
            return true;
        }
    }
}
