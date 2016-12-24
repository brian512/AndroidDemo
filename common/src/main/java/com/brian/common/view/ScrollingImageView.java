
package com.brian.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.brian.common.R;

/**
 * 可滚动的ImageView控件，使用startRolling(int) 或者 startRolling(Bitmap)启动滚动
 * @author huamm
 */
public class ScrollingImageView extends View {

    public final float SPEED_SLOW = 1f;
    public final float SPEED_NORMAL = 3f;
    public final float SPEED_FAST = 10f;

    private Bitmap mBitmap;

    private int mResourceID = 0;

    private float mSpeed = SPEED_NORMAL;

    private Rect clipBounds = new Rect(); // 用于保存边界信息
    
    private float offset = 0; // 记录起始位置，需要拼接

    private boolean mIsScrolling; // 标记是否开始滚动

    public ScrollingImageView(Context context) {
        this(context, null, 0);
    }

    public ScrollingImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollingImageView(Context context, AttributeSet attrs, int styleID) {
        super(context, attrs, styleID);
        startScroll(R.mipmap.bg);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), heightMeasureSpec);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        canvas.getClipBounds(clipBounds);


        int width = bitmap.getWidth();
        while (offset <= -width) {
            offset += width;
        }
        float left = offset;

        while (left < clipBounds.width()) {
            canvas.drawBitmap(bitmap, getBitmapLeft(width, left), 0, null);
            left += width;
        }

        if (mIsScrolling && mSpeed != 0) {
            offset -= Math.abs(mSpeed);
            postInvalidateOnAnimation();
        }
    }

    private Bitmap getBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), mResourceID);
        }

        return mBitmap;
    }

    private float getBitmapLeft(float layerWidth, float left) {
        if (mSpeed < 0) {
            return clipBounds.width() - layerWidth - left;
        } else {
            return left;
        }
    }

    /**
     * 开始滚动
     */
    public void startScroll(int imageID) {
        if (imageID <= 0) {
            return;
        }
        mResourceID = imageID;
        start();
    }
    
    /**
     * 开始滚动
     */
    public void startScroll(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        start();
    }
    
    private void start() {
        if (!mIsScrolling) {
            mIsScrolling = true;
            postInvalidateOnAnimation();
        }
    }

    /**
     * 停止滚动
     */
    public void stop() {
        if (mIsScrolling) {
            mIsScrolling = false;
            invalidate();
        }
    }

    /**
     * 设置滚动速度
     * @param speed 速度，默认为 SPEED_NORMAL = 3f
     */
    public void setSpeed(float speed) {
        this.mSpeed = speed;
        if (mIsScrolling) {
            postInvalidateOnAnimation();
        }
    }
    
    /**
     * 获取滚动速度
     * @return
     */
    public float getSpeed() {
        return mSpeed;
    }
    
    /**
     * 是否在滚动
     * @return
     */
    public boolean getIsScrolling() {
        return mIsScrolling;
    }
    
}
