package com.brian.testandroid.recyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brian.common.util.LogUtil;
import com.brian.testandroid.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

public class CustomerRefreshFooter extends LinearLayout implements RefreshFooter {

    private TextView mRefreshTv;
    private TextView mRefreshSubTv;

    private boolean mIsNoMore = false;

    public CustomerRefreshFooter(Context context) {
        this(context, null, 0);
    }

    public CustomerRefreshFooter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerRefreshFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View rootLy = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, this);
        mRefreshTv = rootLy.findViewById(R.id.pull_to_refresh_text);
        mRefreshSubTv = rootLy.findViewById(R.id.pull_to_refresh_sub_text);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        mRefreshTv.setText("正在加载");
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        int delay = 500;
        if (mIsNoMore) {
            mRefreshTv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshTv.setText("没有更多");
                }
            }, delay); // 延迟修改文案，防止最后一次加载完后显示错乱
        }
        if (success) {
            mRefreshTv.setText("加载完成");
        } else {
            mRefreshTv.setText("加载失败");
        }
        return delay;
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        mIsNoMore = noMoreData;
        return true;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        LogUtil.d("newState=" + newState);
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {
    }
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
    }
    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
    }
    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
    }
    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }
    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
    }
}
