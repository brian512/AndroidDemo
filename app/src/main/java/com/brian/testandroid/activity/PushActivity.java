package com.brian.testandroid.activity;

import android.content.Context;
import android.os.Bundle;

import com.brian.androidpush.Message;
import com.brian.androidpush.PushHelper;
import com.brian.androidpush.PushInterface;
import com.brian.common.BaseActivity;

/**
 * Created by brian on 2017/5/25.
 */

public class PushActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PushHelper.register(this, true, new PushInterface() {
            @Override
            public void onRegister(Context context, String registerID) {

            }

            @Override
            public void onUnRegister(Context context) {

            }

            @Override
            public void onPaused(Context context) {

            }

            @Override
            public void onResume(Context context) {

            }

            @Override
            public void onMessage(Context context, Message message) {

            }

            @Override
            public void onMessageClicked(Context context, Message message) {

            }

            @Override
            public void onCustomMessage(Context context, Message message) {

            }

            @Override
            public void onAlias(Context context, String alias) {

            }
        });
    }
}
