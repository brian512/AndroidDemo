
package com.brian.testandroid.view.bannerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * View轮播控件，需要调用{@link #setAdapter(CyclicViewAdapter)}将需要展示的views与数据源绑定。
 * {@link #setSwitchInterval(int)}可设置切换时间间隔
 * {@link #setSwitchSpeed(int)}可设置切换动画时长
 * 
 * {@link #getIndicatorView()}可以获取指示器，对指示器的一些属性进行设置
 * @author huamm
 */
public class CyclicRollView extends FrameLayout {

    /**
     * 默认的切换间隔
     */
    private static final int DEFAULT_INTERVAL    = 3000;
    
    /**
     * 默认的切换动画时长
     */
    private static final int DEFAULT_SCROllDUR   = 300;
    
    /**
     * handler 消息
     */
    private static final int MSG_NEXT            = 0;

    /**
     * 需要轮播的view容器
     */
    private ViewPager mViewPager;
    
    /**
     * 指示器布局容器
     */
    private IndicatorLayoutView mIndicatorLayout;
    
    
    private CyclicViewAdapter<?> mAdapter;
    
    /**
     * 发消息进行自动轮播
     */
    private InnerHandler mHandler;
    
    /**
     * 监听view切换事件
     */
    private OnSwitchListener mOnSwitchListener;
    
    /**
     * 监听点击事件
     */
    private OnItemClickListener mOnItemClickListener;
    
    /**
     * 切换时间间隔
     */
    private int mSwitchInterval        = DEFAULT_INTERVAL;
    
    /**
     * 切换动画时长
     */
    private int mSwitchDur             = DEFAULT_SCROllDUR;;
    
    /**
     * 记录当前页面索引
     */
    private int mCurrPagePos           = 0;
    
    /**
     * 指示器距离底部的距离
     */
    private int mIndicatorBottomMargin = 20;
    
    
    /**
     * 是否自动切换
     */
    private boolean mIsAutoSwitch       = true;
    
    /**
     * 初始化位置，在设置数据源之前先调用{@link #setCurrentItem(int, boolean)}时存储设置的位置
     */
    private int mInitPos               = 0;
    
    private Context mContext;
    
    /**
     * touchdown事件中X坐标
     */
    private int mTouchDownX = 0;
    private int mTouchDownY = 0;

    public CyclicRollView(Context context) {
        this(context, null);
    }

    public CyclicRollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mHandler = new InnerHandler();
        mViewPager = new ViewPager(context);
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {
                    setAutoSwitch(true);
                    if (checkIsClickEvent(event)) {
                        v.performClick();
                    }
                } else {
                    mTouchDownX = (int) event.getX();
                    mTouchDownY = (int) event.getY();
                    mHandler.removeMessages(MSG_NEXT);
                    setAutoSwitch(false);
                }
                return false;
            }
        });
        mViewPager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(mAdapter.getPrimaryItem(), getCurrentPos());
                }
            }
        });
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int newPos) {//动画还未结束
            }
            @Override
            public void onPageScrolled(int newPos, float arg1, int offset) {
                if (offset == 0 && mAdapter != null) {
                    newPos = mAdapter.convert2ListPosition(newPos);
                    mIndicatorLayout.setSelectedIndicator(newPos);
                    if (mOnSwitchListener != null) {
                        mOnSwitchListener.onSwitch(mCurrPagePos, newPos);
                    }
                    mCurrPagePos = newPos;
                }
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
        initDefault();
        
        initUI();
    }
    
    private void initDefault() {
        mIndicatorBottomMargin = dip2px(mIndicatorBottomMargin);
        setSwitchSpeed(DEFAULT_SCROllDUR);
    }
    
    private void initUI() {
        addView(mViewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mIndicatorLayout = new IndicatorLayoutView(mContext);
        mIndicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = mIndicatorBottomMargin;
        addView(mIndicatorLayout, params);
    }
    
    public void onResume() {
        if (mIsAutoSwitch) {
            sendMsg2Switch();
        }
    }
    
    public void onPause() {
        mHandler.removeMessages(MSG_NEXT);
    }
    
    /**
     * 设置数据适配器
     * @param adapter
     */
    public void setAdapter(CyclicViewAdapter<?> adapter) {
        mAdapter = adapter;
        mViewPager.setAdapter(mAdapter);
        final int initPos = correctPosition(mInitPos);
        mViewPager.setCurrentItem(1000*adapter.getViewCount() + initPos);//偏移
        mIndicatorLayout.updateIndicators(mAdapter.getViewCount(), initPos);
        
        sendMsg2Switch();
        
        mAdapter.setOnDataChangedListener(new CyclicViewAdapter.onDataChangedListener() {
            @Override
            public void onDataChanged(int size) {
                if (mAdapter == null || mViewPager == null) {
                    return;
                }
                int currItem = 1000*mAdapter.getViewCount() + mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(currItem);//偏移
                if (size == 1) {
                    setAutoSwitch(false);
                    mIndicatorLayout.setVisibility(View.GONE);
                } else {
                    mIndicatorLayout.setVisibility(View.VISIBLE);
                    mIndicatorLayout.updateIndicators(mAdapter.getViewCount(), initPos);
                }
            }
        });
    }
    
    /**
     * 处理初始化位置，需要用到mViewList，确保mViewList不为空
     * @param position
     * @return
     */
    private int correctPosition(int position) {
        if (position <= 0 || mAdapter.getViewCount() <= 0) {
            position = 0;
        } else if (position >= mAdapter.getViewCount()) {
            position = position % mAdapter.getViewCount();
        }
        return position;
    }
    
    private boolean checkIsClickEvent(MotionEvent event) {
        int touchUpX = (int) event.getX();
        int touchUpY = (int) event.getY();
        return Math.abs(mTouchDownX - touchUpX) < dip2px(10) 
                && Math.abs(mTouchDownY - touchUpY) < dip2px(10);
    }
    
    /**
     * 获取当前展示的位置
     * 
     * @return
     */
    public int getCurrentPos() {
        return mAdapter.convert2ListPosition(mViewPager.getCurrentItem());
    }

    /**
     * 设置是否自动切换
     * 默认为true
     * @param isAuto
     */
    public void setAutoSwitch(boolean isAuto) {
        mIsAutoSwitch = isAuto;
        if (!isAuto) {
            mHandler.removeMessages(MSG_NEXT);
        } else {
            sendMsg2Switch();
        }
    }
    
    private void sendMsg2Switch() {
        if (!mHandler.hasMessages(MSG_NEXT)) {
            mHandler.sendEmptyMessageDelayed(MSG_NEXT, mSwitchInterval);
        }
    }
    
    /**
     * 切换到该位置，可设置是否动画切换
     * 第一项索引为0
     * @param position
     * @param smoothScroll
     */
    public void setCurrentItem(int position, boolean smoothScroll) {
        if (mAdapter == null) {
            mInitPos = position;
            return;
        }
        if (position < 0 || position >= mAdapter.getViewCount()) {
            return;
        }
        mViewPager.setCurrentItem(convert2AdapterPosition(position), smoothScroll);
        mIndicatorLayout.setSelectedIndicator(position);
    }
    
    /**
     * 直接切换到该位置
     * 注意：请先设置数据，否则该设置无效
     * @param position
     */
    public void setCurrentItem(int position) {
        setCurrentItem(position, false);
    }
    
    
    /**
     * 设置点到底部的距离
     * 单位：dp
     * @param size
     */
    public void setIndicatorBottomMargin(int size) {
        mIndicatorBottomMargin = dip2px(size);
    }
    
    public void setOnSwitchListener(OnSwitchListener listener) {
        mOnSwitchListener = listener;
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public IndicatorLayoutView getIndicatorView() {
        return mIndicatorLayout;
    }
    

    /**
     * 设置切换时间间隔
     * @param time
     */
    public void setSwitchInterval(int time) {
        mSwitchInterval = time + mSwitchDur;
    }

    /**
     * 设置切换速度，也就是动画时长
     * @param time
     */
    public void setSwitchSpeed(int time) {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);

            FixedSpeedScroller mScroller = new FixedSpeedScroller(mContext,
                    new AccelerateInterpolator());
            mScroller.setScrollDuration(time);
            mField.set(mViewPager, mScroller);
            mSwitchInterval = mSwitchInterval - mSwitchDur + time;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 将列表中的位置转为带偏移的位置
     * @param position
     * @return
     */
    protected int convert2AdapterPosition(int position) {
        int currentPos = mViewPager.getCurrentItem();
        currentPos = currentPos - currentPos%mAdapter.getViewCount() + position;
        return currentPos;
    }

    @SuppressLint("HandlerLeak")
    private class InnerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (mIsAutoSwitch && msg.what == MSG_NEXT) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);

                Message message = mHandler.obtainMessage(MSG_NEXT);
                mHandler.sendMessageDelayed(message, mSwitchInterval);
            }
        }
    }
    
    private int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mAdapter.onDestroy();
    }
    
    private int mLastX;
    private int mLastY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            intercepted = false;
            break;
        }
        case MotionEvent.ACTION_MOVE: {
            int deltaX = x - mLastX;
            int deltaY = y - mLastY;
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                intercepted = true;
            } else {
                intercepted = false;
            }
            break;
        }
        case MotionEvent.ACTION_UP: {
            intercepted = false;
            break;
        }
        default:
            break;
        }

        mLastX = x;
        mLastY = y;
        return super.onInterceptTouchEvent(event);
    }
    

    class FixedSpeedScroller extends Scroller {
        
        private int mDuration = DEFAULT_SCROllDUR;
        
        public FixedSpeedScroller(Context context) {
            super(context);
        }
        
        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }
        
        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
        
        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
        
        /**
         * 设置滚动时长
         * 
         * @param time
         */
        public void setScrollDuration(int time) {
            mDuration = time;
        }
        
        /**
         * 获取滚动时长
         * 
         * @return
         */
        public int getScrollDuration() {
            return mDuration;
        }
    }
    
    /**
     * 监听viewpager切换页面事件
     * @author huamm
     */
    public interface OnSwitchListener {
        /**
         * 当切换完成时触发该回调
         * @param lastPos
         * @param currPos
         */
        public void onSwitch(int lastPos, int currPos);
    }
    
    /**
     * 监听viewpager的点击事件
     * @author huamm
     */
    public interface OnItemClickListener {
        /**
         * viewpager的子view点击时回调
         * @param view 点击的子view
         * @param position 该项的位置
         */
        public void onClick(View view, int position);
    }
    
}


