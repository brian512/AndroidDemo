package com.brian.testandroid.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.util.LogUtil;
import com.brian.testandroid.util.PermissionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 测试权限请求
 * Created by brian on 2016/12/4.
 */

public class PermissionsActivity extends BaseActivity {

    @BindView(R.id.request_audio)
    Button mRequestAudio;

    @BindView(R.id.request_camera)
    Button mRequestCamera;

    @BindView(R.id.request_record)
    Button mRequestRecord;

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

        mRequestCameraResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.CAMERA)));
        mRequestAudioResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.RECORD_AUDIO)));
        mRequestRecordResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)));


        mRequestCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestPermissions(PermissionsActivity.this, "请求权限：" + Manifest.permission.CAMERA, 1, Manifest.permission.CAMERA);
            }
        });

        mRequestAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestPermissions(PermissionsActivity.this, "请求权限：" + Manifest.permission.RECORD_AUDIO, 2, Manifest.permission.RECORD_AUDIO);
            }
        });

        mRequestRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestPermissions(PermissionsActivity.this, "请求权限：" + Manifest.permission.CAMERA + " & " + Manifest.permission.RECORD_AUDIO, 3, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.log("requestCode=" + requestCode);
        for (int i=0, count=permissions.length; i<count; i++) {
            LogUtil.log("requestCode=" + permissions[i] + "; result=" + grantResults[i]);

            LogUtil.log("" + PermissionUtil.shouldShowRequestPermissionRationale(this, permissions[i]));

            if (requestCode == 3)
                mRequestRecordResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)));
            if (requestCode == 2)
                mRequestAudioResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.RECORD_AUDIO)));
            if (requestCode == 1)
                mRequestCameraResult.setText(String.valueOf(PermissionUtil.hasPermissions(this, Manifest.permission.CAMERA)));
        }
    }
}
