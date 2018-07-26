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
import com.brian.testandroid.qmuitextview.QMUISpanTouchFixTextView;

public class QMUITextViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QMUISpanTouchFixTextView textView = new QMUISpanTouchFixTextView(this);

        String text = "hhhhhh哈哈哈哈";
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#B057FF"));
        SpannableStringBuilder colorText = new SpannableStringBuilder(text);
        colorText.setSpan(colorSpan,0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(colorText);
        setContentView(textView);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void setAudioMode(Context context, boolean inCommunication) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            LogUtil.d("audioManager=null");
            return;
        }
        if (inCommunication) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }
}
