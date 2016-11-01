package com.brian.testandroid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.util.DayNightHelper;

/**
 * 测试夜晚模式
 * Created by huamm on 2016/10/31 0031.
 */

public class DayNightActivity extends BaseActivity {

    private static boolean mIsNightMode = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_daylight);

        Button btn = (Button) findViewById(R.id.btn_switch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsNightMode = !mIsNightMode;
                DayNightHelper.getInstance().setDayNightMode(mIsNightMode);
                recreate();
            }
        });
    }
}
