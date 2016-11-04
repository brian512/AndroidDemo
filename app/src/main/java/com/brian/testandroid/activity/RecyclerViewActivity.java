package com.brian.testandroid.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.view.swiperecycleview.DefaultItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 测试
 * Created by huamm on 2016/11/4 0004.
 */

public class RecyclerViewActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        ButterKnife.bind(this);

        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true); // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new MyAdapter(getDummyDatas()); // 创建并设置Adapter
        mRecyclerView.setAdapter(mAdapter);
        DefaultItemTouchHelper.OnItemTouchCallbackListener listener = new DefaultItemTouchHelper.OnItemTouchCallbackListener() {

            @Override
            public void onSwiped(int adapterPosition) {
                mAdapter.getDatas().remove(adapterPosition);
                mAdapter.notifyItemRemoved(adapterPosition);
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                // 更换数据源中的数据Item的位置
                Collections.swap(mAdapter.getDatas(), srcPosition, targetPosition);

                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mAdapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
        };
        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(new DefaultItemTouchHelper.DefaultItemTouchHelpCallback(listener));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        itemTouchHelper.setDragEnable(true);
        itemTouchHelper.setSwipeEnable(true);
        mAdapter.setItemTouchHelper(itemTouchHelper);
    }

    private List<String> getDummyDatas() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            datas.add("data:" + i);
        }
        return datas;
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public List<String> datas = null;

        /**
         * Item点击监听
         */
        private AdapterView.OnItemClickListener mItemOnClickListener;

        /**
         * Item拖拽滑动帮助
         */
        private ItemTouchHelper itemTouchHelper;


        public MyAdapter(List<String> datas) {
            this.datas = datas;
        }

        public List<String> getDatas() {
            return datas;
        }

        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview, viewGroup, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mTextView.setText(datas.get(position));
        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.item_title);
            }
        }

        public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
            this.itemTouchHelper = itemTouchHelper;
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            this.mItemOnClickListener = onItemClickListener;
        }
    }
}
