package com.brian.testandroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.brian.testandroid.common.Env;
import com.brian.testandroid.common.WeakRefHandler;
import com.brian.testandroid.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 时间进度条，支持打点标记
 * Created by huamm on 2016/10/11 0011.
 */
public class MarkableProgressBar extends View {

    private static final int DRAW_INTERVAL = 15;

    private Paint mPaint;
    private int mColorBg = Color.parseColor("#88ffffff");
    private int mColorProgress = Color.parseColor("#ff7f71");


    private List<Mark> mMarkList = new ArrayList<>();

    private int mMaxTime = 15_000;
    private int mMinTime = 3_000;
    private long mLastCountTime = 0;
    private float mCurrProgressWidth = 0;
    private long mStartTime = 0;

    private Mark mMinMark;

    private boolean mHasFinished = false;
    private boolean mHasReachedMin = false;

    private float mW = 0;
    private float mH = 0;

    public MarkableProgressBar(Context context) {
        this(context, null, 0);
    }

    public MarkableProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkableProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mH = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (mMinMark == null && mMinTime > 0) {
            initLimitMark();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        mPaint.setColor(mColorBg);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mPaint.setColor(mColorProgress);
        long timeCount = mLastCountTime;
        if (mStartTime != 0) {
            timeCount = mLastCountTime + System.currentTimeMillis() - mStartTime;
        }
        float percent = timeCount / (mMaxTime*1f);
        mCurrProgressWidth = percent * mW; // 绘制时计算当前需要绘制的长度

        if (mCurrProgressWidth > mW) {
            mCurrProgressWidth = mW;
            mHandle.removeMessages(MSG_DRAW);
            if (mProgressListener != null && !mHasFinished) {
                mHasFinished = true;
                mProgressListener.onFinished();
            }
        }
        canvas.drawRect(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + mCurrProgressWidth, mH + getPaddingTop(), mPaint);

        mPaint.setColor(Color.WHITE);
        for (Mark mark : mMarkList) {
            canvas.drawRect(mark.left, getPaddingTop(), mark.right, mH + getPaddingTop(), mPaint);
        }

        if (timeCount < mMinTime && mMinMark != null) {
            canvas.drawRect(mMinMark.left, getPaddingTop(), mMinMark.right, mH + getPaddingTop(), mPaint);
        } else {
            if (mProgressListener != null && !mHasReachedMin) {
                mHasReachedMin = true;
                mProgressListener.onReachMin();
            }
        }

        if (mProgressListener != null) {
            mProgressListener.onProgress(percent);
        }

//        mPaint.setColor(Color.RED);
//        mPaint.setTextSize(20);
//        canvas.drawText("" + (int)(percent*100), 0, getHeight() / 2, mPaint);
    }

    private static final int MSG_DRAW = 0;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_DRAW) {
                invalidate();
                mHandle.sendEmptyMessageDelayed(MSG_DRAW, DRAW_INTERVAL);
            }
            return true;
        }
    };
    private Handler mHandle = new WeakRefHandler(mCallback);

    /**
     * 开始
     */
    public void startProgress() {
        mStartTime = System.currentTimeMillis();
        mHandle.sendEmptyMessage(MSG_DRAW);
    }

    /**
     * 停止或暂停
     */
    public void stopProgress() {
        mHandle.removeMessages(MSG_DRAW);
        if (mCurrProgressWidth > 1) {
            mMarkList.add(Mark.getMark(mCurrProgressWidth));
            mCurrProgressWidth += Mark.getMarkSize();
        }
        mLastCountTime += System.currentTimeMillis() - mStartTime;
        mStartTime = 0;
    }

    public void resetProgress() {
        mHandle.removeMessages(MSG_DRAW);
        mLastCountTime = 0;
        mStartTime = 0;
        mCurrProgressWidth = 0;

        mMarkList.clear();
        if (mW > 0) {
            initLimitMark();
            invalidate();
        }
    }

    private void initLimitMark() {
        mMinMark = Mark.getMark(mW* mMinTime *1f/mMaxTime);
    }


    /**
     * 初始化控件参数
     * @param totalTime 总时长
     * @param leastTime 最低时长标记 leastTime < totalTime，若leastTime < 0，则不标记
     */
    public void setTime(int totalTime, int leastTime) {
        if (totalTime <= 0 || leastTime > totalTime) {
            throw new IllegalArgumentException("参数错误");
        }
        mMaxTime = totalTime;
        mMinTime = leastTime;
    }

    /**
     * 设置颜色
     */
    public void setColor(int colorBg, int colorProgress) {
        mColorBg = colorBg;
        mColorProgress = colorProgress;
    }

    /**
     * 记录标记的位置，及宽度
     */
    static class Mark {
        float left = 0;
        float right = 0;

        static Mark getMark(float left) {
            Mark mark = new Mark();
            mark.left = left;
            mark.right = left + getMarkSize();
            return mark;
        }

        static float getMarkSize() {
            return UIUtil.dp2Px(Env.getContext(), 1);
        }
    }

    public interface OnProgressStateListener {
        void onFinished();
        void onReachMin();
        void onProgress(float percent);
    }
    private OnProgressStateListener mProgressListener;
    public void setOnProgressListener(OnProgressStateListener listener) {
        mProgressListener = listener;
    }
}
