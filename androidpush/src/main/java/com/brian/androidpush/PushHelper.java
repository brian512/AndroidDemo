package com.brian.androidpush;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.brian.androidpush.getui.GeTuiIntentService;
import com.brian.androidpush.getui.GeTuiPushService;
import com.brian.androidpush.utils.LogUtil;
import com.brian.androidpush.utils.RomUtil;
import com.brian.androidpush.xiaomi.MiuiPushMessageReceiver;
import com.igexin.sdk.PushManager;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * 消息推送辅助类
 * 根据ROM类型选择相应的推送平台
 * Created by brian on 2017/5/25.
 */

public class PushHelper {

    private static final String MIUI_APP_ID = "2882303761517580240";
    private static final String MIUI_APP_KEY = " 5951758090240";


    public static void register(Context context, boolean debug, PushInterface pushInterface) {

        LogUtil.log("RomUtil.rom()=" + RomUtil.rom());
        if (RomUtil.rom() == RomUtil.Target.MIUI) {
            if (pushInterface != null) {
                MiuiPushMessageReceiver.registerInterface(pushInterface);
            }
            if (shouldInit(context)) {
                MiPushClient.registerPush(context.getApplicationContext(), MIUI_APP_ID, MIUI_APP_KEY);
            }
            if (debug) {
                LoggerInterface newLogger = new LoggerInterface() {
                    @Override
                    public void setTag(String tag) {
                        // ignore
                    }

                    @Override
                    public void log(String content, Throwable t) {
                        LogUtil.i("content" + content + " exception: " + t.toString());
                    }

                    @Override
                    public void log(String content) {
                        LogUtil.i("miui: " + content);
                    }
                };
                Logger.setLogger(context, newLogger);
            }

            return;
        } else if (RomUtil.rom() == RomUtil.Target.EMUI) {

        } else if (RomUtil.rom() == RomUtil.Target.FLYME) {

        } else {
            PushManager.getInstance().initialize(context.getApplicationContext(), GeTuiPushService.class);
            PushManager.getInstance().registerPushIntentService(context.getApplicationContext(), GeTuiIntentService.class);
        }
    }

    public static void unregister(Context context) {
        if (RomUtil.rom() == RomUtil.Target.MIUI) {
            MiPushClient.unregisterPush(context);
        }
    }

    /**
     * 用于小米推送的注册
     */
    private static boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
