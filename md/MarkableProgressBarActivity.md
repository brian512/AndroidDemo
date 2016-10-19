##[自定义View——可打点记录的进度条](http://blog.csdn.net/brian512/article/details/52864829)
项目为短视频录制的进度条，类似微信小视频，只是样式不一样。需要设定进度条总时长，还有最低时长。视频录制过程中暂停时，如果录制时长长低于最小时长，则算暂停，不能完成录制，若高于最低时长，则显示结束录制按钮，也可以继续录制。每次暂停都需要在进度条上显示一个标记。效果如下图：
![](http://img.blog.csdn.net/20161019223919935)
先分析一下这个进度条的组成，首先是一个底色背景，然后初始化有一个最小录制时长标记，然后是红色的计时进度条。由于每次暂停时都需要在进度条上面打标记，所以需要一个列表来存储标记的位置。
下面就介绍一下具体的实现：
定义一个结构存储每个标记的起始位置和结束位置：
```java
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
```
因为标记的宽度是一致的，所以只需要用标记的起始位置做初始化。
测量时不需要考虑wrap_content的情况，所以只是处理了padding
```java
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mH = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (mMinMark == null && mMinTime > 0) {
            initLimitMark();
        }
    }
    
    private void initLimitMark() {
        mMinMark = Mark.getMark(mW* mMinTime *1f/mMaxTime);
    }
```
测量时把最小标记的位置也初始化了，以便最开始绘制时就能显示最小时长标记。

具体的绘制我在代码里加注释说明了：
```java
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        // 首先绘制背景
        mPaint.setColor(mColorBg);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        // 再绘制进度
        mPaint.setColor(mColorProgress);
        long timeCount = mLastCountTime;
        if (mStartTime != 0) { // 中间暂停的时间需要处理掉，这里的累计时长是用之前的累计时长+本次时长。
            timeCount = mLastCountTime + System.currentTimeMillis() - mStartTime;
        }
        float percent = timeCount / (mMaxTime*1f);
        mCurrProgressWidth = percent * mW; // 绘制时计算当前需要绘制的长度

        // 若已到最大时间，则停止进度条刷新并回调
        if (mCurrProgressWidth > mW) {
            mCurrProgressWidth = mW;
            mHandle.removeMessages(MSG_DRAW);
            if (mProgressListener != null && !mHasFinished) {
                mHasFinished = true;
                mProgressListener.onFinished();
            }
        }
        canvas.drawRect(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + mCurrProgressWidth, mH + getPaddingTop(), mPaint);

        // 最后绘制打点标记
        mPaint.setColor(Color.WHITE);
        for (Mark mark : mMarkList) {
            canvas.drawRect(mark.left, getPaddingTop(), mark.right, mH + getPaddingTop(), mPaint);
        }

        // 是否已达到最小时长，需要回调
        if (timeCount < mMinTime && mMinMark != null) {
            canvas.drawRect(mMinMark.left, getPaddingTop(), mMinMark.right, mH + getPaddingTop(), mPaint);
        } else {
            if (mProgressListener != null && !mHasReachedMin) {
                mHasReachedMin = true;
                mProgressListener.onReachMin();
            }
        }

        // 进度回调
        if (mProgressListener != null) {
            mProgressListener.onProgress(percent);
        }

        // 进度显示
//        mPaint.setColor(Color.RED);
//        mPaint.setTextSize(20);
//        canvas.drawText("" + (int)(percent*100), 0, getHeight() / 2, mPaint);
    }
```

这个进度是定时刷新，刷新间隔定位15ms，在流畅性和性能上找了个平衡点。
```java
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
```
这里是用的`WeakRefHandler`防止内存泄漏，这个是基本的工具类。

主要的代码介绍完了，不清楚的可以看demo源码，有问题可以留言。

-----
代码链接：[https://github.com/brian512/AndroidDemo](https://github.com/brian512/AndroidDemo)