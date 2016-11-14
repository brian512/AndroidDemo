package com.brian.testandroid.common.camera.filter.BlurFilter;

import android.content.Context;

import com.brian.testandroid.common.camera.filter.CameraFilter;
import com.brian.testandroid.common.camera.filter.FilterGroup;

public class ImageFilterGaussianBlur extends FilterGroup<CameraFilter> {

    public ImageFilterGaussianBlur(Context context, float blur) {
        super();
        addFilter(new ImageFilterGaussianSingleBlur(context, blur, false));
        addFilter(new ImageFilterGaussianSingleBlur(context, blur, true));
    }
}
