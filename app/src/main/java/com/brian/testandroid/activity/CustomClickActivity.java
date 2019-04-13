package com.brian.testandroid.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.brian.common.BaseActivity;
import com.brian.common.util.LogUtil;
import com.brian.testandroid.R;

/**
 * @author Brian on 2019/4/13.
 */
public class CustomClickActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_click);


        CustomClickListener listener = new CustomClickListener() {
            @Override
            protected void onClick() {
                LogUtil.d("onClick");
            }
            @Override
            protected void onClickCount(int count) {
                LogUtil.d("onClickCount:" + count);
            }
            @Override
            protected void onClickFinished(int count) {
                LogUtil.d("onClickFinished:" + count);
            }
        };
        listener.setClickDuration(500);
        findViewById(R.id.button).setOnClickListener(listener);
    }

    public static class CustomClickListener implements View.OnClickListener {

        private Handler mHandler = new Handler(Looper.getMainLooper());

        private int mClickDuration = 800;

        private int mClickCount = 0;


        /**
         * 设置连击超时时长
         * @param durationMillis 超时时长
         */
        public void setClickDuration(int durationMillis) {
            mClickDuration = durationMillis;
        }

        private Runnable mResetTask = new Runnable() {
            @Override
            public void run() {
                if (mClickCount > 0) {
                    onClickFinished(mClickCount);
                }
                mClickCount = 0;
            }
        };

        @Override
        public void onClick(View v) {
            mClickCount++;

            onClick();
            onClickCount(mClickCount);

            mHandler.removeCallbacks(mResetTask);
            mHandler.postDelayed(mResetTask, mClickDuration);
        }


        /**
         * 每次点击时回调
         */
        protected void onClick() {
        }

        /**
         * 每次点击时回调
         * @param count 连击次数
         */
        protected void onClickCount(int count) {
        }

        /**
         * 连击结束时回调
         * @param count 连击次数
         */
        protected void onClickFinished(int count) {
        }

    }

}
