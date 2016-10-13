package com.brian.testandroid.common;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import java.util.List;


/**
 * 用于获取环境变量
 */
public class Env {

    public static final String TAG = "Env";
    
    
    /**
     * 程序上下文
     */
    private static Context sContext;

    
    /**
     * 设置当前程序的上下文，需要在application启动的时候立马设置
     */
    public static void setContext(Context context) {
        sContext = context;
    }

    
    /**
     * 获取当前程序的上下文
     */
    public static Context getContext() {
        return sContext;
    }
    
    
    /**
     * 获取当前创建的进程名称，区分主进程和消息推送进程
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> listInfo = am.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : listInfo) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    
    /**
     * 记录程序启动时间，在application创建的时候统一设置
     */
    private static long sAppStartTime = 0;
    public static void setAppStartTime() {
        sAppStartTime = System.currentTimeMillis();
    }

    /**
     * 获取程序当前运行时间点
     */
    public static long getAppRunTime() {
        return System.currentTimeMillis() - sAppStartTime;
    }
}
