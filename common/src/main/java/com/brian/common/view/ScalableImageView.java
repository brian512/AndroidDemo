package com.brian.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.brian.common.util.DipPixelUtil;
import com.brian.common.util.JDLog;
import com.brian.common.util.LogUtil;

/**
 * 可缩放，拖拽的ImageView
 * Created by brian on 17-7-12.
 */

public class ScalableImageView extends AppCompatImageView {

    private enum Action_Mode {
        Drag, Zoom, None
    }

    // 最小缩放
    private static final float MIN_SCALE = 0.5f;
    // 最大缩放
    private static final float MAX_SCALE = 2.0f;

    // 移动的最小间距
    private final int MIN_DST = DipPixelUtil.dip2px(5);

    private Action_Mode mMode = Action_Mode.None;

    private int mCurBitmapWidth;
    private int mCurBitmapHeight;

    private Bitmap mBitmap;

    private Matrix mMatrix = new Matrix();
    private Matrix mCurrentMatrix = new Matrix();
    private PointF mStartPoint = new PointF();
    private float mStartDist;
    private PointF mCenterPoint = new PointF();


    public ScalableImageView(Context context) {
        this(context, null, 0);
    }
    public ScalableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ScalableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.FIT_CENTER); // 先让图片初始化显示
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mBitmap == null) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            mCurBitmapWidth = mBitmap.getWidth();
            mCurBitmapHeight = mBitmap.getHeight();
            LogUtil.log("mCurBitmapWidth=" + mCurBitmapWidth + "; mCurBitmapHeight=" + mCurBitmapHeight);

            setScaleType(ScaleType.MATRIX); // 只有在该模式下才能进行Matrix变换
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartPoint.set(event.getX(), event.getY()); // 记录起始位置，用于拖动处理
                mMode = Action_Mode.Drag;
                mCurrentMatrix.set(getImageMatrix());
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                mMode = Action_Mode.None;
                break;
            // 多点触控
            case MotionEvent.ACTION_POINTER_DOWN:
                mStartDist = spacing(event);
                if (mStartDist > MIN_DST) {
                    centerPoint(mCenterPoint, event); // 设置中心点，用于缩放
                    mMode = Action_Mode.Zoom;
                    mCurrentMatrix.set(getImageMatrix());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mMode == Action_Mode.Drag) {
                    mMatrix.set(mCurrentMatrix);
                    float dX = event.getX() - mStartPoint.x;
                    float dY = event.getY() - mStartPoint.y;

//                    mMatrix.postTranslate(dX, dY);
                    faultTolerantForTranslate(mMatrix, dX, dY);

                } else if (mMode == Action_Mode.Zoom) {
                    float endDist = spacing(event);
                    if (endDist > MIN_DST) {
                        float dScale = endDist / mStartDist; // 两个手指距离比值
                        mMatrix.set(mCurrentMatrix);
//                        mMatrix.postScale(dScale, dScale, mCenterPoint.x, mCenterPoint.y);
                        faultTolerantForScale(mMatrix, dScale, mCenterPoint);
                    }
                }
                setImageMatrix(mMatrix); // 更新
                break;
        }
        return true;
    }

    /**
     * 缩放的容错处理
     * 使缩放比在最大值和最小值之间
     */
    private void faultTolerantForScale(Matrix matrix, float dScale, PointF centerPoint) {

        float[] values = new float[9];
        matrix.getValues(values);
        float scale = values[0];

        float scaleWidth =  (scale * mCurBitmapWidth);
        float scaleHeight =  (scale * mCurBitmapHeight);

        float height = getHeight();
        float width = getWidth();

        if (scale * dScale > MAX_SCALE) {
            dScale =  MAX_SCALE / scale;
        } else if (scale * dScale < MIN_SCALE) {
            dScale =  MIN_SCALE / scale;
        }

        matrix.postScale(dScale, dScale, centerPoint.x, centerPoint.y);

        if (scaleWidth < width || scaleHeight < height) {
            faultTolerantForTranslate(matrix, 0, 0);
        }
    }

    /**
     * 平移的容错处理
     * 会检测移动边缘
     */
    private void faultTolerantForTranslate(Matrix matrix, float dX, float dY) {

        float[] values = new float[9];
        matrix.getValues(values);
        float translateX = values[2];
        float translateY = values[5];

        float scale = values[0];

        float scaleWidth =  (scale * mCurBitmapWidth);
        float scaleHeight =  (scale * mCurBitmapHeight);

        float height = getHeight();
        float width = getWidth();

        JDLog.log("translateX="+ translateX + "; translateY=" + translateY);
        JDLog.log("scaleWidth="+ scaleWidth + "; scaleHeight=" + scaleHeight);

        if (scaleWidth > width) {
            if (translateX + dX > 0) {
                dX = -translateX;
            } else if (translateX + dX < width - scaleWidth) {
                dX = width - scaleWidth - translateX;
            }
        } else {
            float padding = (width - scaleWidth) / 2;
            if (translateX + dX != padding) {
                dX = -translateX + padding;
            }
        }

        if (scaleHeight > height) {
            if (translateY + dY > 0) {
                dY = -translateY;
            } else if (translateY + dY < height - scaleHeight) {
                dY = height - scaleHeight - translateY;
            }
        } else {
            float padding = (height - scaleHeight) / 2;
            if (translateY + dY != padding) {
                dY = -translateY + padding;
            }
        }

        LogUtil.log("dX=" + dX + "; dY=" + dY);
        matrix.postTranslate(dX, dY);
    }

    /**
     * 计算event两个点之间的距离
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void centerPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
