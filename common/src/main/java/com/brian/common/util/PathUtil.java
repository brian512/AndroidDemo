package com.brian.common.util;

import android.content.Context;
import android.text.TextUtils;

import com.brian.common.Env;

import java.io.File;

/**
 * 路径相关工具函数
 * <p>
 * PS:注意程序中所有出现的路径，统一结尾都不带 "/"
 *
 * @author ls
 */
public class PathUtil {

    private static String sCacheDirPath;
    private static String sCacheDataPath;

    /**
     * 获得cache的根目录
     */
    public static String getCacheDir() {
        if (!TextUtils.isEmpty(sCacheDirPath)) {
            return sCacheDirPath;
        }
        Context context = Env.getContext();

        // 先尝试选择外部缓存
        File file = context.getExternalCacheDir();
        if (file != null) {
            String extCacheDir = file.getAbsolutePath();
            FileUtil.ensureDir(extCacheDir);
            if (FileUtil.isDirExist(extCacheDir)) {
                sCacheDirPath = extCacheDir;
                return sCacheDirPath;
            }
        }

        // 暂尝试选择内部缓存
        String intcacheDir = context.getCacheDir().getAbsolutePath();
        FileUtil.ensureDir(intcacheDir);
        sCacheDirPath = intcacheDir;
        return sCacheDirPath;

        // PS: obb用于存放应用下载回来的数据包，这里不使用
        //String obbDir = context.getObbDir().getAbsolutePath();
    }

    /**
     * 获得data的根目录
     */
    public static String getDataDir() {
        if (!TextUtils.isEmpty(sCacheDataPath)) {
            return sCacheDataPath;
        }
        Context context = Env.getContext();

        // 先尝试选择外部缓存
        File file = context.getExternalFilesDir("files");
        if (file != null) {
            String extDataDir = file.getAbsolutePath();
            FileUtil.ensureDir(extDataDir);
            if (FileUtil.isDirExist(extDataDir)) {
                sCacheDataPath = extDataDir;
                return extDataDir;
            }
        }

        // 暂尝试选择内部缓存
        String intFileDir = context.getFilesDir().getAbsolutePath();
        FileUtil.ensureDir(intFileDir);
        sCacheDataPath = intFileDir;
        return intFileDir;

        // PS: obb用于存放应用下载回来的数据包，这里不使用
        //String obbDir = context.getObbDir().getAbsolutePath();
    }
}
