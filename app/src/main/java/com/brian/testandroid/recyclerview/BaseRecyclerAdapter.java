package com.brian.testandroid.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huamm on 2018/4/23.
 */

public abstract class BaseRecyclerAdapter<T extends BaseType, VH extends BaseRecyclerHolder> extends RecyclerView.Adapter<VH> {
    public static final String TAG = BaseRecyclerAdapter.class.getSimpleName();

    private RecyclerView mRecyclerView;

    protected List<T> mDataList;

    private OnItemClickListener mListener;//点击事件监听器
    private OnItemLongClickListener mLongClickListener;//长按监听器


    public BaseRecyclerAdapter() {
        mDataList = new ArrayList<>();
    }

    public List<T> getData() {
        return mDataList;
    }

    public void bindData(List<T> data) {
        mDataList.clear();
        mDataList.addAll(data);
        notifyDataSetChanged();
    }

    public boolean isEmpty(){
        return mDataList == null || mDataList.isEmpty();
    }


    /**
     * add one item
     */
    public void add(T item) {
        mDataList.add(item);
        notifyItemInserted(mDataList.size());
    }

    public void add(int index, T item) {
        mDataList.add(index, item);
        notifyItemInserted(index);
    }

    /**
     * remove a item
     */
    public void remove(T item) {
        int indexOfCell = mDataList.indexOf(item);
        if (indexOfCell < 0) {
            return;
        }
        remove(indexOfCell);
    }

    public boolean contain(T item) {
        int index = mDataList.indexOf(item);
        return index >= 0;
    }

    public void remove(int index) {
        if (index < 0 || index >= mDataList.size()) {
            throw new IllegalArgumentException("index is out of bound");
        }
        mDataList.remove(index);
        notifyItemRemoved(index);
    }

    public void remove(int start, int count) {
        if (start < 0 || count < 0 || (start + count) > mDataList.size()) {
            throw new IllegalArgumentException("");
        }
        mDataList.removeAll(mDataList.subList(start, start+count));
        notifyItemRangeRemoved(start, count);
    }


    /**
     * add a item list
     *
     */
    public void appendAll(List<T> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        mDataList.addAll(datas);
        notifyItemRangeInserted(mDataList.size() - datas.size(), mDataList.size());
    }

    public void appendAll(int index, List<T> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        mDataList.addAll(index, datas);
        notifyItemRangeInserted(index, index + datas.size());
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }


/////////////////////////////////////////////////////////////////


    //在RecyclerView提供数据的时候调用
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.mRecyclerView = null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null && view != null && mRecyclerView != null) {
                    int position = mRecyclerView.getChildAdapterPosition(view);
                    mListener.onItemClick(mRecyclerView, view, position);
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mLongClickListener != null && view != null && mRecyclerView != null) {
                    int position = mRecyclerView.getChildAdapterPosition(view);
                    mLongClickListener.onItemLongClick(mRecyclerView, view, position);
                    return true;
                }
                return false;
            }
        });
        onBindViewHolder(holder, mDataList.get(position), position);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);

        //释放资源
        int position = holder.getAdapterPosition();
        //越界检查
        if (position < 0 || position >= mDataList.size()) {
            return;
        }
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }



    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).viewType;
    }


    /**
     * 填充RecyclerView适配器的方法，子类需要重写
     *
     * @param holder   ViewHolder
     * @param item     子项
     * @param position 位置
     */
    public abstract void onBindViewHolder(VH holder, T item, int position);


    /**
     * 定义一个点击事件接口回调
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView parent, View view, int position);
    }
}
