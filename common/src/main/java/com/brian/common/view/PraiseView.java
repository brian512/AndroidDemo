
package com.brian.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.brian.common.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 直播页面点赞控件，采用SurfaceView绘制 与普通控件使用方法类似，点赞是只需要调用addBubble(int)即可
 * 
 * @author huamm
 */
public class PraiseView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int MAX_BUBBLE_COUNT = 100;

    private SurfaceHolder mHolder;
    private DrawTask mDrawTask; // 绘制UI的线程
    private Paint mPaint; // 绘制需要使用的画刷

    private int mWidth; // 控件的宽度
    private int mHeight; // 控件的高度

    private int mDrawDelta = 4;

    private Bitmap[] mDrawables; // 存放需要展示的图
    private int[] mDrawableResIDs; // 存放需要展示的图

    private static final int MAX_RECYCLE_BUBBLE = 50;
    private final LinkedList<Bubble> mRecycledBubbles = new LinkedList<Bubble>(); // 用于存放回收点赞信息
    private final List<Bubble> mBubbles = Collections.synchronizedList(new LinkedList<Bubble>()); // 用于存放点赞信息

    private OnBubbleStateListener mOnBubbleStateListener; // 点赞开始和停止的监听

    private Random mRandom = new Random(); // 用于产生随机数

    private static final int MSG_DRAW_BUBBLE = 10;
    private HandlerThread mHandlerThread = new HandlerThread("PraiseView");

    private Handler.Callback mCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DRAW_BUBBLE:
                    mHandler.removeMessages(MSG_DRAW_BUBBLE);
                    mHandler.post(mDrawTask);
                    if (!mBubbles.isEmpty()) {
                        mHandler.sendEmptyMessageDelayed(MSG_DRAW_BUBBLE, 30);
                    }
                    break;
            }
            return true;
        }
    };
    private Handler mHandler = null;

    public PraiseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mPaint = new Paint();
        mDrawDelta = dip2px(3);

        mDrawableResIDs = new int[] {
                R.mipmap.ic_praise_eight,
                R.mipmap.ic_praise_one,
                R.mipmap.ic_praise_third,
                R.mipmap.ic_praise_two,
                R.mipmap.ic_praise_five,
                R.mipmap.ic_praise_four,
                R.mipmap.ic_praise_seven,
                R.mipmap.ic_praise_six,
        };
        mDrawables = new Bitmap[mDrawableResIDs.length];
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR); // 清空界面

        synchronized (mBubbles) {
            for (int i = mBubbles.size() - 1; i >= 0 && mBubbles.size() > 0; i--) {
                drawBubble(canvas, mBubbles.get(i));
            }
        }
    }

    private void drawBubble(Canvas canvas, Bubble bubble) {
        int alpha = 0;
        Bubble.Coordinates coords = bubble.coordinates;

        // 设置透明度
        if (coords.y > mHeight - bubble.bitmap.getHeight() / 2) {
            alpha = 0;
        } else if (coords.y > mHeight * 0.75f) {
            alpha = (int) ((1 - coords.y * 1f / mHeight) * 1020f);
        } else if (coords.y > mHeight * 0.5f) {
            alpha = 255;
        } else {
            if (coords.y < 0) {
                alpha = 0;
            } else {
                alpha = (int) (coords.y * 510f / mHeight);
            }
        }
        mPaint.setAlpha(alpha);

        // 设置缩放
        canvas.save();
        if (coords.y < (mHeight + bubble.bitmap.getHeight()) && coords.y > mHeight * 0.75) {
            bubble.scale = 0.5f + (mHeight - coords.y) * 2f / mHeight;
            canvas.scale(bubble.scale, bubble.scale, coords.x, coords.y);
        }

        canvas.drawBitmap(bubble.bitmap, coords.x, coords.y, mPaint); // 绘制图像
        canvas.restore();

        // 设置下次绘制坐标
        coords.y = coords.y - mDrawDelta;
        coords.x = getXbyY(bubble, coords.y);

        // 判断是否已经不可见
        if (coords.y < 0) {
            if (mRecycledBubbles.size() < MAX_RECYCLE_BUBBLE) {
                mRecycledBubbles.add(bubble);
            }
            mBubbles.remove(bubble);
        }
    }

    private int getXbyY(Bubble bubble, int y) {
        return (int) (bubble.delta - bubble.amplifier * (float) (Math.sin(bubble.data1 + bubble.data2 * y)));
    }

    /**
     * 优化显示点赞个数，显示和待显示的气泡总数为MAX_BUBBLE_COUNT
     * 
     * @param count
     * @return
     */
    private int getOptimized(int count) {
        int optmizedCount = 0;
        if (count < 30) {
            optmizedCount = count;
        } else {
            optmizedCount = 27 + count / 10;
        }
        int leftCount = mBubbles.size();
        int totalCount = optmizedCount + leftCount;
        return totalCount > MAX_BUBBLE_COUNT ? (MAX_BUBBLE_COUNT - leftCount) : optmizedCount;
    }

    /**
     * 添加点赞，会对传入的个数进行处理
     * 
     * @param count
     */
    public void addBubble(int count) {
        // 界面为初始化完全或者不可见的情况下，都直接丢弃点赞动效
        if (mWidth < 10 || getVisibility() != View.VISIBLE) {
            return;
        }
        if (mHandler == null) {
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper(), mCallback);
        }
        if (mBubbles.size() <= 0 && mOnBubbleStateListener != null) {
            mOnBubbleStateListener.onStart();
        }
        if (count == 1) {
            Bubble bubble = getRandomBubble(10);
            mBubbles.add(bubble);
        } else {
            int optmizedCount = getOptimized(count);
            int delayMax = optmizedCount * 50 > 3000 ? 3000 : optmizedCount * 50;
            LinkedList<Bubble> list = new LinkedList<Bubble>();
            for (int i = 0; i < optmizedCount; i++) {
                Bubble bubble = getRandomBubble(delayMax);
                list.add(bubble);
            }
            mBubbles.addAll(list);
        }
        mHandler.sendEmptyMessage(MSG_DRAW_BUBBLE);
    }

    /**
     * 生成一个随机的气泡，若回收池有可用就直接初始化一下返回，没有则创建一个
     * 
     * @param delayMax
     * @return
     */
    private Bubble getRandomBubble(int delayMax) {
        Bubble bubble = null;
        if (mRecycledBubbles.isEmpty()) {
            bubble = new Bubble();
        } else {
            try { // 修复umeng崩溃
                bubble = mRecycledBubbles.removeFirst();
            } catch (Exception e) {
                bubble = new Bubble();
            }
            bubble.initBubble();
        }
        bubble.delay = mRandom.nextInt(delayMax); // 避免同时增加很多个赞
        bubble.coordinates.x = 0;
        bubble.coordinates.y = mHeight + bubble.delay;
        bubble.delta += bubble.delta - getXbyY(bubble, mHeight);
        return bubble;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mDrawTask == null) {
            mDrawTask = new DrawTask(holder, this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        clearBubbles();
    }

    /**
     * 随机获取一个bitmap
     * 
     * @return
     */
    private Bitmap getRandBitmap() {
        int n = mRandom.nextInt(mDrawableResIDs.length);
        Bitmap bitmap = mDrawables[n];
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = BitmapFactory.decodeResource(getResources(), mDrawableResIDs[n]);
            mDrawables[n] = bitmap;
        }

        return bitmap;
    }

    public void release() {
        clearBubbles();
        mHandlerThread.quit();
        mHandler = null;
    }

    /**
     * 清除点赞气泡
     */
    public void clearBubbles() {
        mBubbles.clear();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 绘制UI的线程，只要是调用PraiseView.onDraw(canvas); 并且做了锁保护（固定用法，不要轻易修改）
     * ###########################################
     */
    class DrawTask implements Runnable {

        private SurfaceHolder holder;
        private PraiseView praiseView;

        public DrawTask(SurfaceHolder holder, PraiseView snowView) {
            this.praiseView = snowView;
            this.holder = holder;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                // 程序切后台会导致获取的canvas为null,这个时候直接移除当前的所有动效，防止程序在后台跑无用的消息循环
                if (canvas == null) {
                    mHandler.removeMessages(MSG_DRAW_BUBBLE);
                    return;
                }
                synchronized (holder) {
                    praiseView.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    try { // 修复umeng崩溃
                        holder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * 保存赞的信息，主要有坐标信息，路径相关 ###########################################
     */
    class Bubble {
        public Bitmap bitmap;
        public Coordinates coordinates;

        // 路径相关
        public int delay; // 延迟距离
        public float frequency = 1.5f; // 频率
        public float phase = 45; // 相位
        public float delta = 500; // 偏移量
        public float amplifier = 500; // 振幅

        public float scale = 1.0f; // 缩放

        // 用于缓存计算结果，避免在onDraw中计算过量
        public double data1;
        public double data2;

        public Bubble() {
            initBubble();
        }

        /**
         * Contains the coordinates of the graphic.
         */
        public class Coordinates {
            public int x;
            public int y;
        }

        public Bubble initBubble() {
            this.bitmap = getRandBitmap();
            float rFloat = mRandom.nextFloat();
            frequency = 0.5f + rFloat * 0.5f;
            phase = rFloat * 360;
            amplifier = mWidth / 8 + rFloat * mWidth / 8;

            if (mWidth > 10) {
                delta = mWidth / 2; // 偏移量
            } else {
                delta = dip2px(20); // 偏移量
            }

            coordinates = new Coordinates();
            coordinates.y = mHeight;

            data1 = phase * 2 * (float) Math.PI / 360.0f;
            data2 = 2 * Math.PI * frequency / mHeight;
            return this;
        }
    }

    private float scale = -1f; // 缓存起来

    private int dip2px(float dpValue) {
        if (scale < 0) {
            scale = getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置点赞开始和结束事件
     * 
     * @param bubbleStateListener
     */
    public void setOnBubbleStateListener(OnBubbleStateListener bubbleStateListener) {
        this.mOnBubbleStateListener = bubbleStateListener;
    }

    public interface OnBubbleStateListener {
        public void onStart();

        /**
         * 千万不要做耗时操作，否则UI会卡顿
         */
        public void onEnd();
    }

}
