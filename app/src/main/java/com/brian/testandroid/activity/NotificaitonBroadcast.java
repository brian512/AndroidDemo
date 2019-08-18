package com.brian.testandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.brian.common.util.LogUtil;

/**
 * @author Brian on 2019/8/18.
 */
public class NotificaitonBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("intent=" + intent.getAction());
    }
}
