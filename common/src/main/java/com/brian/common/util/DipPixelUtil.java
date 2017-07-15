
package com.brian.common.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.brian.common.Env;

/**
 * Dip和Pixel之间转化
 */
public class DipPixelUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 offsetX(像素)
     */
    @SuppressLint("DefaultLocale")
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        final float scale = Env.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 offsetX(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param spValue
     * @param fontScale (DisplayMetrics类中的scaledDensity属性)
     * @return
     */
    public static int sp2pix(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * sp转px
     * PS：建议使用setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
     */
    public static int sp2px(float spValue) {
        return sp2px(Env.getContext(), spValue);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变 
     * PS：建议使用setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
     * @param context
     * @param spValue
     * @return
     */
    public static int px2sp(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue / scale + 0.5f);
    }

}
