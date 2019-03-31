package com.brian.testandroid.activity;

import android.os.Bundle;

import com.brian.common.BaseActivity;
import com.brian.testandroid.ZoomImageView;

public class ShapedButtonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ZoomImageView(this, null));
    }
}
