package com.brian.common.view.frameanimation;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.brian.common.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 控制帧动画序列帧加载，采用inBitmap复用内存，需要保证传入的图片序列尺寸相同
 *
 * @author brian512
 */
public class FrameAnimation {

    private static final String TAG = "FrameAnimation";

    /**
     * Repeat the animation once.
     */
    public static final int MODE_ONCE = 1;
    /**
     * Repeat the animation indefinitely.
     */
    public static final int MODE_INFINITE = 2;


    /**
     * Animation状态监听
     */
    public interface AnimationStateListener {
        /**
         * 动画开始
         */
        void onStart();

        /**
         * 动画结束
         */
        void onStop();

        /**
         * 动画循环
         */
        void onRepeat();
    }


    private boolean mInited = false;

    private ImageView mImageView;

    private List<String> mImageList = new ArrayList<>();

    private int mFrameInterval = 60;

    private boolean mIsRunning = false;

    private boolean mRepeat = true;

    private int mCurrIndex = 0;

    private boolean mForceUpdate = false;

    private AnimationStateListener mAnimationListener;

    private HashMap<String, Bitmap> mBitmapHashMap = new HashMap<>();

    private static final int MSG_FRAME_NEXT = 123;

    private HandlerThread mHandlerThread = new HandlerThread("anim");
    {
        mHandlerThread.start();
    }

    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    private Handler mDecodeHandler = new Handler(mHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FRAME_NEXT:
                    String imagePath = mImageList.get(msg.arg1);
                    if (!mBitmapHashMap.containsKey(imagePath)) {
                        String imageReuse = mImageList.get((msg.arg1+mImageList.size() - 2)%mImageList.size());

                        Bitmap bitmap = decodeBitmap(imagePath, mBitmapHashMap.remove(imageReuse));
                        mBitmapHashMap.put(imagePath, bitmap);
                    }
                    if (mForceUpdate) {
                        mUIHandler.removeCallbacks(mUpdateTask);
                        mUIHandler.post(mUpdateTask);
                    }
                    break;
            }
        }
    };


    public void init(@NonNull ImageView imageView,
                     @NonNull List<String> imagePathList,
                     @IntRange(from = 1)int frameInterval) {
        stop();
        mImageView = imageView;
        mImageList.clear();
        mImageList.addAll(imagePathList);
        mFrameInterval = frameInterval;

        final ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /*
                 * 通过这样一个监听事件，我们就可以获取
                 * 到ValueAnimator每一步所产生的值。
                 *
                 * 通过调用getAnimatedValue()获取到每个时间因子所产生的Value。
                 * */
                Integer value = (Integer) animation.getAnimatedValue();
            }
        });
        animator.start();

        mBitmapHashMap.clear();

        mInited = true;
    }

    public void start() {
        if (!mInited) {
            throw new IllegalStateException("init first");
        }
        if (mIsRunning) {
            return;
        }
        mCurrIndex = 0; // 从第一帧开始
        mIsRunning = true;

        mForceUpdate = true; // 强制更新，否则只是加载图片不会绘制
        prepareNextFrame();

        if (mAnimationListener != null) {
            mAnimationListener.onStart();
        }
    }

    public void stop() {
        mIsRunning = false;
        mDecodeHandler.removeCallbacksAndMessages(null);
        mUIHandler.removeCallbacksAndMessages(null);
        if (mAnimationListener != null) {
            mAnimationListener.onStop();
        }
    }

    public void setAnimationListener(AnimationStateListener listener) {
        mAnimationListener = listener;
    }

    public void release() {
        stop();
        mInited = false;
        mBitmapHashMap.clear();
        mImageList.clear();
        mHandlerThread.quit();
    }

    public void setRepeat(boolean repeat) {
        mRepeat = repeat;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            String imagePath = mImageList.get(mCurrIndex);
            Bitmap bitmap = mBitmapHashMap.get(imagePath);
            if (bitmap != null) {
                mForceUpdate = false;
                updateFrame(bitmap);

                if (needDrawNextFrame()) {
                    if (mAnimationListener != null && mCurrIndex == mImageList.size()-1) {
                        mAnimationListener.onRepeat();
                    }
                    mCurrIndex = (mCurrIndex+1) % mImageList.size();
                    LogUtil.d(TAG, "mCurrIndex=" + mCurrIndex);
                    mUIHandler.postDelayed(mUpdateTask, mFrameInterval);
                    prepareNextFrame();
                }
            } else {
                // 如果到更新时间时，图片还没有准备好，等图片准备好了强制更新，不必等下一次绘制
                mForceUpdate = true;
            }
        }
    };

    private boolean needDrawNextFrame() {
        // 正在运行，且还没有循环结束
        return mIsRunning && (mRepeat || mCurrIndex < mImageList.size()-1);
    }

    private void prepareNextFrame() {
        Message message = mDecodeHandler.obtainMessage(MSG_FRAME_NEXT);
        message.arg1 = mCurrIndex;
        mDecodeHandler.sendMessage(message);
    }

    private void updateFrame(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    private Bitmap decodeBitmap(String imagePath, Bitmap inBitmap) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inMutable = true;
        if (inBitmap != null) {
            opts.inBitmap = inBitmap;
        }
        return BitmapFactory.decodeFile(imagePath, opts);
    }
}
