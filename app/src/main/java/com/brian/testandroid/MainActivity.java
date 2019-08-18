package com.brian.testandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

import com.brian.common.BaseActivity;
import com.brian.common.CommonAdapter;
import com.brian.testandroid.activity.AudioStateActivity;
import com.brian.testandroid.activity.BannerViewActivity;
import com.brian.testandroid.activity.BombViewActivity;
import com.brian.testandroid.activity.CameraActivity;
import com.brian.testandroid.activity.ConstrainLayoutDemoAcitivty;
import com.brian.testandroid.activity.CustomClickActivity;
import com.brian.testandroid.activity.DayNightActivity;
import com.brian.testandroid.activity.DialogFragmentActivity;
import com.brian.testandroid.activity.DrawerArrowActivity;
import com.brian.testandroid.activity.FrameAnimActivity;
import com.brian.testandroid.activity.FtpFilesActivity;
import com.brian.testandroid.activity.KeyBoardActivity;
import com.brian.testandroid.activity.LimitedLengthTextViewActivity;
import com.brian.testandroid.activity.LottieAnimActivity;
import com.brian.testandroid.activity.MarkableProgressBarActivity;
import com.brian.testandroid.activity.NotificationActivity;
import com.brian.testandroid.activity.PermissionsActivity;
import com.brian.testandroid.activity.PraiseViewActivity;
import com.brian.testandroid.activity.PushActivity;
import com.brian.testandroid.activity.QMUITextViewActivity;
import com.brian.testandroid.activity.RecyclerListActivity;
import com.brian.testandroid.activity.RecyclerViewActivity;
import com.brian.testandroid.activity.ScalableImageViewActivity;
import com.brian.testandroid.activity.ScrollingImageActivity;
import com.brian.testandroid.activity.ShapedButtonActivity;
import com.brian.testandroid.activity.TabLayoutActivity;
import com.brian.testandroid.activity.TextViewClickActivity;
import com.brian.testandroid.activity.TranslucentStatusBarActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ListView mListView;

    private CommonAdapter<Item> mAdapter;

    private List<Item> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);
        initListView();

        // 需要在AndroidManifest.xml注册activity
        mDatas.add(new Item(NotificationActivity.class, "NotificationActivity"));
        mDatas.add(new Item(ConstrainLayoutDemoAcitivty.class, "ConstrainLayoutDemoAcitivty"));
        mDatas.add(new Item(CustomClickActivity.class, "CustomClickActivity"));
        mDatas.add(new Item(LimitedLengthTextViewActivity.class, "LimitedLengthTextViewActivity"));
        mDatas.add(new Item(LottieAnimActivity.class, "Lottie动画"));
        mDatas.add(new Item(ShapedButtonActivity.class, "ShapedButtonActivity"));
        mDatas.add(new Item(FrameAnimActivity.class, "FrameAnimActivity"));
        mDatas.add(new Item(AudioStateActivity.class, "AudioStateActivity"));
        mDatas.add(new Item(QMUITextViewActivity.class, "测试QMUI中的TextView控件"));
        mDatas.add(new Item(TextViewClickActivity.class, "测试TextView点击事件"));
        mDatas.add(new Item(RecyclerListActivity.class, "测试列表刷新和加载"));
        mDatas.add(new Item(KeyBoardActivity.class, "测试软键盘相关"));
        mDatas.add(new Item(ScalableImageViewActivity.class, "测试可缩放，拖拽的imageview"));
        mDatas.add(new Item(PushActivity.class, "测试消息推送"));
        mDatas.add(new Item(FtpFilesActivity.class, "测试ftp访问"));
        mDatas.add(new Item(PermissionsActivity.class, "测试权限请求"));
        mDatas.add(new Item(CameraActivity.class, "测试相机预览"));
        mDatas.add(new Item(RecyclerViewActivity.class, "测试RecyclerView"));
        mDatas.add(new Item(DayNightActivity.class, "测试夜晚模式"));
        mDatas.add(new Item(DialogFragmentActivity.class, "测试DialogFragment"));
        mDatas.add(new Item(BannerViewActivity.class, "测试轮播图BannerView"));
        mDatas.add(new Item(PraiseViewActivity.class, "测试点赞效果"));
        mDatas.add(new Item(BombViewActivity.class, "测试烟花效果"));
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
