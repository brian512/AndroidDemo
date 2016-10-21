package com.brian.testandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;

import com.brian.testandroid.R;
import com.brian.testandroid.util.ResourceUtil;
import com.brian.testandroid.view.DrawerArrowDrawable;
import com.brian.testandroid.view.SwitchDrawable;

/**
 * 测试DrawerArrow
 * Created by Brian on 2016/10/21 0021.
 */
public class DrawerArrowActivity extends Activity {
    private SwitchDrawable mSwitchDrawable;
    private DrawerArrowDrawable mDrawerArrowDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_arrow);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final View drawerView = findViewById(R.id.drawer_content);
        final ImageView imageView1 = (ImageView) findViewById(R.id.drawer_indicator1);
        final ImageView imageView2 = (ImageView) findViewById(R.id.drawer_indicator2);

        mSwitchDrawable = new SwitchDrawable(this);
        mDrawerArrowDrawable = new DrawerArrowDrawable(this);
        mDrawerArrowDrawable.setColor(ResourceUtil.getColor(R.color.light_gray));
        mSwitchDrawable.setColor(ResourceUtil.getColor(R.color.light_gray));
        imageView1.setImageDrawable(mSwitchDrawable);
        imageView2.setImageDrawable(mDrawerArrowDrawable);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mSwitchDrawable.setProgress(slideOffset);
                mDrawerArrowDrawable.setProgress(slideOffset);
            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.closeDrawer(drawerView);
                } else {
                    drawerLayout.openDrawer(drawerView);
                }
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.closeDrawer(drawerView);
                } else {
                    drawerLayout.openDrawer(drawerView);
                }
            }
        });
    }
}
