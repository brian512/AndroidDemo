package com.brian.testandroid.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.brian.common.BaseActivity;
import com.brian.common.util.LogUtil;
import com.brian.testandroid.KeyboardHeightProvider;
import com.brian.testandroid.R;

import java.lang.reflect.Field;

/**
 * 测试键盘相关
 *
 * 如果不希望键盘影响已有的布局效果，需要配置activity如下：
 * android:windowSoftInputMode="stateHidden|adjustNothing"
 * 然而，如此配置之后，ViewTreeObserver.OnGlobalLayoutListener.onGlobalLayout()就不会被调用，故而传统获取键盘高度的方式失效
 *
 * 此种方式获取键盘高度的原理是在界面弹出一个 宽度为0 高度全屏 的PopupWindow，通过PopupWindow去感知键盘的显示与隐藏引起的可视区高度变化
 *
 * 注意，由于在界面添加了PopupWindow，所以在生命周期回调里需要处理PopupWindow的状态
 */
public class KeyBoardActivity extends BaseActivity {

    private View mInputLy;

    private EditText mEditText;

    private int mOriginMarginBottom = -1;

    private KeyboardHeightProvider mKeyboardHeightProvider;

    private KeyboardHeightProvider.KeyboardHeightObserver mKeyboardHeightObserver = new KeyboardHeightProvider.KeyboardHeightObserver() {
        @Override
        public void onKeyboardHeightChanged(int height, int orientation) {
            LogUtil.d("height=" + height);
//            // way 1
//            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mInputLy.getLayoutParams();
//            if (mOriginMarginBottom < 0) {
//                mOriginMarginBottom = params.bottomMargin;
//            }
//            if (height > 0) {
//                params.bottomMargin += height;
//            } else {
//                params.bottomMargin = mOriginMarginBottom;
//            }
//            mInputLy.setLayoutParams(params);

            // way 2
            if (height > 0) {
                mInputLy.setTranslationY(-height);
            } else {
                mInputLy.setTranslationY(0);
            }

            mEditText.setText("KeyboardHeight=" + height);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        mInputLy = findViewById(R.id.input_ly);
        mEditText = (EditText) findViewById(R.id.input);

        mKeyboardHeightProvider = new KeyboardHeightProvider(this);
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                mKeyboardHeightProvider.start();
            }
        }, 300);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mKeyboardHeightProvider != null) {
            mKeyboardHeightProvider.setKeyboardHeightObserver(mKeyboardHeightObserver);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mKeyboardHeightProvider != null) {
            mKeyboardHeightProvider.setKeyboardHeightObserver(null);
            mKeyboardHeightObserver.onKeyboardHeightChanged(0, mKeyboardHeightProvider.getScreenOrientation());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mKeyboardHeightProvider != null) {
            mKeyboardHeightProvider.close();
        }
    }
}
