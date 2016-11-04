package com.brian.testandroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义view模板
 *
 * measure[onMeasure], layout[ setFrame[ onSizeChanged ] onLayout], draw[ onDraw ]
 *
 *
 * Created by huamm on 2016/11/2 0002.
 */

public class CustomView extends View {

    // 尽量使用dp，默认单位是px
    private static final int DEFAULT_SIZE_WIDTH = 200;
    private static final int DEFAULT_SIZE_HEIGHT = 200;

    private Paint mPaint;

    private int mWidth;
    private int mHeight;

    public CustomView(Context context) {
        this(context, null, 0);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
    }

    /**
     * 如果不需要兼容wrap_content的话，直接使用默认的测量就够了
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec); // 默认的测量方式，不支持wrap_content即MeasureSpec.AT_MOST模式

        // 自测量之后设置大小
        setMeasuredDimension(measureSize(widthMeasureSpec, DEFAULT_SIZE_WIDTH), measureSize(heightMeasureSpec, DEFAULT_SIZE_HEIGHT));
    }

    /**
     * 在@link #draw(android.graphics.Canvas)} 方法里被调用
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); // 如果继承现有控件的话，这个必须加上，否则现有控件的效果就没了

        invalidate();
        postInvalidateDelayed(15); // 延时自刷新
    }

    /**
     * 自定义view时尽量不要覆写该方法，即使覆盖也需要调用super.draw(canvas);
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    /**
     * 从XML加载组件后回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 组件大小改变时回调
     * 可以在这里初始化一些与控件大小相关的组件
     * 在setFrame/setTop/setBottom/setLeft/setRight中调用
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();

//        mPaint.setShader()
    }

    /**
     * 此方法在旧的SDK上是定义为final，所以还是尽量不要覆写该方法
     * 在super.layout中调用了{#link onLayout}，并回调OnLayoutChangeListener.onLayoutChange
     */
    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
    }

    /**
     * 回调该方式来确定显示的位置
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 监听触摸事件回调
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 自测量控件的大小
     */
    private int measureSize(int measureSpec, int defualtSize) {
        int result = defualtSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }
}
