package com.brian.testandroid.activity;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.brian.common.BaseActivity;
import com.brian.common.util.LogUtil;
import com.brian.testandroid.R;
import com.brian.testandroid.qmuitextview.QMUISpanTouchFixTextView;

public class ShapedButtonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaped_buttion);
    }
}
