package com.brian.testandroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * 带触摸动画的ImageView
 * Created by huamm on 2016/10/12 0012.
 */
public class AnimImageButton extends ImageView {

    public AnimImageButton(Context context) {
        this(context, null);
    }

    public AnimImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doScaleAnim(this, 1.4f, 200, true);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                doScaleAnim(this, 1.4f, 200, false);
                break;
            case MotionEvent.ACTION_CANCEL:
                doScaleAnim(this, 1.4f, 200, false);
                break;
        }
        return true;
    }

    private void doScaleAnim(View view, float scale, int duration, boolean toScale) {
        ScaleAnimation anim;
        if (toScale) {
            anim = new ScaleAnimation(1f, scale, 1f, scale, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            anim = new ScaleAnimation(scale, 1f, scale, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        anim.setDuration(duration);
        anim.setFillAfter(true); //设置动画结束后是否保持状态
        view.startAnimation(anim);
    }
}
