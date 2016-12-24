package com.brian.common.camera.filter;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.brian.common.R;
import com.brian.common.camera.gles.GLUtil;

public class CameraFilterBlendSoftLight extends CameraFilterBlend {

    public CameraFilterBlendSoftLight(Context context, @DrawableRes int drawableId) {
        super(context, drawableId);
    }

    @Override
    protected int createProgram(Context applicationContext) {

        return GLUtil.createProgram(applicationContext, R.raw.vertex_shader_two_input,
                R.raw.fragment_shader_ext_blend_soft_light);
    }
}