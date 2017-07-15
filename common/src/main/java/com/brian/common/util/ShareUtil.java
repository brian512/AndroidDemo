package com.brian.common.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;

/**
 * 系统原生分享
 * Created by huamm on 2017/3/23 0023.
 */
public class ShareUtil {

    /**
     * 分享文字
     */
    public static boolean shareText(Context context, @NonNull String content) {
        return share(context, "text/plain", null, null, content, null);
    }

    public static boolean shareText(Context context, String title, @NonNull String content, String subject) {
        return share(context, "text/plain", null, title, content, subject);
    }

    /**
     * 分享图片
     */
    public static boolean shareImg(Context context, File file) {
        return share(context, "image/*", file);
    }

    public static boolean shareImg(Context context, File file, String content) {
        return share(context, "image/*;text/plain", file, null, content, null);
    }

    /**
     * 分享音乐
     */
    public static boolean shareAudio(Context context, File file) {
        return share(context, "audio/*", file);
    }

    public static boolean shareAudio(Context context, File file, String content) {
        return share(context, "audio/*;text/plain", file, null, content, null);
    }

    /**
     * 分享视频
     */
    public static boolean shareVideo(Context context, File file) {
        return share(context, "video/*", file);
    }

    public static boolean shareVideo(Context context, File file, String content) {
        return share(context, "video/*;text/plain", file, null, content, null);
    }

    public static boolean share(Context context, String type, File file) {
        if (!file.exists()) {
            return false;
        }
        return share(context, type, file, null, null, null);
    }

    public static boolean share2Weibo(Context context, String type, String textContent, File file) {
        if (!file.exists()) {
            return false;
        }
        return share(context, type, file, null, textContent, null, InstalledAppUtil.PKG_NAME_WEIBO);
    }

    public static boolean share2Weixin(Context context, String type, File file) {
        if (!file.exists()) {
            return false;
        }
        return share(context, type, file, null, null, null, InstalledAppUtil.PKG_NAME_WEIXIN);
    }

    public static boolean share2QQ(Context context, String type, File file) {
        if (!file.exists()) {
            return false;
        }
        return share(context, type, file, null, null, null, InstalledAppUtil.PKG_NAME_QQ);
    }

    public static boolean share2Qzone(Context context, String type, String textContent, File file) {
        if (!file.exists()) {
            return false;
        }
        return share(context, type, file, null, textContent, null, InstalledAppUtil.PKG_NAME_QZONE);
    }

    public static boolean share(Context context, String type, File file, String title, String content, String subject) {
        return share(context, type, file, title, content, subject, "");
    }

    public static boolean share(Context context, String type, File file, String title, String content, String subject, String pkgName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(type);

        int flag = 0;
        if (file != null && file.exists()) {
            Uri contentUri = Uri.fromFile(file);
//            Uri contentUri = FileProvider.getUriForFile(context, "com.joyodream.rokk.fileprovider", file);
            JDLog.log("contentUri=" + contentUri);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            flag |= 0x01;
        }

        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
            flag |= 0x10;
        }

        if (flag == 0) {
            throw new IllegalArgumentException("content and file can't be null at the same time!");
        }

        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(Intent.EXTRA_TITLE, title);
        }
        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        if (!TextUtils.isEmpty(pkgName)) {
            intent.setPackage(pkgName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            JDLog.printError(e);
        }
        return true;
    }

    /**
     * 分享多张图片和文字至朋友圈
     */
    public static void shareToWXCircle(Context context, String title, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        int flag = 0;
        if (file != null && file.exists()) {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            flag |= 0x01;
        }
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra("Kdescription", title);
            flag |= 0x10;
        }

        doShare(context, intent, flag, "");
    }

    private static void doShare(Context context, Intent intent, int flag, String pkgName) {
        if (flag == 0x01) {
            intent.setType("image/*");
        } else if (flag == 0x10) {
            intent.setType("text/plain");
        } else if (flag == 0x11) {
            intent.setType("image/*;text/plain");
        } else {
            throw new IllegalArgumentException("title and file can't be null at the same time!");
        }
        if (!TextUtils.isEmpty(pkgName)) {
            intent.setPackage(pkgName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转官方安装网址
     */
    public static void openUrlWithDefaultApp(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}