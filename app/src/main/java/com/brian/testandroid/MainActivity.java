package com.brian.testandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

import com.brian.testandroid.activity.BannerViewActivity;
import com.brian.testandroid.activity.DialogFragmentActivity;
import com.brian.testandroid.activity.DrawerArrowActivity;
import com.brian.testandroid.activity.MarkableProgressBarActivity;
import com.brian.testandroid.activity.PraiseViewActivity;
import com.brian.testandroid.activity.ScrollingImageActivity;
import com.brian.testandroid.activity.TabLayoutActivity;
import com.brian.testandroid.activity.TranslucentStatusBarActivity;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.common.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {

    @BindView(R.id.listview) ListView mListView;

    private CommonAdapter<Item> mAdapter;

    private List<Item> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initListView();

        // 需要在AndroidManifest.xml注册activity
        mDatas.add(new Item(DialogFragmentActivity.class, "测试DialogFragment"));
        mDatas.add(new Item(BannerViewActivity.class, "测试轮播图BannerView"));
        mDatas.add(new Item(PraiseViewActivity.class, "测试点赞效果"));
        mDatas.add(new Item(DrawerArrowActivity.class, "测试DrawerArrow"));
        mDatas.add(new Item(ScrollingImageActivity.class, "测试循环滚动图片"));
        mDatas.add(new Item(MarkableProgressBarActivity.class, "测试可打点进度条"));
        mDatas.add(new Item(TranslucentStatusBarActivity.class, "测试沉浸式状态栏"));
        mDatas.add(new Item(TabLayoutActivity.class, "测试TabLayout"));
        mAdapter.initListWithDatas(mDatas);
    }

    private void initListView() {
        mAdapter = new CommonAdapter<Item>(this, R.layout.item_main_list) {
            @Override
            public void convert(ViewHolder holder, final Item item) {
                holder.setText(R.id.title, item.title);
                holder.setText(R.id.description, item.description);
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(item.clazz);
                    }
                });
            }
        };

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    class Item {
        String title;
        String description;
        Class clazz;

        public Item(String title, String description, Class clazz) {
            this.title = title;
            this.description = description;
            this.clazz = clazz;
        }

        public Item(String title, @NonNull Class clazz) {
            this(title, "", clazz);
        }

        public Item(Class clazz) {
            this(clazz.getSimpleName(), clazz);
        }

        public Item(Class clazz, String description) {
            this(clazz.getSimpleName(), description, clazz);
        }
    }
}
