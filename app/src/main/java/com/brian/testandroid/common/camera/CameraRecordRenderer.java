package com.brian.testandroid.common.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;

import com.brian.testandroid.common.camera.filter.FilterManager;
import com.brian.testandroid.common.camera.gles.FullFrameRect;
import com.brian.testandroid.common.camera.gles.GLUtil;
import com.brian.testandroid.common.camera.video.EncoderConfig;
import com.brian.testandroid.common.camera.video.TextureMovieEncoder;
import com.brian.testandroid.util.LogUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class CameraRecordRenderer implements GLSurfaceView.Renderer {

    public static final int RECORD_STATE_IDEL = 0;
    public static final int RECORD_STATE_INITED = 1;
    public static final int RECORD_STATE_RECORDING= 2;
    public static final int RECORD_STATE_PAUSED= 3;
    public static final int RECORD_STATE_RESUMED = 4;
    public static final int RECORD_STATE_FINISHED= 5;


    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;

    private final Context mApplicationContext;
    private int mTextureId = GLUtil.NO_TEXTURE;
    private FullFrameRect mFullScreen; // 绘制的主要实现，包含滤镜、数据处理等
    private SurfaceTexture mSurfaceTexture; // 显示纹理
    private final float[] mSTMatrix = new float[16];

    private TextureMovieEncoder mVideoEncoder;
    private EncoderConfig mEncoderConfig;

    private int mRecordState = RECORD_STATE_IDEL;
    private float mMvpScaleX = 1f, mMvpScaleY = 1f;



    public CameraRecordRenderer(Context applicationContext) {
        mApplicationContext = applicationContext;

        TextureMovieEncoder.initialize(applicationContext);
        mVideoEncoder = TextureMovieEncoder.getInstance();
    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener) {
        mOnFrameAvailableListener = listener;
    }

    public void setEncoderConfig(EncoderConfig encoderConfig) {
        mEncoderConfig = encoderConfig;
        LogUtil.log(mEncoderConfig.toString());
    }

//    public void setCameraPreviewSize(int width, int height) {
//        mIncomingWidth = width;
//        mIncomingHeight = height;
//
//        float scaleHeight = mSurfaceWidth / (width * 1f / height * 1f);
//        float surfaceHeight = mSurfaceHeight;
//
//        if (mFullScreen != null) {
//            mMvpScaleX = 1f;
//            mMvpScaleY = scaleHeight / surfaceHeight;
//            mFullScreen.scaleMVPMatrix(mMvpScaleX, mMvpScaleY);
//        }
//    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtil.log(config.toString());
        Matrix.setIdentityM(mSTMatrix, 0);
        mFullScreen = new FullFrameRect(FilterManager.getCameraFilter(FilterManager.FilterType.Normal, mApplicationContext));
        mTextureId = GLUtil.createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId); // 通过mTextureId将view和数据绑定
        mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);

        mVideoEncoder.initFilter(FilterManager.FilterType.Normal);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtil.log("width=" + width + "; height=" + height);
        gl.glViewport(0, 0, width, height);

        // 打开相机预览，有数据时就会通过mSurfaceTexture回调回来
        CameraHelperAsy.getInstance().setupCameraAsy(mApplicationContext);
        CameraHelperAsy.getInstance().startPreviewAsy(mSurfaceTexture);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        mFullScreen.drawFrame(mTextureId, mSTMatrix); // 绘制图像

        videoOnDrawFrame(mTextureId, mSTMatrix, mSurfaceTexture.getTimestamp());
    }

    public void release() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mFullScreen != null) {
            mFullScreen.release(false);     // assume the GLSurfaceView EGL context is about
            mFullScreen = null;             // to be destroyed
        }
    }

    private void videoOnDrawFrame(int textureId, float[] texMatrix, long timestamp) {
        if (mEncoderConfig == null) {
            return;
        }
        switch (mRecordState) {
            case RECORD_STATE_INITED:
                mEncoderConfig.updateEglContext(EGL14.eglGetCurrentContext());
                mVideoEncoder.startRecording(mEncoderConfig);
                mVideoEncoder.setTextureId(textureId);
                mVideoEncoder.scaleMVPMatrix(mMvpScaleX, mMvpScaleY);
                mRecordState = RECORD_STATE_RECORDING;
                break;
            case RECORD_STATE_RESUMED:
                mEncoderConfig.updateEglContext(EGL14.eglGetCurrentContext());
                mVideoEncoder.setTextureId(textureId);
                mVideoEncoder.scaleMVPMatrix(mMvpScaleX, mMvpScaleY);
                mRecordState = RECORD_STATE_RECORDING;
                break;
            case RECORD_STATE_PAUSED:
                mVideoEncoder.stopRecording();
                break;
            case RECORD_STATE_IDEL:
            case RECORD_STATE_RECORDING:
                // yay
                break;
            case RECORD_STATE_FINISHED:
                mVideoEncoder.stopRecording();
                mRecordState = RECORD_STATE_IDEL;
                break;
        }

        mVideoEncoder.updateFilter(FilterManager.FilterType.Normal);
        mVideoEncoder.frameAvailable(texMatrix, timestamp);
    }

    public interface OnDrawFrameListener {
        void onDrawFrame(int textureId, float[] texMatrix, long timestamp);
    }

    public void startRecordVideo(String savePath) {
        mRecordState = RECORD_STATE_INITED;
    }

    public void stopRecord() {
        mRecordState = RECORD_STATE_FINISHED;
    }

    public void pauseRecord() {
        mRecordState = RECORD_STATE_PAUSED;
    }

    public void resumeRecord() {
        mRecordState = RECORD_STATE_RESUMED;
    }

    public void takePhoto(String savePath, VideoRecorder.OnTakePicListener listener) {
    }
}
