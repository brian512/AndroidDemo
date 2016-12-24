package com.brian.common.view.bannerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * 轮播控件中使用的指示器控件，默认是用圆点作为指示器，红色为选中，绿色为未选中。
 * 可通过{@link #setIndicatorRes(int, int)} 和 {@link #setIndicatorColor(int, int)}设置指示器的资源ID或者颜色值
 * {@link #setIndicatorSize(int)}设置指示器的大小
 * {@link #setIndicatorSpaceSize(int)}设置指示器的间距
 * 
 * @author huamm
 */
public class IndicatorLayoutView extends LinearLayout {
    
    private ArrayList<CircleIndicatorView> mIndicatorViews = new ArrayList<>();
    
    /**
     * 指示器未被选中的资源ID
     */
    private int mIndicatorResID_normal   = -1;
    
    /**
     * 指示器被选中的资源ID
     */
    private int mIndicatorResID_selected = -1;
    
    /**
     * 指示器被选中的颜色值
     */
    private int mIndicatorColor_normal   = Color.GREEN;
    
    /**
     * 指示器被选中的颜色值
     */
    private int mIndicatorColor_selected = Color.RED;
    
    /**
     * 指示器的大小
     */
    private int mIndicatorSize           = 10;
    
    /**
     * 指示器之间的间距
     */
    private int mIndicatorSpaceSize      = 10;
    
    /**
     * 当前指示器的位置
     */
    private int mCurrIndicatorPos        = -1;
    
    private Context mContext;
    

    public IndicatorLayoutView(Context context) {
        this(context, null);
    }

    public IndicatorLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initDefault();
    }
    
    private void initDefault() {
        mIndicatorSize = dip2px(mIndicatorSize);
        mIndicatorSpaceSize = dip2px(mIndicatorSpaceSize);
    }

    
    public void updateIndicators(final int count, int initPos) {
        int deta = count;
        if (mIndicatorViews != null) {
            int size = mIndicatorViews.size();
            if (size > count) {
                mIndicatorViews = new ArrayList<>(mIndicatorViews.subList(0, count));
                removeViews(count, size - count);
                deta = 0;
            } else if (size < count) {
                deta = count - size;
            } else {
                deta = 0;
            }
        } else {
            mIndicatorViews = new ArrayList<>();
        }
        for (int i = 0; i < deta; i++) {
            addIndicator(-1);
        }
        setSelectedIndicator(initPos);//初始化位置
    }
    
    protected void addIndicator(int index) {
        LayoutParams params = new LayoutParams(mIndicatorSize, mIndicatorSize);
        CircleIndicatorView view = new CircleIndicatorView(mContext);
        if (mIndicatorViews.isEmpty()) {
            params.leftMargin = 0;
        } else {
            params.leftMargin = mIndicatorSpaceSize;
        }
        view.setLayoutParams(params);
        if (mIndicatorResID_normal != -1) {
            view.setBackgroundResource(mIndicatorResID_normal);
        } else {
            view.setColor(mIndicatorColor_normal);
        }
        if (index >= 0) {
            addView(view, index);
            mIndicatorViews.add(index, view);
        } else {
            addView(view);
            mIndicatorViews.add(view);
        }
    }
    
    
    /**
     * viewpager更新后，更新指示器的状态
     * @param newPos
     */
    protected void setSelectedIndicator(int newPos) {
        if (mIndicatorViews == null) {
            return;
        }
        if (newPos >= 0 && newPos < mIndicatorViews.size()) {
            CircleIndicatorView newDot = mIndicatorViews.get(newPos);
            if (mIndicatorResID_selected != -1) {
                newDot.setBackgroundResource(mIndicatorResID_selected);
            } else {
                newDot.setBackgroundResource(0);
                newDot.setColor(mIndicatorColor_selected);
            }
        }
        
        if (mCurrIndicatorPos == newPos) {
            return;
        }
        
        if (mCurrIndicatorPos >= 0 && mCurrIndicatorPos < mIndicatorViews.size()) {
            CircleIndicatorView oldDot = mIndicatorViews.get(mCurrIndicatorPos);
            if (mIndicatorResID_normal != -1) {
                oldDot.setBackgroundResource(mIndicatorResID_normal);
            } else {
                oldDot.setBackgroundResource(0);
                oldDot.setColor(mIndicatorColor_normal);
            }
        }
        
        mCurrIndicatorPos = newPos;
    }
    
    /**
     * 设置指示器的大小
     * 单位：dp
     * @param size
     */
    public void setIndicatorSize(int size) {
        mIndicatorSize = dip2px(size);
    }
    
    /**
     * 设置指示器的大小
     * 单位：dp
     */
    public void setIndicatorColor(int colorNormal, int colorSelected) {
        mIndicatorColor_normal   = colorNormal;
        mIndicatorColor_selected = colorSelected;
    }
    
    /**
     * 设置指示器的大小
     * 单位：dp
     */
    public void setIndicatorRes(int resNormal, int resSelected) {
        mIndicatorResID_normal   = resNormal;
        mIndicatorResID_selected = resSelected;
    }
    
    
    /**
     * 设置指示器之间的距离
     * 单位：dp
     * @param size
     */
    public void setIndicatorSpaceSize(int size) {
        mIndicatorSpaceSize = dip2px(size);
    }
    
    private int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIndicatorViews.clear();
    }
    
    
    class CircleIndicatorView extends View {
        Paint p = new Paint();
        private int color = Color.WHITE;
        
        public CircleIndicatorView(Context context) {
            this(context, null);
        }
        
        public CircleIndicatorView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        
        public void setColor(int color) {
            this.color = color;
            invalidate();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (getBackground() != null) {
                return;
            }
            int colorBp = p.getColor();
            p.setColor(color);
            int radius = getHeight()/2;
            canvas.drawCircle(radius, radius, radius, p);
            p.setColor(colorBp);
        }
    }
    
}
