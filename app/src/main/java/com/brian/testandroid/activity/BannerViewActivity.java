package com.brian.testandroid.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.view.bannerview.CyclicRollView;
import com.brian.testandroid.view.bannerview.CyclicViewAdapter;

import java.util.ArrayList;

/**
 * 测试轮播图BannerView
 * Created by Brian on 2016/10/21 0021.
 */
public class BannerViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        CyclicRollView viewPager = new CyclicRollView(this);

        final ArrayList<String> datas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            datas.add("BannerView Item : " + i);
        }

        CyclicViewAdapter<String> adapter = new CyclicViewAdapter<String>(this) {

            @Override
            public View getView(View convertView, int position) {
                TextView view;
                if (convertView != null) {
                    view = (TextView) convertView;
                } else {
                    view = new TextView(BannerViewActivity.this);
                }
                view.setGravity(Gravity.CENTER);
                view.setText(datas.get(position));
                return view;
            }
        };
        viewPager.setSwitchInterval(1500);
        viewPager.setSwitchSpeed(500);
        viewPager.setCurrentItem(2);
        adapter.initListWithDatas(datas);//init data
        viewPager.setAdapter(adapter);

        viewPager.setOnItemClickListener(new CyclicRollView.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(BannerViewActivity.this, "position=" + position, Toast.LENGTH_SHORT).show();
                Toast.makeText(BannerViewActivity.this, "view=" + ((TextView)view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        setContentView(viewPager);
    }
}
