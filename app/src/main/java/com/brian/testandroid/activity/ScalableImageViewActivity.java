package com.brian.testandroid.activity;

import android.os.Bundle;

import com.brian.common.BaseActivity;
import com.brian.testandroid.R;
import com.brian.common.view.ScalableImageView;

/**
 * Created by brian on 17-7-13.
 */

public class ScalableImageViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scalable_imageview);

        ScalableImageView imageView = (ScalableImageView) findViewById(R.id.scalable_imageview);

    }
}
