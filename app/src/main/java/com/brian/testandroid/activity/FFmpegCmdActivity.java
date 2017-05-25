package com.brian.testandroid.activity;

import android.os.Bundle;

import com.brian.common.BaseActivity;
import com.brian.common.ThreadPoolManager;
import com.brian.common.util.LogUtil;
import com.common.ffmpegkit.FFmpegKit;

/**
 * Created by huamm on 2017/3/11 0011.
 */

public class FFmpegCmdActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThreadPoolManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                String cmd = "ffmpeg -version";
                String videoPath = "/storage/emulated/0/DCIM/Rokk/Rokk_20170311_140329.mp4";
//                String cmd = "ffmpeg -i " + videoPath + " -filter:v \"setpts=0.5*PTS\" " + videoPath + ".mp4";
                LogUtil.e("cmd =" + cmd);
                boolean result = FFmpegKit.runSafly(cmd);
                LogUtil.e("result =" + result);
            }
        });

    }
}
