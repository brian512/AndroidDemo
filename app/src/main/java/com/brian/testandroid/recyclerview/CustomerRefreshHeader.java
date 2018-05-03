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
import com.brian.testandroid.R;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

public class CustomerRefreshHeader extends LinearLayout implements RefreshHeader {

    private ImageView mLoadingIv;
    private ImageView mStaticIv;

    public CustomerRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public CustomerRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View rootLy = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
        mLoadingIv = rootLy.findViewById(R.id.pull_to_refresh_image);
        mStaticIv = rootLy.findViewById(R.id.pull_to_refresh_first_top_image);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        mStaticIv.setVisibility(GONE);
        ((AnimationDrawable) mLoadingIv.getDrawable()).start();
        mLoadingIv.setVisibility(VISIBLE);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        ((AnimationDrawable) mLoadingIv.getDrawable()).stop();
        mStaticIv.setVisibility(VISIBLE);
        mLoadingIv.setVisibility(GONE);
        return 0;//延迟500毫秒之后再弹回
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                break;
            case Refreshing:
                break;
            case ReleaseToRefresh:
                break;
        }
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
