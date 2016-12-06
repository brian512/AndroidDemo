package com.brian.common.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by brian on 2016/12/4.
 */

public class PermissionUtil {
    private static final String TAG = "PermissionUtil";

    public interface IPermissionCallback {

        void onShowRequestPermissionRationale(PermissionRequest request);

        void onPermissionsDenied(PermissionRequest request);

        void onPermissionsGranted(PermissionRequest request);

        void onAllPermissionsGranted(int requestCode);

    }

    private static SparseArray<PermissionRequest> mRequestMap = new SparseArray<>();

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context the calling context.
     * @param perms   one ore more permissions, such as {@code android.Manifest.permission.CAMERA}.
     * @return true if all permissions are already granted, false if at least one permission
     * is not yet granted.
     */
    public static boolean hasPermissions(Context context, String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            return true;
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void requestPermissions(final Object object, IPermissionCallback callback, final int requestCode, final String... perms) {
        requestPermissions(object, callback, null, requestCode, perms);
    }

    /**
     * Request a set of permissions, showing rationale if the system requests it.
     *
     * @param object         Activity or Fragment requesting permissions. Should implement
     *                       {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
     * @param rationale      a message explaining why the application needs this set of permissions, will
     *                       be displayed if the user rejects the request the first time.
     * @param requestCode    request code to track this request, must be < 256.
     * @param perms          a set of permissions to be requested.
     */
    public static void requestPermissions(final Object object, IPermissionCallback callback, String rationale,
                                          final int requestCode, final String... perms) {

        checkCallingObjectSuitability(object);

        PermissionRequest request = new PermissionRequest();
        request.requestHolder = object;
        request.requestCode = requestCode;
        request.rationale = rationale;
        request.perms = perms;
        request.callback = callback;

        mRequestMap.put(requestCode, request);

        boolean shouldShowRationale = false;
        for (String perm : perms) {
            shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
        }

        if (callback != null && shouldShowRationale && !TextUtils.isEmpty(rationale)) {
            callback.onShowRequestPermissionRationale(request);
        } else {
            executePermissionsRequest(request);
        }
    }

    private static void checkCallingObjectSuitability(Object object) {
        // Make sure Object is an Activity or Fragment
        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        boolean isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        if (!(isSupportFragment || isActivity || (isAppFragment && isMinSdkM))) {
            if (isAppFragment) {
                throw new IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }

    @TargetApi(23)
    public static void executePermissionsRequest(PermissionRequest request) {
        checkCallingObjectSuitability(request.requestHolder);

        Object object = request.requestHolder;
        LogUtil.d("requestCode=" + request.requestCode);
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, request.perms, request.requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(request.perms, request.requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(request.perms, request.requestCode);
        }
    }

    @TargetApi(11)
    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }



    @TargetApi(23)
    public static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {

        // Make a collection of granted and denied permissions from the request.
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        PermissionRequest request = mRequestMap.get(requestCode);
        request.grantResults = grantResults;
        // iterate through all receivers
        if (request.callback != null) {
            // Report granted permissions, if granted partly
            if (!granted.isEmpty() && !denied.isEmpty()) {
                request.callback.onPermissionsGranted(request);
            }

            // Report denied permissions, if any.
            if (!denied.isEmpty()) {
                request.callback.onPermissionsDenied(request);
            }

            // If 100% successful, call annotated methods
            if (!granted.isEmpty() && denied.isEmpty()) {
                request.callback.onAllPermissionsGranted(requestCode);
            }
        }
        mRequestMap.remove(requestCode); // 请求结束则移除，否则会引起内存泄漏
    }

    public static void startSettingForResult(Object object, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        if (object instanceof Activity) {
            Activity activity = (Activity) object;
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, requestCode);
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            Uri uri = Uri.fromParts("package", fragment.getContext().getPackageName(), null);
            intent.setData(uri);
            fragment.startActivityForResult(intent, requestCode);
        } else if (object instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) object;
            Uri uri = Uri.fromParts("package", fragment.getActivity().getPackageName(), null);
            intent.setData(uri);
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    public static class PermissionRequest {
        public Object requestHolder;
        public int requestCode;
        public String rationale;
        public String[] perms;
        public IPermissionCallback callback;
        int[] grantResults;
    }
}