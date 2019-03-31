package com.brian.testandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * title : 放大镜控件
 * author : ruohui.huang
 * time : 2018-10-15
 */

public class ZoomImageView extends View {

    /**
     * UI主体
     * 放大镜  mShapeDrawable
     * 圆顶点 点1 放大倍数
     * 圆底点 点2 放大半径
     */

    private static final String TAG = "log_huang";
    //放大倍数
    private float mFactor = 1.5f;
    //放大镜的半径
    private int mRadius = 150;
    // 原图
    final private Bitmap mBitmap;
    // 放大后的图
    private Bitmap mScaledBitmap;
    // 制作的圆形的图片（放大的局部），盖在Canvas上面
    private ShapeDrawable mShapeDrawable;
    //保存当前view的宽和高
    final private int measuredWith = 800;
    final private int measuredHeight = 800;

    private Matrix mMatrix;
    private Paint mPaint;

    private float mOldDist;//两点按下时的距离
    private float mNewDist;//两点实时的距离

    //保存初始坐标
    private int mZoomCenterX;
    private int mZoomCenterY;

    private int mPointerDownX;
    private int mPointerDownY;

    /**
     * 3 - 手势缩放状态
     * 0 - 普通拖动
     */
    private int flag = 0;

    public ZoomImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //实例化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.daa);
        mMatrix = new Matrix();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure: ");

        setMeasuredDimension(measuredWith, measuredHeight);
        //初始化圆心坐标为中心
        mZoomCenterX = measuredWith / 2;
        mZoomCenterY = measuredHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: "
                + "mRadius=" + mRadius
        );
        canvas.drawBitmap(mBitmap,0,0,null);

        //重新获取放大后的图像
        getScaledPhoto(mBitmap.getWidth(), mBitmap.getHeight());
        // 设置移动距离
        mMatrix.setTranslate(mRadius - mZoomCenterX * mFactor, mRadius - mZoomCenterY * mFactor);
        mShapeDrawable.getPaint().getShader().setLocalMatrix(mMatrix);
        // 切出手势区域点位置的圆
        mShapeDrawable.setBounds(mZoomCenterX - mRadius,  mZoomCenterY - mRadius, mZoomCenterX + mRadius, mZoomCenterY + mRadius);
        mShapeDrawable.draw(canvas);
        canvas.drawCircle(mZoomCenterX, mZoomCenterY, mRadius, mPaint);
//        canvas.drawPoint(trans_x, trans_y, mPaint);//画点1 放大倍数控制
//        canvas.drawPoint(mZoomCenterX, mZoomCenterY + mRadius, mPaint);//画点2 放大半径控制
    }

    private void getScaledPhoto(int bitmapWidth, int bitmapHeight) {
        Log.i(TAG, "getScaledPhoto: ");
        //放大后的整个图片
        mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (bitmapWidth * mFactor),
                (int) (bitmapHeight * mFactor), true);
        BitmapShader bitmapShader = new BitmapShader(mScaledBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        //获取放大的图片
        if (mShapeDrawable == null) {
            mShapeDrawable = new ShapeDrawable(new OvalShape());
        }
        mShapeDrawable.getPaint().setShader(bitmapShader);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "event.getAction()=" + (event.getAction() & MotionEvent.ACTION_MASK));
        int x = (int) event.getX(); // 获取当前触摸点的X轴坐标
        int y = (int) event.getY(); // 获取当前触摸点的Y轴坐标

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mPointerDownX = x;
                mPointerDownY = y;
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                /*flag = 0;*/
                Log.i(TAG, "onTouchEvent: event.getPointerCount() = " + event.getPointerCount());
                if (event.getPointerCount() == 2) {
                    mOldDist = spacing(event);//两点按下时的距离
                    Log.d(TAG, "ACTION_POINTER_DOWN = " + mOldDist);
                    flag = 3;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (flag == 1) {
                } else if (flag == 2) {
//                    zoomRadius(y - mZoomCenterY);
                } else if (flag == 3) { // 手势控制缩放
                    if (event.getPointerCount() == 2) {
                        mNewDist = spacing(event);//两点按下时的距离
                        zoomRadius((int)(mNewDist - mOldDist + mRadius));
                        mOldDist = mNewDist;
                    }
                } else {
                    //更新圆心坐标
                    if (Math.abs(mPointerDownX - x) > 20 || Math.abs(mPointerDownY - y) > 20) {
                        mZoomCenterX = x;
                        mZoomCenterY = y;
                    }
                }
                invalidate(); // 重绘画布
                break;
            }
            case MotionEvent.ACTION_UP: {
                //手指抬起，更新flag
                flag = 0;
                mPointerDownX = 0;
                mPointerDownY = 0;
                break;
            }
        }
        return true;
    }

    //放大半径变化
    private void zoomRadius(int radius) {
        Log.i(TAG, "radius=" + radius);
        if (radius >= 300) {
            mRadius = 300;
        } else if (radius <= 100) {
            mRadius = 100;
        } else {
            mRadius = radius;
        }
    }

    //放大倍数变化
    private void zoomFactor(float v) {
        Log.i(TAG, "zoomFACTOR: ");
        mFactor = 1.5f * v;
        if (mFactor >= 3f) mFactor = 3F;
        if (mFactor <= 1.5f) mFactor = 1.5F;
    }

    //计算间距
    private float spacing(MotionEvent event) {
        float deltaX = event.getX(0) - event.getX(1);
        float deltaY = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

}

