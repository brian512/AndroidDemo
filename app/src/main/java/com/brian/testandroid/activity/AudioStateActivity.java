package com.brian.testandroid.activity;

import android.os.Bundle;

import com.brian.common.BaseActivity;
import com.brian.common.HeadsetDeviceMonitor;
import com.brian.common.util.LogUtil;

public class AudioStateActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isOn = HeadsetDeviceMonitor.isHeadSetOn(this);
        LogUtil.d("ison=" + isOn);

        HeadsetDeviceMonitor.getInstance().subscribe(this, new HeadsetDeviceMonitor.OnHeadsetDeviceListener() {
            @Override
            public void onDeviceChanged(boolean conn, int type) {

            }
        });

    }
}
