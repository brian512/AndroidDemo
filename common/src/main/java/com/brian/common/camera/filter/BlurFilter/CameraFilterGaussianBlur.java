package com.brian.common.camera.filter.BlurFilter;

import android.content.Context;

import com.brian.common.camera.filter.CameraFilter;
import com.brian.common.camera.filter.FilterGroup;

public class CameraFilterGaussianBlur extends FilterGroup<CameraFilter> {

    public CameraFilterGaussianBlur(Context context, float blur) {
        super();
        addFilter(new CameraFilterGaussianSingleBlur(context, blur, false));
        addFilter(new CameraFilterGaussianSingleBlur(context, blur, true));
    }
}
