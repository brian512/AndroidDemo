package com.brian.testandroid.activity;

import android.os.Bundle;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.view.BombView;

/**
 * 测试烟花效果
 */

public class BombViewActivity extends BaseActivity {

    private BombView mBombView;

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            mBombView.startBomb();
            getUIHandler().postDelayed(task, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_bomb_view);

        mBombView = (BombView) findViewById(R.id.bombview);

        getUIHandler().postDelayed(task, 1000);
    }
}
