package com.brian.testandroid.common.camera;


/**
 * 定义我们定制的摄像头的默认参数
 */
public class CameraConfigDefine {
    /**
     * 发送漂流瓶，期望的拍照预览图片的宽高（手机不满足的话，会自动适配到最接近的分辨率）
     */
    public static final int PREVIEW_SIZE_WIDTH  = 720;
    public static final int PREVIEW_SIZE_HEIGHT = 1280;
    
    /**
     * 发送漂流瓶，期望的拍照保存图片的宽高（手机不满足的话，会自动适配到最接近的分辨率）
     */
    public static final int TAKE_PIC_SIZE_WIDTH  = PREVIEW_SIZE_WIDTH;
    public static final int TAKE_PIC_SIZE_HEIGHT = PREVIEW_SIZE_HEIGHT;
}
