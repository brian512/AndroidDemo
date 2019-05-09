package com.brian.testandroid.activity;

import android.os.Bundle;

import com.brian.common.BaseActivity;
import com.brian.common.view.LimitedLengthTextView;
import com.brian.testandroid.R;

public class LimitedLengthTextViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limited_length_text);
        final LimitedLengthTextView textView = (LimitedLengthTextView) findViewById(R.id.text);

    }
}
