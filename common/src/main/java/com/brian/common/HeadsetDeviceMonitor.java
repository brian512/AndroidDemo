package com.brian.common;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;

import com.brian.common.util.LogUtil;

import java.util.ArrayList;

public class HeadsetDeviceMonitor extends BroadcastReceiver {

    public interface OnHeadsetDeviceListener {
        void onDeviceChanged(boolean conn, int type);
    }

    private static HeadsetDeviceMonitor sInstance;

    private ArrayList<OnHeadsetDeviceListener> mDeviceListeners;
    
    
    public static HeadsetDeviceMonitor getInstance() {
        if (sInstance == null) {
            synchronized (HeadsetDeviceMonitor.class) {
                if (sInstance == null) {
                    sInstance = new HeadsetDeviceMonitor();
                }
            }
        }
        return sInstance;
    }
    
    private HeadsetDeviceMonitor() {
        mDeviceListeners = new ArrayList<>();
    }


    public void subscribe(Context ctx, OnHeadsetDeviceListener listener) {
        subscribe(ctx, listener, false);
    }

    public void subscribe(Context ctx, OnHeadsetDeviceListener listener, boolean fireSoon) {
        if (mDeviceListeners.size() <= 0) {
            start(ctx);
        }
        mDeviceListeners.add(listener);

        if (fireSoon) {
            try {
            } catch (Throwable e) {}
        }
    }

    public void unsubscribe(Context ctx, OnHeadsetDeviceListener listener) {
        mDeviceListeners.remove(listener);

        if (mDeviceListeners.size() <= 0) {
            stop(ctx);
        }
    }

    private void notifyState(boolean conn, int type) {
        for (OnHeadsetDeviceListener listener : mDeviceListeners) {
            listener.onDeviceChanged(conn, type);
        }
    }
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.d("action=" + action);

        int state = 0;
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
            state = intent.getIntExtra("state", 0);
        } else if (AudioManager.ACTION_HEADSET_PLUG.equals(action)) {
            state = intent.getIntExtra("state", 0);
            if (state == 0) {
                // 拔出时不需要重复操作
                return;
            }
        } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            state = intent.getIntExtra("state", 0);
        } else {
            return;
        }
        LogUtil.d("state=" + state);
        if (state == 1) {
            int intType = intent.getIntExtra("microphone", 0);
            LogUtil.d(intType == 1 ? "有麦克风" : "没有麦克风" );
        }
        notifyState(state == 1, 1);
    }


    private void start(Context ctx) {
        try {
            // 耳机设备插拔事件
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
            ctx.registerReceiver(this, intentFilter);

            // 有线耳机和蓝牙耳机 拔出事件广播，回调较快，  PS：仅拔出事件
            IntentFilter disconnectFilter = new IntentFilter();
            disconnectFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            ctx.registerReceiver(this, disconnectFilter);

            // for bluetooth headset connection receiver
            IntentFilter bluetoothFilter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            ctx.registerReceiver(this, bluetoothFilter);
        } catch (Throwable e) {
            LogUtil.printError(e);
        }
    }

    private void stop(Context ctx) {
        try {
            ctx.getApplicationContext().unregisterReceiver(this);
        } catch (Throwable e) {
            LogUtil.printError(e);
        }
    }


    public static boolean isHeadSetOn(Context context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            AudioDeviceInfo[] deviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo info : deviceInfos) {
                LogUtil.d("info=" + info.getType());
                if (info.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || info.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        ) {
                    return true;
                }
            }
            return false;
        } else {
            return audioManager.isWiredHeadsetOn();
        }
    }
}
