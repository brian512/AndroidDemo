
package com.brian.common.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志管理
 */
public class JDLog {
    private static final String TAG = JDLog.class.getSimpleName();
    private static final boolean PRINT_LOG = true;



    private static String FILTER = "Rokkapp";

    public static void setLogFilter(String filter) {
        FILTER = filter;
    }

    /**
     * 一般log
     */
    public static void log(String tag, String content) {
        if (content == null) {
            return;
        }
        if (PRINT_LOG) {
            if (TextUtils.isEmpty(tag)) {
                log(content);
            } else {
                Log.d(tag, buildMessageSafe(content));
            }
        }
    }

    public static void log(String content) {
        if (PRINT_LOG) {
            Log.i(getTag(), buildMessageSafe(content));
        }
    }

    public static void log(double value) {
        if (PRINT_LOG) {
            Log.i(getTag(), buildMessageSafe(value));
        }
    }

    public static void w(String content) {
        Log.w(getTag(), buildMessageSafe(content));
    }

    public static void printError(Throwable throwable) {
        if (PRINT_LOG) {
            throwable.printStackTrace();
        }
    }

    public static void log(String content, Object... obj) {
        log(String.format(content, obj));
    }

    /**
     * 错误log
     */
    public static void logError(String tag, String content) {
        if (tag == null || content == null) {
            return;
        }

        if (PRINT_LOG) {
            Log.e(tag, buildMessageSafe(content));
        }
    }

    public static void logError(String content) {
        if (content == null) {
            return;
        }
        if (PRINT_LOG) {
            Log.e(getTag(), buildMessageSafe(content));
        }
    }

    /**
     * 将日志输出到文件
     */
    private static FileOutputStream logFile = null;
    private static long mLogTime;


    /**
     * 辅助函数：获取当前时间
     */
    public static String getMillTimeEx() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
        return format.format(new Date());
    }

    /**
     * 辅助函数：获取当前时间
     */
    public static String getDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US);
        return format.format(new Date());
    }

    /**
     * 标记开始时间
     * PS:配套logTimeDur使用
     */
    public static void tagTimeStart() {
        mLogTime = System.currentTimeMillis();
    }
    
    /**
     * 打印标记开始到现在的时间间隔
     * PS：配套tagTimeStart使用
     */
    public static void logTimeDur(String tag) {
        log(tag, "Consume ms time:" + (System.currentTimeMillis() - mLogTime));
    }
    
    /**
     * 用来调试时间间隔
     */
    public static void logTime(String log, long time) {
        log("Time", log + ": " + (time - mLogTime));
        mLogTime = time;
    }

    private static String buildMessageSafe(double value) {
        String msg = String.format(Locale.ENGLISH, "%10f", value);
        try {
            return buildMessage(msg);
        } catch (Exception e) {
        }
        return msg;
    }

    private static String buildMessageSafe(String msg) {
        try {
            return buildMessage(msg);
        } catch (Exception e) {
        }
        return msg;
    }

    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "";
        for (int i = 3; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(JDLog.class)) {
                caller = "(" + trace[i].getFileName() + ":" + trace[i].getLineNumber() + ") "
                        + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, FILTER + " [%d] %s: %s", Thread.currentThread()
                .getId(), caller, msg==null?"":msg);
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String clssName = "";
        for (int i = 3; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(JDLog.class)) {
                clssName = trace[i].getClassName();
                clssName = clssName.substring(clssName.lastIndexOf('.') + 1);
                break;
            }
        }
        return clssName;
    }
    
    /**
     * 显示方法的调用轨迹，过滤关键字“showCallStack”
     */
    public static void showCallStack() {
        if (!PRINT_LOG) {
            return;
        }
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        StringBuilder builder = new StringBuilder();
        String temp = "";
        for (int i = trace.length - 1; i > 0; i--) {
            temp = trace[i].getClassName();
            temp = temp.substring(temp.lastIndexOf('.') + 1);
            builder.append(temp + "." + trace[i].getMethodName() + "()(L" + trace[i].getLineNumber() + ")");
            builder.append(" ——> ");
        }
        builder.append(" ——> " + TAG + ".showCallStack()");
        Log.e(TAG, "showCallStack:" + builder.toString());
    }
}
