package com.brian.testandroid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.brian.testandroid.R;
import com.brian.common.BaseActivity;
import com.brian.common.view.AnimImageButton;
import com.brian.common.view.MarkableProgressBar;

public class MarkableProgressBarActivity extends BaseActivity {

    private MarkableProgressBar progressBar;
    private Button textView;
    private Button resetView;

    private AnimImageButton mAnimButton;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_markable_progressbar);
        progressBar = (MarkableProgressBar) findViewById(R.id.progressbar);
        textView = (Button) findViewById(R.id.button);
        resetView = (Button) findViewById(R.id.button_reset);
        mAnimButton = (AnimImageButton) findViewById(R.id.anim_button);

        progressBar.setTime(15_000, 3_000);
        textView.setSelected(false);
        textView.setText("start");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.isSelected()) {
                    textView.setSelected(false);
                    textView.setText("start");
                    progressBar.stopProgress();
                } else {
                    textView.setText("stop");
                    textView.setSelected(true);
                    progressBar.startProgress();
                }
            }
        });

        resetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.resetProgress();
                textView.setSelected(false);
                textView.setText("start");
            }
        });
    }
}
