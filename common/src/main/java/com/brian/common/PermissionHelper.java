package com.brian.common;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;

import com.brian.common.util.PermissionUtil;
import com.brian.common.view.CommonDialogFragment;

/**
 * 封装6.0 需要动态请求的权限
 * Created by lipeilong on 2016/7/26.
 * <p/>
 * 这些权限需要动态申请：
 * ACCESS_COARSE_LOCATION、 ACCESS_FINE_LOCATION、 ADD_VOICEMAIL、 BODY_SENSORS
 * CALL_PHONE、 CAMERA、 GET_ACCOUNTS、 PROCESS_OUTGOING_CALLS、READ_CALENDAR
 * READ_CALL_LOG、 READ_CELL_BROADCASTS、 READ_CONTACTS、 READ_EXTERNAL_STORAGE
 * READ_PHONE_STATE、READ_SMS、RECEIVE_MMS、RECEIVE_SMS、RECEIVE_WAP_PUSH、RECORD_AUDIO
 * SEND_SMS、USE_SIP、WRITE_CALENDAR、WRITE_CALL_LOG、WRITE_CONTACTS、WRITE_EXTERNAL_STORAGE
 */
public class PermissionHelper {

    /**
     * 读SDCARD 权限
     */
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;

    /**
     * 定位权限
     */
    public static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    /**
     * 录音权限
     */
    public static final int PERMISSION_REQUEST_CODE_AUDIO = 2;

    /**
     * 照相机权限
     */
    public static final int PERMISSION_REQUEST_CODE_CAMERA = 3;

    public static final int PERMISSION_REQUEST_CODE_CAMERA_AND_AUDIO = 4;

    /**
     * 读取手机状态权限（for imei）
     */
    public static final int PERMISSION_REQUEST_CODE_READ_PHONE_STATE = 5;

    /**
     * 应用初始化所需权限，PHONE_STATE 和 WRITE_EXTERNAL_STORAGE_REQUEST
     */
    public static final int PERMISSION_REQUEST_CODE_INIT = 6;


    public static class PermissionCallback implements PermissionUtil.IPermissionCallback {

        /**
         * 覆写该方法需要处理点击事件，调用执行权限请求
         * @param request
         */
        @Override
        public void onShowRequestPermissionRationale(final PermissionUtil.PermissionRequest request) {
            Activity activity = PermissionUtil.getActivity(request.requestHolder);
            if (null == activity) {
                return;
            }

            CommonDialogFragment.create(activity.getFragmentManager())
                    .setPositiveButton(activity.getText(android.R.string.ok), null)
                    .setMessage(request.rationale)
                    .setTitle("权限提醒")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            PermissionUtil.executePermissionsRequest(request);
                        }
                    })
                    .show();
        }

        @Override
        public void onPermissionsDenied(final PermissionUtil.PermissionRequest request) {
            Activity activity = PermissionUtil.getActivity(request.requestHolder);
            if (null == activity) {
                return;
            }
            boolean shouldShowRationale = true;
            for (String perm : request.perms) {
                shouldShowRationale = shouldShowRationale && PermissionUtil.shouldShowRequestPermissionRationale(request.requestHolder, perm);
            }

            if (!shouldShowRationale) {
                CommonDialogFragment.create(activity.getFragmentManager())
                        .setPositiveButton(activity.getText(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtil.startSettingForResult(request.requestHolder, request.requestCode);
                            }
                        })
                        .setNegativeButton(activity.getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setMessage(request.rationale + "\n跳转设置页面授予权限？")
                        .setTitle("权限提醒")
                        .setDialogCancelable(false)
                        .show();
            }
        }

        /**
         * 部分权限被授权才会被调用，若全部权限被授予则会调用onAllPermissionsGranted
         */
        @Override
        public void onPermissionsGranted(PermissionUtil.PermissionRequest request) {
        }

        @Override
        public void onAllPermissionsGranted(int requestCode) {
        }
    }

    /**
     * 检查定位权限
     */
    public static boolean checkLocationPermission(Activity activity, PermissionUtil.IPermissionCallback callback) {
        // SDK 小于23默认已经授权
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!PermissionUtil.hasPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            String rationale = "需要定位权限";
            return checkPermission(activity, callback, rationale, PERMISSION_REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return true;
    }

    /**
     * 权限
     */
    public static boolean checkPermission(Activity activity, PermissionUtil.IPermissionCallback callback, int code, String... permissions) {
        return checkPermission(activity, callback, null, code, permissions);
    }

    /**
     * 权限
     */
    public static boolean checkPermission(Activity activity, PermissionUtil.IPermissionCallback callback, String rationale, int code, String... permissions) {
        // SDK 小于23默认已经授权
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!PermissionUtil.hasPermissions(activity, permissions)) {
            if (callback == null) {
                callback = new PermissionCallback();
            }
            PermissionUtil.requestPermissions(activity, callback, rationale, code, permissions);
            return false;
        }
        return true;
    }

    /**
     * 相机权限：CAMERA
     */
    public static boolean checkCameraPermission(Activity activity, PermissionUtil.IPermissionCallback callback) {
        String rationale = "相机预览需要相机权限";
        return checkPermission(activity, callback, rationale, PERMISSION_REQUEST_CODE_CAMERA, Manifest.permission.CAMERA);
    }

    /**
     * 录音权限：RECORD_AUDIO
     */
    public static boolean checkAudioPermission(Activity activity, PermissionUtil.IPermissionCallback callback) {
        String rationale = "需要录音权限";
        return checkPermission(activity, callback, rationale, PERMISSION_REQUEST_CODE_AUDIO, Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 视频录制权限：CAMERA 和 RECORD_AUDIO
     */
    public static boolean checkRecordPermission(Activity activity, PermissionUtil.IPermissionCallback callback) {
        String rationale = "录制视频需要相机和录音权限";
        return checkPermission(activity, callback, rationale, PERMISSION_REQUEST_CODE_CAMERA_AND_AUDIO, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
    }

    public static boolean checkInitPermission(Activity activity, PermissionUtil.IPermissionCallback callback) {
        String rationale = "读取设备ID和文件存储是应用需要的最基本的权限";
        return checkPermission(activity, callback, rationale, PERMISSION_REQUEST_CODE_INIT, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
