package com.brian.common.util;

import android.os.Looper;

/**
 * 线程相关工具
 * Created by huamm on 2016/11/29 0029.
 */

public class ThreadUtil {

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {}
    }
}
