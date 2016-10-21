package com.brian.testandroid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.view.PraiseView;

/**
 * 测试点赞效果
 * Created by Brian on 2016/10/21 0021.
 */

public class PraiseViewActivity extends BaseActivity {

    private PraiseView mPraiseView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_praise_view);

        mPraiseView = (PraiseView) findViewById(R.id.bubble);
        mPraiseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPraiseView.addBubble(1);
            }
        });

        Toast.makeText(this, "Just click screen ! ", Toast.LENGTH_SHORT).show();
    }
}
