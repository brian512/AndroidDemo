package com.brian.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 应用相关的工具类
 * 说明：目前功能还比较简单，只提供安装功能，后续可完善
 */
public class ApkUtil {

    /**
     * 安装APK应用
     * @param context 原则上必须是Activity是context，现在使用FLAG_ACTIVITY_NEW_TASK，就无所谓了
     * @param apkPath
     * @return
     */
    public static void installApk(Context context, final String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // PS：加这行，保证程序不管是否在前台，安装应用都能弹出来
        context.startActivity(intent);  
        
    }
}
