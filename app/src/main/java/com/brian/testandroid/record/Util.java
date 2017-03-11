package com.brian.testandroid.record;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.brian.testandroid.R;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import static com.brian.testandroid.record.RecorderParameters.RESOLUTION_HIGH_VALUE;
import static com.brian.testandroid.record.RecorderParameters.RESOLUTION_LOW_VALUE;
import static com.brian.testandroid.record.RecorderParameters.RESOLUTION_MEDIUM_VALUE;


public class Util {
    public static ContentValues videoContentValues = null;


    public static int determineDisplayOrientation(Activity activity, int defaultCameraId) {
        int displayOrientation = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(defaultCameraId, cameraInfo);

            int degrees = getRotationAngle(activity);


            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                displayOrientation = (cameraInfo.orientation + degrees) % 360;
                displayOrientation = (360 - displayOrientation) % 360;
            } else {
                displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
            }
        }
        return displayOrientation;
    }

    public static int getRotationAngle(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return degrees;
    }

    public static int getRotationAngle(int rotation) {
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return degrees;
    }

    public static String createImagePath(Context context) {
        long dateTaken = System.currentTimeMillis();
        String title = "Picture_" + dateTaken;
        String filename = title + ".jpg";

        String dirPath = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/video";
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory())
            file.mkdirs();
        String filePath = dirPath + "/" + filename;
        return filePath;
    }

    public static String createFinalPath(Context context) {
        long dateTaken = System.currentTimeMillis();
        String title = "Video_" + dateTaken;
        String filename = title + ".mp4";
        String filePath = genrateFilePath(context, String.valueOf(dateTaken), true, null);

        ContentValues values = new ContentValues(7);
        values.put(Video.Media.TITLE, title);
        values.put(Video.Media.DISPLAY_NAME, filename);
        values.put(Video.Media.DATE_TAKEN, dateTaken);
        values.put(Video.Media.MIME_TYPE, "video/3gpp");
        values.put(Video.Media.DATA, filePath);
        videoContentValues = values;

        return filePath;
    }

    private static String genrateFilePath(Context context, String uniqueId, boolean isFinalPath, File tempFolderPath) {
        String fileName = "Video_" + uniqueId + ".mp4";
        String dirPath = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/video";
        if (isFinalPath) {
            File file = new File(dirPath);
            if (!file.exists() || !file.isDirectory())
                file.mkdirs();
        } else
            dirPath = tempFolderPath.getAbsolutePath();
        String filePath = dirPath + "/" + fileName;
        return filePath;
    }

    public static List<Camera.Size> getResolutionList(Camera camera) {
        Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();


        return previewSizes;
    }

    public static RecorderParameters getRecorderParameter(int currentResolution) {
        RecorderParameters parameters = new RecorderParameters();
        if (currentResolution == RESOLUTION_HIGH_VALUE) {
            parameters.setAudioBitrate(128000);
            parameters.setVideoQuality(0);
        } else if (currentResolution == RESOLUTION_MEDIUM_VALUE) {
            parameters.setAudioBitrate(128000);
            parameters.setVideoQuality(5);
        } else if (currentResolution == RESOLUTION_LOW_VALUE) {
            parameters.setAudioBitrate(96000);
            parameters.setVideoQuality(20);
        }
        return parameters;
    }

    public static class ResolutionComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size size1, Camera.Size size2) {
            if (size1.height != size2.height)
                return size1.height - size2.height;
            else
                return size1.width - size2.width;
        }
    }

    public static int getTimeStampInNsFromSampleCounted(int paramInt) {
        return (int) (paramInt / 0.0441D);
    }

    /**
     * 公共弹窗
     *
     * @param context :Context 传入当前调用该方法的activity实例
     * @param type    :int 显示类型1：仅为确定，2：有“确定”、“取消”两个操作
     * @param handler :Handler 传入的需要回调的handler信息，可作为回调方法是用，msg.what = 1时为操作完成状态符
     */
    public static void showDialog(Context context, String title, String content, int type, final Handler handler) {
        final Dialog dialog = new Dialog(context, R.style.Dialog_loading);
        dialog.setCancelable(true);
        // 设置像是内容模板
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.global_dialog_tpl, null);
        Button confirmButton = (Button) view
                .findViewById(R.id.setting_account_bind_confirm);// 确认
        Button cancelButton = (Button) view
                .findViewById(R.id.setting_account_bind_cancel);// 取消
        TextView dialogTitle = (TextView) view
                .findViewById(R.id.global_dialog_title);// 标题
        View line_hori_center = view.findViewById(R.id.line_hori_center);// 中竖线
        confirmButton.setVisibility(View.GONE);// 默认隐藏取消按钮
        line_hori_center.setVisibility(View.GONE);
        TextView textView = (TextView) view.findViewById(R.id.setting_account_bind_text);

        // 设置对话框的宽度
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (context.getResources().getDisplayMetrics().density * 288);
        dialogWindow.setAttributes(lp);

        // 设置显示类型
        if (type != 1 && type != 2) {
            type = 1;
        }
        dialogTitle.setText(title);// 设置标题
        textView.setText(content); // 设置提示内容

        // 确认按钮操作
        if (type == 1 || type == 2) {
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (handler != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    dialog.dismiss();
                }
            });
        }
        // 取消按钮事件
        if (type == 2) {
            cancelButton.setVisibility(View.VISIBLE);
            line_hori_center.setVisibility(View.VISIBLE);
            cancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (handler != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                    dialog.dismiss();
                }
            });
        }
        dialog.addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        dialog.setCancelable(true);// 点击返回键关闭
        dialog.setCanceledOnTouchOutside(true);// 点击外部关闭
        dialog.show();
    }

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {

        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }

        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;

        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (imageWidth != nWidth || imageHeight != nHeight) {
            nWidth = imageWidth;
            nHeight = imageHeight;
            wh = imageWidth * imageHeight;
            uvHeight = imageHeight >> 1;//uvHeight = height / 2
        }

        //旋转Y
        int k = 0;
        for (int i = 0; i < imageWidth; i++) {
            int nPos = 0;
            for (int j = 0; j < imageHeight; j++) {
                yuv[k] = data[nPos + i];
                k++;
                nPos += imageWidth;
            }
        }

        for (int i = 0; i < imageWidth; i += 2) {
            int nPos = wh;
            for (int j = 0; j < uvHeight; j++) {
                yuv[k] = data[nPos + i];
                yuv[k + 1] = data[nPos + i + 1];
                k += 2;
                nPos += imageWidth;
            }
        }
        //这一部分可以直接旋转270度，但是图像颜色不对
        return rotateYUV420Degree180(yuv, imageWidth, imageHeight);
    }
}
