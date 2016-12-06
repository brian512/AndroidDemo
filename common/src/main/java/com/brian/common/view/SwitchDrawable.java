
package com.brian.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class SwitchDrawable extends Drawable {

    private static final float ARROW_HEAD_ANGLE = (float) Math.toRadians(45);

    private final Path mPath = new Path();
    private final Paint mPaint = new Paint();

    private float mProgress;
    
    private int mSize = 40;
    private int mGap = 15;

    public SwitchDrawable(Context context) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setAntiAlias(true);

        setBarThickness(dip2px(context, 2));
    }

    @Override
    public void draw(Canvas canvas) {

        mPath.rewind();
        if (mProgress < 0.5f) {
            // top bar
            mPath.moveTo(0, lerp(mSize - mGap, mSize, mProgress * 2));
            mPath.rLineTo(mSize, 0);

            // draw middle bar
            mPath.moveTo(0, mSize);
            mPath.rLineTo(mSize, 0);

            // bottom bar
            mPath.moveTo(0, lerp(mSize + mGap, mSize, mProgress * 2));
            mPath.rLineTo(mSize, 0);
        } else {

            final float rotation = lerp(0, ARROW_HEAD_ANGLE, mProgress * 2 - 1);
            final float upStartX = Math.round(mSize/2 * Math.cos(rotation));
            final float upStartY = Math.round(mSize/2 * Math.sin(rotation));

            mPath.moveTo(mSize/2 - upStartX, mSize - upStartY);
            mPath.rLineTo(2 * upStartX, upStartY * 2);

            mPath.moveTo(mSize/2 - upStartX, mSize + upStartY);
            mPath.rLineTo(2 * upStartX, upStartY * -2);
        }

        mPath.close();

        canvas.save();
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha != mPaint.getAlpha()) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    /**
     * Sets the color of the drawable.
     */
    public void setColor(int color) {
        if (color != mPaint.getColor()) {
            mPaint.setColor(color);
            invalidateSelf();
        }
    }

    /**
     * Returns the color of the drawable.
     */
    public int getColor() {
        return mPaint.getColor();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * Sets the thickness (stroke size) for the bars.
     * 
     * @param width stroke width in pixels
     */
    public void setBarThickness(float width) {
        if (mPaint.getStrokeWidth() != width) {
            mPaint.setStrokeWidth(width);
            invalidateSelf();
        }
    }

    /**
     * Returns the thickness (stroke width) of the bars.
     */
    public float getBarThickness() {
        return mPaint.getStrokeWidth();
    }

    /**
     * Returns the current progress of the arrow. (from = 0.0, to = 1.0)
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * Set the progress of the arrow.
     * <p>
     * A value of {@code 0.0} indicates that the arrow should be drawn in it's
     * starting position. A value of {@code 1.0} indicates that the arrow should
     * be drawn in it's ending position.
     * </p>
     */
    public void setProgress(float progress) {
        if (mProgress != progress) {
            mProgress = progress;
            invalidateSelf();
        }
    }

    /**
     * Linear interpolate between a and b with parameter t.
     */
    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
