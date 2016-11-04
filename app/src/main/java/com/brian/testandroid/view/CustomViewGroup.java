package com.brian.testandroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义ViewGroup模板
 * Created by huamm on 2016/11/2 0002.
 */

public class CustomViewGroup extends ViewGroup {

    public CustomViewGroup(Context context) {
        this(context, null, 0);
    }
    public CustomViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * viewgroup本身是没有需要绘制的东西，除非设置了背景，否则ViewGroup的onDraw方法都不会被调用
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); // from View
    }
}
