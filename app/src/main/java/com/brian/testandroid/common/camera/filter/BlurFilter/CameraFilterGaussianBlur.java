package com.brian.testandroid.common.camera.filter.BlurFilter;

import android.content.Context;

import com.brian.testandroid.common.camera.filter.CameraFilter;
import com.brian.testandroid.common.camera.filter.FilterGroup;

public class CameraFilterGaussianBlur extends FilterGroup<CameraFilter> {

    public CameraFilterGaussianBlur(Context context, float blur) {
        super();
        addFilter(new CameraFilterGaussianSingleBlur(context, blur, false));
        addFilter(new CameraFilterGaussianSingleBlur(context, blur, true));
    }
}
