package com.brian.testandroid.common.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.brian.testandroid.common.camera.filter.FilterManager;
import com.brian.testandroid.common.camera.gles.FullFrameRect;
import com.brian.testandroid.common.camera.gles.GlUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRecordRenderer implements GLSurfaceView.Renderer {

    private final Context mApplicationContext;
    private int mTextureId = GlUtil.NO_TEXTURE;
    private FullFrameRect mFullScreen; // 绘制的主要实现，包含滤镜、数据处理等
    private SurfaceTexture mSurfaceTexture; // 显示纹理
    private final float[] mSTMatrix = new float[16];

    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;

    public CameraRecordRenderer(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Matrix.setIdentityM(mSTMatrix, 0);
        mFullScreen = new FullFrameRect(FilterManager.getCameraFilter(FilterManager.FilterType.Normal, mApplicationContext));
        mTextureId = GlUtil.createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId); // 通过mTextureId将view和数据绑定
        mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (gl != null) {
            gl.glViewport(0, 0, width, height);
        }

        // 打开相机预览，有数据时就会通过mSurfaceTexture回调回来
        CameraHelperAsy.getInstance().setupCameraAsy(mApplicationContext);
        CameraHelperAsy.getInstance().startPreviewAsy(mSurfaceTexture);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        mFullScreen.drawFrame(mTextureId, mSTMatrix); // 绘制图像
    }

    public void notifyPausing() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mFullScreen != null) {
            mFullScreen.release(false);     // assume the GLSurfaceView EGL context is about
            mFullScreen = null;             // to be destroyed
        }
    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener) {
        mOnFrameAvailableListener = listener;
    }
}
