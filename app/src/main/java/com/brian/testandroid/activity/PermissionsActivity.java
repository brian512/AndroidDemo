package com.brian.testandroid.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brian.testandroid.R;
import com.brian.common.BaseActivity;
import com.brian.common.PermissionHelper;
import com.brian.common.util.LogUtil;
import com.brian.common.util.PermissionUtil;
import com.brian.common.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 测试权限请求
 * Created by brian on 2016/12/4.
 */

public class PermissionsActivity extends BaseActivity {

    @BindView(R.id.request_location)
    Button mRequestLocation;

    @BindView(R.id.request_audio)
    Button mRequestAudio;

    @BindView(R.id.request_camera)
    Button mRequestCamera;

    @BindView(R.id.request_record)
    Button mRequestRecord;

    @BindView(R.id.request_location_result)
    TextView mRequestLocationResult;

    @BindView(R.id.request_audio_result)
    TextView mRequestAudioResult;

    @BindView(R.id.request_camera_result)
    TextView mRequestCameraResult;

    @BindView(R.id.request_record_result)
    TextView mRequestRecordResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        ButterKnife.bind(this);

        refreshPermissionState();

        final PermissionHelper.PermissionCallback callback = new PermissionHelper.PermissionCallback() {

            @Override
            public void onAllPermissionsGranted(int requestCode) {
                switch (requestCode) {
                    case PermissionHelper.PERMISSION_REQUEST_CODE_CAMERA:
                        ToastUtil.showMsg("已获取相机权限");
                        break;
                    case PermissionHelper.PERMISSION_REQUEST_CODE_AUDIO:
                        ToastUtil.showMsg("已获取录音权限");
                        break;
                    case PermissionHelper.PERMISSION_REQUEST_CODE_CAMERA_AND_AUDIO:
                        ToastUtil.showMsg("已获取相机和录音权限");
                        break;
                    case PermissionHelper.PERMISSION_REQUEST_CODE_LOCATION:
                        ToastUtil.showMsg("已获取定位权限");
                        break;
                }

            }
        };

        mRequestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionHelper.checkLocationPermission(PermissionsActivity.this, callback)) {
                    ToastUtil.showMsg("已获取定位权限");
                }
            }
        });

        mRequestCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionHelper.checkCameraPermission(PermissionsActivity.this, callback)) {
                    ToastUtil.showMsg("已获取相机权限");
                }
            }
        });

        mRequestAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionHelper.checkAudioPermission(PermissionsActivity.this, callback)) {
                    ToastUtil.showMsg("已获取录音权限");
                }
            }
        });

        mRequestRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionHelper.checkRecordPermission(PermissionsActivity.this, callback)) {
                    ToastUtil.showMsg("已获取相机和录音权限");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final PermissionHelper.PermissionCallback callback = new PermissionHelper.PermissionCallback() {

            @Override
            public void onAllPermissionsGranted(int requestCode) {
                ToastUtil.showMsg("已获取手机状态和读写权限");
            }
        };
        PermissionHelper.checkInitPermission(this, callback);
    }

    private void refreshPermissionState() {
        mRequestCameraResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.CAMERA)));
        mRequestAudioResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.RECORD_AUDIO)));
        mRequestRecordResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)));
        mRequestLocationResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PermissionUtil.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.log("requestCode=" + requestCode);
        for (int i=0, count=permissions.length; i<count; i++) {
            LogUtil.log("permission=" + permissions[i] + "; result=" + grantResults[i]);

            LogUtil.log("shouldShowRequestPermissionRationale=" + PermissionUtil.shouldShowRequestPermissionRationale(this, permissions[i]));
        }
        refreshPermissionState();
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
