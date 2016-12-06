package com.brian.common.camera.filter;

import android.content.Context;

public class FilterManager {

    private FilterManager() {
    }

    public static IFilter getCameraFilter(FilterType filterType, Context context) {
        switch (filterType) {
            case Normal:
            default:
                return new CameraFilter(context);
        }
    }

    public enum FilterType {
        Normal
    }
}
