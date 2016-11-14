package com.brian.testandroid.activity;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.common.camera.CameraHelperAsy;
import com.brian.testandroid.common.camera.CameraSurfaceView;
import com.brian.testandroid.util.PermissionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

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

        PermissionUtil.checkCameraPermission(this);

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
            }
        });
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
