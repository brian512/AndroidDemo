package com.brian.testandroid.view.swiperecycleview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brian.testandroid.R;

import java.util.List;

/**
 *
 * Created by huamm on 2016/11/10 0010.
 */

public class HorizontalRecyclerView extends RecyclerView {

    private DefaultItemTouchHelper mItemTouchHelper;

    public HorizontalRecyclerView(Context context) {
        this(context, null, 0);
    }
    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);

        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // 横向列表
        setLayoutManager(mLayoutManager);

    }

    public void setItemTouchHelperListener(DefaultItemTouchHelper.OnItemTouchCallbackListener listener, boolean dragable, boolean swipeable) {
        mItemTouchHelper = new DefaultItemTouchHelper(new DefaultItemTouchHelper.DefaultItemTouchHelpCallback(listener));
        mItemTouchHelper.setDragEnable(dragable);
        mItemTouchHelper.setSwipeEnable(swipeable);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public static class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {
        public List<String> datas = null;

        /**
         * Item点击监听
         */
        private OnItemClickLitener mOnItemClickLitener;


        public InnerAdapter(List<String> datas) {
            this.datas = datas;
        }

        public List<String> getDatas() {
            return datas;
        }

        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview, viewGroup, false);
            return new ViewHolder(view);
        }

        /**
         * 将数据与界面进行绑定的操作
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            viewHolder.mTextView.setImageResource(R.drawable.ic_launcher);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, viewHolder.getAdapterPosition());
                }
            });
        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas == null ? 0 : datas.size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mTextView;

            ViewHolder(View view) {
                super(view);
                mTextView = (ImageView) view.findViewById(R.id.item_title);
            }
        }

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

}
