package com.brian.common.camera;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.text.TextUtils;

/**
 * 摄像头属性配置
 */
public class CameraConfig {

    public static final int PREVIEW_SIZE_WIDTH = 720;
    public static final int PREVIEW_SIZE_HEIGHT = 1280;

    public static final int TAKE_PIC_SIZE_WIDTH = PREVIEW_SIZE_WIDTH;
    public static final int TAKE_PIC_SIZE_HEIGHT = PREVIEW_SIZE_HEIGHT;

    private CameraConfig(int cameraType) {
        this.cameraType = cameraType;
    }

    public static CameraConfig createDefaultCameraConfig(int cameraType) {
        CameraConfig cameraConfig = new CameraConfig(cameraType);
        cameraConfig.initDefaultValue();
        return cameraConfig;
    }

    /**
     * 照相机类型（后置摄像头0/前置摄像头1）
     * 默认后置摄像头
     */
    int cameraType;

    /**
     * 默认闪光模式
     * 关闭
     */
    public String flashMode;

    /**
     * 默认的对焦模式
     */
    public String focusMode;

    /**
     * 想要的预览尺寸大小
     */
    public int preViewWidth;
    public int preViewHeight;

    /**
     * 真实的预览尺寸
     */
    Size preViewSize;

    /**
     * 照相尺寸大小
     */
    public int picWidth;
    public int picHeight;

    /**
     * 真实的照相尺寸大小
     */
    Size picSize;

    /**
     * 目前对前后置摄像头初始化同样的参数
     */
    private void initDefaultValue() {
        if (TextUtils.isEmpty(flashMode)) {
            flashMode = Camera.Parameters.FLASH_MODE_OFF;
        }

        if (TextUtils.isEmpty(focusMode)) {
            focusMode = Camera.Parameters.FOCUS_MODE_AUTO;
        }

        if (preViewWidth == 0 || preViewHeight == 0) {
            preViewWidth = PREVIEW_SIZE_WIDTH;
            preViewHeight = PREVIEW_SIZE_HEIGHT;
        }

        if (picWidth == 0 || picHeight == 0) {
            picWidth = TAKE_PIC_SIZE_WIDTH;
            picHeight = TAKE_PIC_SIZE_HEIGHT;
        }
    }
}
