package com.brian.common.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by huamm on 2016/10/12 0012.
 */

public class AnimUtil {

    public static void doScaleAnim(View view, float fromScale, float toScale, int duration, boolean fillAfter) {
        ScaleAnimation anim = new ScaleAnimation(fromScale, toScale, fromScale, toScale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(duration); //设置动画持续时间
        anim.setFillAfter(fillAfter); //设置动画结束后是否保持状态
        view.startAnimation(anim);
    }
}
