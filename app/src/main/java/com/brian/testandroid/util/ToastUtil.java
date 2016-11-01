package com.brian.testandroid.util;

import android.os.Looper;
import android.widget.Toast;

import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.common.Env;

/**
 * toast信息提示
 * Created by huamm on 2016/9/12 0012.
 */
public class ToastUtil {

    public static void showMsg(CharSequence msg) {
        showMsg(msg, false);
    }

    public static void showMsg(final CharSequence msg, final boolean isLong) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(Env.getContext(), msg, isLong ? Toast.LENGTH_SHORT : Toast.LENGTH_SHORT).show();
        } else {
            BaseActivity.getUIHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Env.getContext(), msg, isLong ? Toast.LENGTH_SHORT : Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
