package com.brian.androidpush;

import android.content.Context;

import com.brian.androidpush.getui.GeTuiIntentService;
import com.brian.androidpush.getui.GeTuiPushService;
import com.igexin.sdk.PushManager;

/**
 * Created by brian on 2017/5/25.
 */

public class PushHelper {

    public static void register(Context context, boolean debug) {
        PushManager.getInstance().initialize(context.getApplicationContext(), GeTuiPushService.class);
        PushManager.getInstance().registerPushIntentService(context.getApplicationContext(), GeTuiIntentService.class);
    }

}
