package com.brian.testandroid.activity;

import android.os.Bundle;
import android.view.View;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.util.ToastUtil;
import com.brian.testandroid.view.swiperecycleview.DefaultItemTouchHelper;
import com.brian.testandroid.view.swiperecycleview.HorizontalRecyclerView;

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
    HorizontalRecyclerView mRecyclerView;

    private HorizontalRecyclerView.InnerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        ButterKnife.bind(this);

        //创建默认的线性LayoutManager
        mRecyclerView.setHasFixedSize(true); // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mAdapter = new HorizontalRecyclerView.InnerAdapter(getDummyDatas()); // 创建并设置Adapter
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemTouchHelperListener(new DefaultItemTouchHelper.OnItemTouchCallbackListener() {

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
        }, false, false);
        mAdapter.setOnItemClickLitener(new HorizontalRecyclerView.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtil.showMsg("" + mAdapter.getDatas().get(position));
            }
        });
    }

    private List<String> getDummyDatas() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            datas.add("data:" + i);
        }
        return datas;
    }

}
