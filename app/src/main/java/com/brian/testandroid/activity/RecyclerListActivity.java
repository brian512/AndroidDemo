package com.brian.testandroid.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brian.common.BaseActivity;
import com.brian.testandroid.R;
import com.brian.testandroid.recyclerview.BaseRecyclerAdapter;
import com.brian.testandroid.recyclerview.BaseRecyclerHolder;
import com.brian.testandroid.recyclerview.BaseType;
import com.brian.testandroid.recyclerview.CustomerRefreshFooter;
import com.brian.testandroid.recyclerview.CustomerRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

/**
 * 测试列表刷新和加载
 * 基于https://github.com/scwang90/SmartRefreshLayout
 */
public class RecyclerListActivity extends BaseActivity {

    private SmartRefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private RecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerlist);

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                fetchDatas(true);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                fetchDatas(false);
            }
        });

        mRefreshLayout.setRefreshHeader(new CustomerRefreshHeader(this));
        mRefreshLayout.setRefreshFooter(new CustomerRefreshFooter(this));

        mRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);

        mRecyclerAdapter = new RecyclerAdapter();

        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private void fetchDatas(final boolean isRefresh) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { // 睡一会儿，模拟网络请求
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                }

                final ArrayList<Data> datas = new ArrayList<>();
                int start = isRefresh ? 0 : (mRecyclerAdapter.getItemCount());
                for (int i=start; i < start+20; i++) {
                    datas.add(new Data("data" + i));
                }

                final boolean isEnd = mRecyclerAdapter.getItemCount() >= 35;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isRefresh) {
                            mRecyclerAdapter.bindData(datas);
                            mRefreshLayout.finishRefresh(true);
                        } else {
                            mRecyclerAdapter.appendAll(datas);
                            mRefreshLayout.finishLoadMore(true);
                        }
                        mRefreshLayout.setNoMoreData(isEnd); // 设置是否还有更多数据
                    }
                });

            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // 开始刷新
        mRefreshLayout.autoRefresh(100);
    }

    static class RecyclerAdapter extends BaseRecyclerAdapter<Data, RecyclerAdapter.DataViewHolder> {

        @Override
        public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, null);
            return new DataViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.DataViewHolder holder, Data item, int position) {
            holder.mTitleTv.setText(mDataList.get(position).title);
        }

        static class DataViewHolder extends BaseRecyclerHolder {

            public TextView mTitleTv;

            public DataViewHolder(View itemView) {
                super(itemView);
                mTitleTv = getTextView(R.id.item_title);
            }
        }

    }

    static class Data extends BaseType {
        public String title;
        public Data(String title) {
            this.title = title;
        }
    }

}
