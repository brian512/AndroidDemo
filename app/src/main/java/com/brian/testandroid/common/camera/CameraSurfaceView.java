package com.brian.testandroid.common.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

public class CameraSurfaceView extends AutoFitGLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    private CameraRecordRenderer mCameraRenderer;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2); // 设置OpenGL ES版本为2.0

        mCameraRenderer = new CameraRecordRenderer(context);
        mCameraRenderer.setOnFrameAvailableListener(this);
        setRenderer(mCameraRenderer); // 设置
        setRenderMode(RENDERMODE_WHEN_DIRTY); // 有数据更新 或者 requestRender时才更新
    }

    public CameraRecordRenderer getRenderer() {
        return mCameraRenderer;
    }

    @Override
    public void onPause() {
        CameraHelperAsy.getInstance().closeCameraAsy();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                // 跨进程 清空 Renderer数据
                mCameraRenderer.release();
            }
        });

        super.onPause();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender(); // 有数据来时请求渲染
    }

}