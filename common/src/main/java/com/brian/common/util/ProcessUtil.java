package com.brian.common.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import java.util.List;

/**
 * 进程管理相关的类
 * @author lipeilong
 *
 */
public class ProcessUtil {

    private static int mgprocessId = 0;
    
    /**
     * 获取进程名
     * PS:异常情况反馈空字符串
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcessInfoList = mActivityManager.getRunningAppProcesses();
        if (appProcessInfoList == null || appProcessInfoList.size() <= 0) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessInfoList) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }


    /**
     * 判断进程是否存在
     *
     * @param context
     * @return
     */
    public static boolean isExistProcessName(Context context, String processName) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.processName.equals(processName)) {
                return  true;
            }
        }
        return false;
    }
    
  
    
    

    /**
     * 根据进程名获取进程id
     * @param con
     * @return
     */
    public static int getProcessId(Context con, String processName) {
        if (mgprocessId == 0) {
            ActivityManager am = (ActivityManager) con.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> apps = am.getRunningAppProcesses();// 返回进程列表信息
            for (RunningAppProcessInfo p : apps) {
                if (p.processName.equals(processName)) {
                    mgprocessId = p.pid;
                    break;
                }
            }
        }
        return mgprocessId;
    }
    
}
