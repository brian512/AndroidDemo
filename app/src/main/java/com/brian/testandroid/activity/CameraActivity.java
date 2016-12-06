package com.brian.testandroid.activity;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.brian.testandroid.R;
import com.brian.common.BaseActivity;
import com.brian.common.camera.CameraHelperAsy;
import com.brian.common.camera.CameraRecordRenderer;
import com.brian.common.camera.CameraSurfaceView;
import com.brian.common.camera.video.EncoderConfig;
import com.brian.common.util.PathUtil;
import com.brian.common.PermissionHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 测试相机预览
 * Created by huamm on 2016/11/14 0014.
 */

public class CameraActivity extends BaseActivity {

    @BindView(R.id.camera) CameraSurfaceView mCameraSurfaceView;
    @BindView(R.id.flash) Button mFlashBtn;
    @BindView(R.id.facing) Button mFacingBtn;
    @BindView(R.id.photo) Button mPhotoBtn;
    @BindView(R.id.record) Button mRecordBtn;

    private boolean mIsFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

//        mCameraSurfaceView.setAspectRatio(3, 4);

        PermissionHelper.checkCameraPermission(this, new PermissionHelper.PermissionCallback());

        initListeners();
    }

    private void initListeners() {
        mFlashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsFlashOn) {
                    CameraHelperAsy.getInstance().setFlashModeAsy(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    CameraHelperAsy.getInstance().setFlashModeAsy(Camera.Parameters.FLASH_MODE_ON);
                }
                mIsFlashOn = !mIsFlashOn;
            }
        });
        mFacingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraHelperAsy.getInstance().switchCameraAsy();
            }
        });
        mPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraHelperAsy.getInstance().takePictureAsy(new CameraHelperAsy.TakePictureCallback() {
                    @Override
                    public void onSuccess(Bitmap bm) {

                    }

                    @Override
                    public void onFail(int reason) {

                    }
                });
                mCameraSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        CameraRecordRenderer renderer = mCameraSurfaceView.getRenderer();
                        renderer.stopRecord();
                    }
                });
            }
        });
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        CameraRecordRenderer renderer = mCameraSurfaceView.getRenderer();
                        if (mIsRecording) {
                            renderer.stopRecord();
                        } else {
                            renderer.setEncoderConfig(new EncoderConfig(new File(PathUtil.getCacheDir(), "video-" + System.currentTimeMillis() + ".mp4"), 720, 1280,
                                    2*720*1280 /* 1 Mb/s */));
                            renderer.startRecordVideo("");
                        }
                        mIsRecording = !mIsRecording;
                    }
                });
                if (mRecordBtn.getText().equals("录像")) {
                    mRecordBtn.setText("停止");
                } else {
                    mRecordBtn.setText("录像");
                }
            }
        });
    }

    private boolean mIsRecording = false;

    @Override protected void onResume() {
        super.onResume();
        mCameraSurfaceView.onResume();
    }

    @Override protected void onPause() {
        mCameraSurfaceView.onPause();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
