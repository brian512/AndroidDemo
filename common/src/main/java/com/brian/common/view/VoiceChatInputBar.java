package com.brian.common.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brian.common.R;

public class VoiceChatInputBar extends LinearLayout {

    private EditText mChatEdit;
    private TextView mBtnSend;


    public VoiceChatInputBar(@NonNull Context context) {
        super(context);
    }

    public VoiceChatInputBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceChatInputBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChatEdit = findViewById(R.id.msg_input);
        mBtnSend = findViewById(R.id.msg_send);
    }

    public EditText getChatEdit() {
        return mChatEdit;
    }

    public TextView getChatSend() {
        return mBtnSend;
    }

}
