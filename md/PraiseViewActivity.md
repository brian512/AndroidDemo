##[SurfaceView实现点赞效果](http://www.jianshu.com/p/1468128b70f1)

项目需要实现点赞的效果，在网上找个一个解决方案：
[http://www.jianshu.com/p/03fdcfd3ae9c](http://www.jianshu.com/p/03fdcfd3ae9c)
初用一些还好，暴力测试一下发现很卡，不太好用。并且像如此麻烦的动画，确实该用
SurfaceView来实现。于是就找资料，没有找到直接能用的。最后，还是自己来实现。先看效果图：
![这里写图片描述](https://github.com/brian512/PraiseView/raw/master/pic/1.gif)
点击屏幕时，从屏幕底部飘出随机的一个赞的图标。
demo中两个核心的东西：
1. SurfaceView的基本使用；
2. 正弦波的参数设置；

直接在代码中看注释吧
```java
/**
 * 直播页面点赞控件，采用SurfaceView绘制
 * 与普通控件使用方法类似，点赞是只需要调用addBubble(int)即可
 * @author huamm
 */
public class PraiseView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder   mHolder;
    private AnimThread      mAnimThread; // 绘制UI的线程
    private Paint           mPaint; // 绘制需要使用的画刷

    private int mWidth; // 控件的宽度
    private int mHeight; // 控件的高度

    private Bitmap[] mDrawables; // 存放需要展示的图
    private int[] mDrawableResIDs; // 存放需要展示的图
    private final LinkedList<Bubble> mTempList = new LinkedList<Bubble>(); // 用于遍历时临时存放
    private final LinkedList<Bubble> mBubbles = new LinkedList<Bubble>(); // 用于存放点赞信息

    private OnBubbleStateListener mOnBubbleStateListener; // 点赞开始和停止的监听

    private Random mRandom = new Random(); // 用于产生随机数

    public PraiseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mPaint = new Paint();

        mDrawableResIDs = new int[] {
                R.drawable.praise_eight,
                R.drawable.praise_one,
                R.drawable.praise_third,
                R.drawable.praise_two,
                R.drawable.praise_five,
                R.drawable.praise_four,
                R.drawable.praise_seven,
                R.drawable.praise_six,
        };
        mDrawables = new Bitmap[mDrawableResIDs.length];
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR); // 清空界面

            mTempList.clear();
            synchronized (mBubbles) {
                mTempList.addAll(mBubbles);
            }
            for (Bubble bubble : mTempList) {
                Coordinates coords = bubble.coordinates;

                // 设置透明度
                if (coords.y < mHeight * 0.25) {
                    if (coords.y < 0) {
                        mPaint.setAlpha(0);
                    } else {
                        mPaint.setAlpha((int) (coords.y * 1020f / mHeight));
                    }
                } else if (coords.y > mHeight * 0.75 && coords.y < mHeight) {
                    mPaint.setAlpha((int) ((1 - coords.y * 1f / mHeight) * 1020));
                } else {
                    mPaint.setAlpha(255);
                }
                // 设置缩放
                if (coords.y < mHeight && coords.y > mHeight*0.75) {
                    bubble.scale = 0.5f + (mHeight - coords.y)*2f/mHeight;
                    canvas.scale(bubble.scale, bubble.scale, mWidth/2, mHeight);
                } else {
                    canvas.scale(1f, 1f);
                }
                canvas.drawBitmap(bubble.bitmap, coords.x, coords.y, mPaint); // 绘制图像

                // 设置下次绘制坐标
                coords.y = coords.y - dip2px(2);
                coords.x = getXbyY(bubble, coords.y);

                // 判断是否已经不可见
                if (coords.y < -dip2px(20)) {
                    synchronized (mBubbles) {
                        mBubbles.remove(bubble);
                        if (mBubbles.size() <= 0 && mOnBubbleStateListener != null) {
                            mOnBubbleStateListener.onEnd();
                        }
                    }
                }
            }
        }
    }

    private int getXbyY(Bubble bubble, int y) {

        // float startx = delta - amplifier * (float) (Math.sin(phase * 2 *
        // (float) Math.PI / 360.0f + 2 * Math.PI * frequency / height * x));
        float startx = bubble.delta - bubble.amplifier * (float) (Math.sin(bubble.data1 + bubble.data2 * y));
        return (int) startx;
    }

    public void addBubble(int count) {
        if (mBubbles.size() <= 0 && mOnBubbleStateListener != null) {
            mOnBubbleStateListener.onStart();
        }
        for (int i = 0; i < count; i++) {
            Bubble bubble = new Bubble(getRandBitmap());
            bubble.delay = mRandom.nextInt(100 * count); // 避免同时增加很多个赞
            bubble.coordinates.x = 0;
            bubble.coordinates.y = mHeight + bubble.delay;
            bubble.delta += bubble.delta - getXbyY(bubble, mHeight);

            synchronized (mBubbles) {
                mBubbles.add(bubble);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mAnimThread = new AnimThread(holder, this);
        mAnimThread.setRunning(true);
        mAnimThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mAnimThread.setRunning(false);
        while (retry) {
            try {
                mAnimThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 随机获取一个bitmap
     * @return
     */
    private Bitmap getRandBitmap() {
        int n = mRandom.nextInt(mDrawableResIDs.length);
        Bitmap bitmap = mDrawables[n];
        if (bitmap == null || bitmap.isRecycled()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inMutable = true;
            bitmap = BitmapFactory.decodeResource(getResources(), mDrawableResIDs[n], opts);
            mDrawables[n] = bitmap;
        }
        return bitmap;
    }

    /**
     * 绘制UI的线程，只要是调用PraiseView.onDraw(canvas);
     * 并且做了锁保护（固定用法，不要轻易修改）
     * ###########################################
     */
    class AnimThread extends Thread {
        private SurfaceHolder holder;
        private PraiseView praiseView;
        private boolean running = true;

        public AnimThread(SurfaceHolder holder, PraiseView snowView) {
            this.praiseView = snowView;
            this.holder = holder;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    synchronized (holder) {
                        praiseView.onDraw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public void setRunning(boolean b) {
            running = b;
        }
    }

    /**
     * 保存赞的信息，主要有坐标信息，路径相关
     * ###########################################
     */
    class Bubble {
        public Bitmap bitmap;
        public Coordinates coordinates;

        // 路径相关
        public int delay; // 延迟距离
        public float frequency  = 1.5f; // 频率
        public float phase      = 45; // 相位
        public float delta      = 500; // 偏移量
        public float amplifier  = 500; // 振幅

        public float scale  = 1.0f; // 缩放

        // 用于缓存计算结果，避免在onDraw中计算过量
        public double data1;
        public double data2;

        public Bubble(Bitmap bitmap) {
            this.bitmap = bitmap;

            float rFloat = mRandom.nextFloat();
            frequency = 0.5f + rFloat*0.5f;
            phase = rFloat * 360;
            amplifier = mWidth / 8 + rFloat * mWidth / 8;

//            int n = dip2px(rFloat * 20);
//            delta = mWidth / 2 + (n % 2 == 1 ? n * (-1) : n); // 偏移量
            delta = mWidth / 2; // 偏移量

            coordinates = new Coordinates();
            coordinates.y = mHeight;

            data1 = phase * 2 * (float) Math.PI / 360.0f;
            data2 = 2 * Math.PI * frequency / mHeight;
        }

        /**
         * Contains the coordinates of the graphic.
         */
        public class Coordinates {
            public int x;
            public int y;
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
     * @param bubbleStateListener
     */
    public void setOnBubbleStateListener(OnBubbleStateListener bubbleStateListener){
        this.mOnBubbleStateListener = bubbleStateListener;
    }

    public interface OnBubbleStateListener {
        public void onStart();
        public void onEnd();
    }
}
```
使用起来与一般控件无差别，直接复制去用吧
[https://github.com/brian512/PraiseView](https://github.com/brian512/PraiseView)