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
        final LimitedLengthTextView textView = findViewById(R.id.text);
        final String name = "abcdefghijkabcdefghijk";
        final String str = "欢迎%s来到心动女友";
        String text = String.format(str, name);
        textView.setText(text);
        textView.setFitTextRange(text.indexOf(name), text.indexOf(name)+name.length());
    }
}
