package com.brian.testandroid.activity;

import android.os.Bundle;
import android.view.View;

import com.brian.testandroid.R;
import com.brian.common.BaseActivity;
import com.brian.common.util.DeviceUtil;
import com.brian.common.util.LogUtil;
import com.brian.common.util.ToastUtil;
import com.brian.common.view.swiperecycleview.DefaultItemTouchHelper;
import com.brian.common.view.swiperecycleview.HorizontalRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 测试
 * Created by huamm on 2016/11/4 0004.
 */

public class RecyclerViewActivity extends BaseActivity {

    private HorizontalRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        mRecyclerView = (HorizontalRecyclerView) findViewById(R.id.recycler_view);

        //创建默认的线性LayoutManager
        mRecyclerView.setHasFixedSize(true); // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setPadding(DeviceUtil.getScreenWidth(this)/2, 0, DeviceUtil.getScreenWidth(this)/2, 0);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.getAdapter().bindDatas(getDummyDatas());
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        boolean dragable = true;
        boolean swipeable = true;
        mRecyclerView.setItemTouchHelperListener(new DefaultItemTouchHelper.OnItemTouchCallbackListener() {

            @Override
            public void onSwiped(int adapterPosition) {
                mRecyclerView.getAdapter().getDatas().remove(adapterPosition);
                mRecyclerView.getAdapter().notifyItemRemoved(adapterPosition);
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                // 更换数据源中的数据Item的位置
                Collections.swap(mRecyclerView.getAdapter().getDatas(), srcPosition, targetPosition);

                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mRecyclerView.getAdapter().notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
        }, dragable, swipeable);
        mRecyclerView.setOnItemSelectedListener(new HorizontalRecyclerView.OnItemSelectedLitener() {
            @Override
            public void onItemSelected(View view, int position) {
                mRecyclerView.smoothToCenter(view);
                ToastUtil.showMsg("position=" + position + "; data=" + mRecyclerView.getAdapter().getDatas().get(position));
                LogUtil.d("position=" + position + "; data=" + mRecyclerView.getAdapter().getDatas().get(position));
            }
        });
    }

    private List<String> getDummyDatas() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("data:" + i);
        }
        return datas;
    }

}
