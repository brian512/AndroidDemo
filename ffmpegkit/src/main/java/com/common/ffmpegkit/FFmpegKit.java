package com.common.ffmpegkit;

import android.util.Log;

/**
 * Created by huamm on 2017/3/11 0011.
 */

public class FFmpegKit {
    static{
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpegkit");
    }

    public static boolean runSafly(String command) {
        return runSafly(command.split(" "));
    }

    public static boolean runSafly(String[] commands) {
        try {
            for (String s : commands) {
                Log.d("FFmpegKit", "cmd=" + s);
            }

            return run(commands) == 0;
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        return false;
    }

    private native static int run(String[] commands);
}