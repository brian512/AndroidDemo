package com.brian.common.view.swiperecycleview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.brian.common.R;
import com.brian.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huamm on 2016/11/10 0010.
 */

public class HorizontalRecyclerView extends RecyclerView {

    private DefaultItemTouchHelper mItemTouchHelper;

    /**
     * Item点击监听
     */
    private OnItemSelectedLitener mOnItemSelectedLitener;

    private Scroller mScroller;
    private int mLastx;

    private InnerAdapter mAdapter;

    public HorizontalRecyclerView(Context context) {
        this(context, null, 0);
    }

    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
        mAdapter = new InnerAdapter(null);
        setAdapter(mAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);

        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // 横向列表
        setLayoutManager(mLayoutManager);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    smoothToCenter(mCenterIndex);
                    LogUtil.d("mCenterIndex=" + mCenterIndex);
                    View child = getChildAt(mCenterIndex);
                    mOnItemSelectedLitener.onItemSelected(child, getChildAdapterPosition(child));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (getWidth() <= 0) return;

                float maxScale = 0;
                for (int i = 0, count = getChildCount(); i < count; i++) {
                    View view = getChildAt(i);
                    float scale = 1.2f - Math.abs((view.getLeft() + view.getRight()) / 2 - getWidth() / 2) * 1f / getWidth();

                    view.setScaleX(scale);
                    view.setScaleY(scale);

                    if (scale > maxScale) {
                        maxScale = scale;
                        mCenterIndex = i;
                    }
                }
            }
        });
    }

    public InnerAdapter getAdapter() {
        return mAdapter;
    }

    public void setOnItemSelectedListener(OnItemSelectedLitener listener) {
        mOnItemSelectedLitener = listener;
        mAdapter.setOnItemSelectedListener(mOnItemSelectedLitener);
    }

    private int mCenterIndex = 0;

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller != null && mScroller.computeScrollOffset()) {
            scrollBy(mLastx - mScroller.getCurrX(), 0);
            mLastx = mScroller.getCurrX();
            postInvalidate();//让系统继续重绘，则会继续重复执行computeScroll
        }
    }

    /**
     * 这是库的bug，设置padding后，获取可见第一项的下标会错乱
     * <url>https://code.google.com/p/android/issues/detail?id=181412<url/>
     * Issue 181412:   LinearLayoutManager findFirstVisibleItemPosition() with setClipToPadding(false);
     */
    public void smoothToCenter(View view) {
        for (int i=0, count = getChildCount(); i < count; i++) {
            if (getChildAt(i) == view) {
                smoothToCenter(i);
                break;
            }
        }
    }

    /**
     * 将指定item平滑移动到整个view的中间位置
     */
    public void smoothToCenter(int position) {
        int parentWidth = getWidth();//获取父视图的宽度
        //获取可视范围内的选项的头尾位置
        View targetChild = getChildAt(position);//获取目标item在当前可见视图item集合中的位置
        if (targetChild == null) {
            return;
        }
        int childLeftPx = targetChild.getLeft();//子view相对于父view的左边距
        int childRightPx = targetChild.getRight();//子view相对于父view的右边距

        int childWidth = targetChild.getWidth();
        int centerLeft = parentWidth / 2 - childWidth / 2;//计算子view居中后相对于父view的左边距
        int centerRight = parentWidth / 2 + childWidth / 2;//计算子view居中后相对于父view的右边距
        if (childLeftPx > centerLeft) {//子view左边距比居中view大（说明子view靠父view的右边，此时需要把子view向左平移
            //平移的起始位置就是子view的左边距，平移的距离就是两者之差
            mLastx = childLeftPx;
            mScroller.startScroll(childLeftPx, 0, centerLeft - childLeftPx, 0, 500);//200为移动时长，可自行设定
            postInvalidate();
        } else if (childRightPx < centerRight) {
            mLastx = childRightPx;
            mScroller.startScroll(childRightPx, 0, centerRight - childRightPx, 0, 500);
            postInvalidate();
        }
    }

    public void setItemTouchHelperListener(DefaultItemTouchHelper.OnItemTouchCallbackListener listener, boolean dragable, boolean swipeable) {
        mItemTouchHelper = new DefaultItemTouchHelper(new DefaultItemTouchHelper.DefaultItemTouchHelpCallback(listener));
        mItemTouchHelper.setDragEnable(dragable);
        mItemTouchHelper.setSwipeEnable(swipeable);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public static class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {
        public List<String> mDatas = null;

        private OnItemSelectedLitener mOnItemSelectedLitener;

        public InnerAdapter(List<String> datas) {
            mDatas = new ArrayList<>();
            bindDatas(datas);
        }

        public List<String> getDatas() {
            return mDatas;
        }

        public void bindDatas(List<String> datas) {
            if (datas == null) {
                return;
            }
            if (mDatas == null) {
                mDatas = new ArrayList<>();
            } else {
                mDatas.clear();
            }
            mDatas.addAll(datas);
        }

        public void addDatas(List<String> datas) {
            if (mDatas == null) {
                mDatas = new ArrayList<>();
            }
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return mDatas == null || mDatas.isEmpty();
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
            viewHolder.mTextView.setImageResource(R.mipmap.ic_launcher);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemSelectedLitener.onItemSelected(viewHolder.itemView, viewHolder.getAdapterPosition());
                }
            });
        }

        public void setOnItemSelectedListener(OnItemSelectedLitener listener) {
            mOnItemSelectedLitener = listener;
        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mTextView;

            ViewHolder(View view) {
                super(view);
                mTextView = (ImageView) view.findViewById(R.id.item_title);
            }
        }
    }

    public interface OnItemSelectedLitener {
        void onItemSelected(View view, int position);
    }

}
