package com.brian.testandroid.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by huamm on 2018/1/3.
 */

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;

    public BaseRecyclerHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();

    }

    /**
     * 获取ItemView
     */
    public View getItemView() {
        return itemView;
    }

    public View getView(int resId) {
        return getViewById(resId);
    }

    public TextView getTextView(int resId) {
        return getViewById(resId);
    }

    public ImageView getImageView(int resId) {
        return getViewById(resId);
    }

    public Button getButton(int resId) {
        return getViewById(resId);
    }

    public <V extends View> V getViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (V) view;
    }

    public void setText(int resId, CharSequence text) {
        getTextView(resId).setText(text);
    }

    public void setText(int resId, int strId) {
        getTextView(resId).setText(strId);
    }

    public void onViewDetachedFromWindow(){}

}
