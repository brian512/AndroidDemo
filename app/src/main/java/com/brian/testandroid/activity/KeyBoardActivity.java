package com.brian.testandroid.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.brian.common.BaseActivity;
import com.brian.common.util.LogUtil;
import com.brian.common.view.VoiceInputDialog;
import com.brian.testandroid.KeyboardHeightProvider;
import com.brian.testandroid.R;

import java.lang.reflect.Field;

/**
 * 测试键盘相关
 *
 * 如果不希望键盘影响已有的布局效果，需要配置activity如下：
 * android:windowSoftInputMode="stateHidden|adjustNothing"
 */
public class KeyBoardActivity extends BaseActivity {

    private VoiceInputDialog mInputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        findViewById(R.id.ok_btn1).setOnClickListener(mOnClickListener);
        findViewById(R.id.ok_btn2).setOnClickListener(mOnClickListener);
        findViewById(R.id.ok_btn3).setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog();
        }
    };

    private void showDialog() {
        if (mInputDialog == null) {
            mInputDialog = new VoiceInputDialog(this, new VoiceInputDialog.OnInputDialogListener() {
                @Override
                public void onWordsSend(String text) {
                    LogUtil.d("text=" + text);
                    Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                    mInputDialog.clearText();
                }

                @Override
                public void onInputActive(boolean active, int layoutDelta) {
                    LogUtil.d("active=" + active + "; layoutDelta=" + layoutDelta);
                    if (active) {
                        Toast.makeText(getBaseContext(), "键盘高度="+layoutDelta, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), "键盘隐藏", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDismiss() {
                    LogUtil.d("onDismiss=");
                }
            });
        }
        mInputDialog.show();
    }
}
