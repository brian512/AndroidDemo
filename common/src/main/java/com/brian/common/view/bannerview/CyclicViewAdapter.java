package com.brian.common.view.bannerview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 使用views轮播控件{@link CyclicRollView}需要实现该类的abstract方法{@link #getView(View, int)} 和 {@link #getViewCount()}
 * @author huamm
 * @param <T> 需要展示的数据源类型
 */
public abstract class CyclicViewAdapter<T> extends PagerAdapter {
    private static final String TAG = CyclicViewAdapter.class.getSimpleName();
    
    private static final int MAX_RECYLE_VIEW = 3;
    private LinkedList<View> mViewCache = null;
    private View mCurrentView = null;
    
    private onDataChangedListener mOnDataChangedListener;
    
    private List<T> mDatas;
    
    public CyclicViewAdapter(Context context) {
        mViewCache = new LinkedList<View>();
        mDatas = new ArrayList<T>();
    }
    
    /**
     * 绑定数据
     * @param datas
     */
    public void initListWithDatas(List<T> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        
        if (mDatas != null) {
            mDatas.clear();
        } else {
            mDatas = new ArrayList<T>();
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
    
    /**
     * 批量添加数据
     * 
     * PS：没有进行去重检查
     * @param datas
     */
    public void addDatas(List<T> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
    
    /**
     * 添加数据
     * PS：没有进行去重检查
     * 
     * @param data
     */
    public void addData(T data) {
        if (data == null) {
            return;
        }
        
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        mDatas.add(data);
        notifyDataSetChanged();
    }
    
    /**
     * 移除相等数据对象
     * 
     * PS：包括同一个对象和equal相等的对象
     * 
     * @param data
     */
    public void removeData(T data) {
        if (data == null) {
            return;
        }
        
        if (mDatas != null) {
            boolean isRemove = mDatas.remove(data);
            if (isRemove) {
                notifyDataSetChanged();
            }
        }
    }
    
    /**
     * 移除指定位置的数据
     * 
     * @param pos
     */
    public void removeDataAt(int pos) {
        if (mDatas != null && pos >= 0 && pos < mDatas.size()) {
            mDatas.remove(pos);
            notifyDataSetChanged();
        }
    }
    
    /**
     * 移除所有数据
     */
    public void removeAllDatas() {
        if (mDatas != null) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }
    
    /**
     * 更新数据
     * PS：使用替换的方式来进行更新
     * 
     * @param data
     */
    public void updateData(T data) {
        if (mDatas != null) {
            int index = mDatas.indexOf(data); 
            if (index != -1) {
                mDatas.remove(index);
                mDatas.add(index, data);
                
                notifyDataSetChanged();
            }
        }
    }
    
    /**
     * 获取全部数据
     * @return
     */
    public List<T> getDatas() {
        return mDatas;
    }
    
    public boolean isDataEmpty() {
        return mDatas == null || mDatas.isEmpty();
    }
    
    /**
     * 返回Integer.MAX_VALUE，实现 “无限循环滚动”
     */
    @Override
    public final int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public  boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View contentView = (View) object;
        if (mViewCache.size() < MAX_RECYLE_VIEW) {
            mViewCache.add(contentView);
        }
        ((ViewPager) container).removeView(contentView);
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        // 先对position进行偏移矫正
        position = convert2ListPosition(position);
        View convertView = null;
        if (!mViewCache.isEmpty()) {
            convertView = mViewCache.removeFirst();
        }
        
        // 让调用者定义该convertview，与数据源解绑
        View child = getView(convertView, position);
        if (child == null) {
            return convertView;
        }
        
        // 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = child.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(child);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        container.addView(child, params);
        // add listeners here if necessary
        return child;
    }
    
    /**
     * 获取view的总数，区别于{@link #getCount()}}
     * @return
     */
    public int getViewCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }
    
    /**
     * 使用者实现该方法，用于绑定数据源和convertView
     * @param convertView
     * @param position
     * @return
     */
    public abstract View getView(View convertView, int position);
    
    /**
     * 设置并保存当前显示的view
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        View view = (View) object;
        if (view != mCurrentView) {
            mCurrentView = view;
        }
    }
    
    /**
     * 设置数据变化监听
     * @param listener
     */
    public void setOnDataChangedListener(onDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }
    
    /**
     * 获取当前展示的view
     * @return
     */
    public View getPrimaryItem() {
        return mCurrentView;
    }
    
    /**
     * 将偏移位置转为列表中的位置
     * @param position
     * @return
     */
    protected int convert2ListPosition(int position) {
        if (getViewCount() <= 0) {
            return 0;
        }
        while (position < 0) {
            position = getViewCount() + position;
        }
        position %= getViewCount();
        return position;
    }
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mOnDataChangedListener != null) {
            mOnDataChangedListener.onDataChanged(mDatas == null ? 0 : mDatas.size());
        }
    }
    
    void onDestroy() {
        if (mViewCache != null) {
            mViewCache.clear();
        }
    }
    
    /**
     * 数据变化监听
     * @author huamm
     */
    public interface onDataChangedListener {
        /**
         * 数据变化是回调
         * @param size 数据改变后的大小
         */
        public void onDataChanged(int size);
    }
}