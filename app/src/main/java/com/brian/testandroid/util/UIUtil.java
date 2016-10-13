
package com.brian.testandroid.util;

import android.content.Context;

import com.brian.testandroid.common.Env;

public class UIUtil {
    private static final String TAG = UIUtil.class.getSimpleName();

    public static int dp2Px(float dp) {
        return dp2Px(Env.getContext(), dp);
    }

    public static int px2Dp(float px) {
        return px2Dp(Env.getContext(), px);
    }

    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
