package com.brian.testandroid.common.camera.filter;

import android.content.Context;
import android.opengl.GLES10;

import com.brian.testandroid.R;
import com.brian.testandroid.common.camera.gles.GLUtil;

import java.io.InputStream;

public class ImageFilterToneCurve extends CameraFilterToneCurve {

    public ImageFilterToneCurve(Context context, InputStream inputStream) {
        super(context, inputStream);
    }

    @Override
    public int getTextureTarget() {
        return GLES10.GL_TEXTURE_2D;
    }

    @Override
    protected int createProgram(Context applicationContext) {
        return GLUtil.createProgram(applicationContext, R.raw.vertex_shader,
                R.raw.fragment_shader_2d_tone_curve);
    }
}
