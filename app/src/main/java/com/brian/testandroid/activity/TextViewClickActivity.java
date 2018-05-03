package com.brian.testandroid.activity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.brian.common.BaseActivity;
import com.brian.common.util.LogUtil;
import com.brian.testandroid.R;

public class TextViewClickActivity extends BaseActivity {

    private static final String TAG = TextViewClickActivity.class.getSimpleName();

    private boolean mClickHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview_click);

        TextView content = (TextView) findViewById(R.id.comment_item_detail_content);
        String string = "我是和常常大声点发大水发送到发送到发";
        SpannableString spannableString = new SpannableString(string);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                LogUtil.showCallStack();
                mClickHandled = true;
            }
        };
        spannableString.setSpan(clickableSpan, 3, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        content.setText(spannableString);

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickHandled) {
                    mClickHandled = false;
                    return;
                }
                LogUtil.showCallStack();
            }
        });
        content.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
