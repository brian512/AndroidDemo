
package com.brian.common.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

import java.util.List;

/**
 * 对Activity基本判断的工具
 */
public class ActivityUtil {

    /**
     * 判断程序是否在前台.
     */
    public static boolean isAppInForeground(Context context) {
        boolean result = false;
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> task_list = activityManager.getRunningTasks(1);
        if (task_list.size() > 0) {
            if (task_list.get(0).topActivity.getPackageName().trim()
                    .equals(packageName)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 判断程序是否在前台（遍历判断RunningAppProcessInfo.importance）
     */
    public static boolean isAppInForegroundByImportance(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null) {
            String package_name = ctx.getPackageName();
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(package_name)
                        && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测该Context是否在栈顶
     *
     * @param activity
     * @return
     */
    public static boolean isTop(Context activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(Integer.MAX_VALUE);
        if (runningTasks != null && runningTasks.size() > 0) {
            RunningTaskInfo taskInfo = runningTasks.get(0);
            String temp = taskInfo.topActivity.getClassName();
            if (activity.getClass().getName().equals(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测页面是否在前台
     *
     * @param ctx
     * @param cls
     * @return
     */
    public static boolean isActivityForeground(Context ctx, Class<?> cls) {
        boolean result = false;
        String clsName = cls.getName();
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> task_list = activityManager.getRunningTasks(1);
        if (task_list.size() > 0) {
            String topName = task_list.get(0).topActivity.getClassName();
            if (clsName.equals(topName)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 判断指定程序是否在前台（遍历判断RunningAppProcessInfo.importance）
     */
    public static boolean isSpecAppInForeground(Context ctx, final String specAppName) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null) {
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(specAppName)
                        && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

}
