package com.brian.testandroid.util;

import android.content.Context;

import com.brian.testandroid.common.Env;

import java.io.File;

/**
 * 路径相关工具函数
 * 
 * PS:注意程序中所有出现的路径，统一结尾都不带 "/"
 */
public class PathUtil {
    /**
     * 获得cache的根目录
     */
    public static String getCacheDir(){
        Context context = Env.getContext();
        
        // 先尝试选择外部缓存
        File file = context.getExternalCacheDir();
        if (file != null) {
            String extCacheDir = file.getAbsolutePath();
            FileUtil.ensureFolderExists(extCacheDir);
            if (FileUtil.isDirExist(extCacheDir)) {
                return extCacheDir;
            }
        }
        
        // 暂尝试选择内部缓存
        String intcacheDir = context.getCacheDir().getAbsolutePath();
        FileUtil.ensureFolderExists(intcacheDir);
        return intcacheDir;
        
        // PS: obb用于存放应用下载回来的数据包，这里不使用
        //String obbDir = context.getObbDir().getAbsolutePath();
    }
}
