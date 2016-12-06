
package com.brian.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 万能的适配器
 * 1、覆写convert，使用ViewHolder.set*，将数据填充到viewID的控件
 * 2、覆写onInitLayout，可以对从xml导入的布局进行初始化
 * 
 * PS：适用于Item类型单一的列表
 * 摘自http://blog.csdn.net/lmj623565791/article/details/38902805
 * 
 * @author huamm
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int layoutId;
    
    /**
     * 对ItemView进行定制更新的函数
     * 
     * @param holder
     * @param item
     */
    public abstract void convert(ViewHolder holder, T item);
    
    /**
     * 导入layout布局的回调
     * 
     * @param viewHolder
     */
    public void onInitLayout(ViewHolder viewHolder) {}
    

    public CommonAdapter(Context context, int layoutId) {
        this(context, null, layoutId);
    }

    public CommonAdapter(Context context, @Nullable List<T> datas, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        this.layoutId = layoutId;
    }

    /**
     * 绑定数据
     * @param datas
     */
    public void initListWithDatas(List<T> datas) {
        if (mDatas != null) {
            mDatas.clear();
        } else {
            mDatas = new ArrayList<T>();
        }
        
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
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
     * 移除数据对象
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
        if (mDatas != null && data != null) {
            int index = mDatas.indexOf(data); 
            if (index != -1) {
                mDatas.set(index, data);
                
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
    
    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (mDatas == null || position >= mDatas.size()) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
        
        // 回调初始化布局,让用户定制额外的初始化
        if (convertView == null) {
            onInitLayout(holder);
        }
        
        convert(holder, getItem(position));
        return holder.getConvertView();
    }
    
    /**
     * 
     * @author huamm
     */
    public static class ViewHolder {
        private SparseArray<View> mViews;
        private int mPosition;
        private View mConvertView;
        private Context mContext;
        private int mLayoutId;

        public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
            mContext = context;
            mLayoutId = layoutId;
            this.mPosition = position;
            this.mViews = new SparseArray<View>();
            mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            mConvertView.setTag(this);
        }

        public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
            if (convertView == null) {
                return new ViewHolder(context, parent, layoutId, position);
            } else {
                ViewHolder holder = (ViewHolder) convertView.getTag();
                if (holder == null) {
                    return new ViewHolder(context, parent, layoutId, position);
                }
                holder.mPosition = position;
                return holder;
            }
        }

        public int getPosition() {
            return mPosition;
        }

        public int getLayoutId() {
            return mLayoutId;
        }

        /**
         * 通过viewId获取控件
         * 
         * @param viewId
         * @return
         */
        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public View getConvertView() {
            return mConvertView;
        }

        /**
         * 设置TextView的值
         * 
         * @param viewId
         * @param text
         * @return
         */
        public ViewHolder setText(int viewId, String text) {
            TextView tv = getView(viewId);
            tv.setText(text);
            return this;
        }
        
        /**
         * 设置TextView的值
         * 
         * @param viewId
         * @param resId
         * @return
         */
        public ViewHolder setText(int viewId, int resId) {
            TextView tv = getView(viewId);
            tv.setText(resId);
            return this;
        }

        public ViewHolder setImageResource(int viewId, int resId) {
            ImageView view = getView(viewId);
            view.setImageResource(resId);
            return this;
        }

        public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bitmap);
            return this;
        }

        public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
            ImageView view = getView(viewId);
            view.setImageDrawable(drawable);
            return this;
        }

        public ViewHolder setBackgroundColor(int viewId, int color) {
            View view = getView(viewId);
            view.setBackgroundColor(color);
            return this;
        }

        public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
            View view = getView(viewId);
            view.setBackgroundResource(backgroundRes);
            return this;
        }

        public ViewHolder setTextColor(int viewId, int textColor) {
            TextView view = getView(viewId);
            view.setTextColor(textColor);
            return this;
        }

        public ViewHolder setTextColorRes(int viewId, int textColorRes) {
            TextView view = getView(viewId);
            view.setTextColor(mContext.getResources().getColor(textColorRes));
            return this;
        }

        @SuppressLint("NewApi")
        public ViewHolder setAlpha(int viewId, float value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getView(viewId).setAlpha(value);
            } else {
                // Pre-honeycomb hack to set Alpha value
                AlphaAnimation alpha = new AlphaAnimation(value, value);
                alpha.setDuration(0);
                alpha.setFillAfter(true);
                getView(viewId).startAnimation(alpha);
            }
            return this;
        }

        public ViewHolder setVisible(int viewId, boolean visible) {
            View view = getView(viewId);
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            return this;
        }
        
        public ViewHolder setVisible(int viewId, int visibility) {
            View view = getView(viewId);
            view.setVisibility(visibility);
            return this;
        }

        public ViewHolder linkify(int viewId) {
            TextView view = getView(viewId);
            Linkify.addLinks(view, Linkify.ALL);
            return this;
        }

        public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
            for (int viewId : viewIds) {
                TextView view = getView(viewId);
                view.setTypeface(typeface);
                view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
            return this;
        }

        public ViewHolder setProgress(int viewId, int progress) {
            ProgressBar view = getView(viewId);
            view.setProgress(progress);
            return this;
        }

        public ViewHolder setProgress(int viewId, int progress, int max) {
            ProgressBar view = getView(viewId);
            view.setMax(max);
            view.setProgress(progress);
            return this;
        }

        public ViewHolder setMax(int viewId, int max) {
            ProgressBar view = getView(viewId);
            view.setMax(max);
            return this;
        }

        public ViewHolder setRating(int viewId, float rating) {
            RatingBar view = getView(viewId);
            view.setRating(rating);
            return this;
        }

        public ViewHolder setRating(int viewId, float rating, int max) {
            RatingBar view = getView(viewId);
            view.setMax(max);
            view.setRating(rating);
            return this;
        }

        public ViewHolder setTag(int viewId, Object tag) {
            View view = getView(viewId);
            view.setTag(tag);
            return this;
        }

        public ViewHolder setTag(int viewId, int key, Object tag) {
            View view = getView(viewId);
            view.setTag(key, tag);
            return this;
        }

        public ViewHolder setChecked(int viewId, boolean checked) {
            Checkable view =  getView(viewId);
            view.setChecked(checked);
            return this;
        }

        /**
         * 关于事件的
         */
        public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            View view = getView(viewId);
            view.setOnClickListener(listener);
            return this;
        }
        
        public ViewHolder setOnClickListener(View.OnClickListener listener) {
            mConvertView.setOnClickListener(listener);
            return this;
        }

        public ViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
            View view = getView(viewId);
            view.setOnTouchListener(listener);
            return this;
        }

        public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
            View view = getView(viewId);
            view.setOnLongClickListener(listener);
            return this;
        }

    }
}
