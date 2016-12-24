package com.brian.testandroid.common.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import java.util.List;

/**
 * 摄像机操作的工具类
 * 
 * @author ls
 *
 */
public class CameraUtil {
    
    public static final String TAG = CameraUtil.class.getSimpleName();
    
    /**
     * 预览界面拉伸模式
     * 
     * @author ls
     *
     */
    public enum PreViewScaleMode{
        /**
         * 短边填满、长边留白模式
         */
        CENTERINSIDE,
        
        /**
         * 长边填满、短边截取模式
         */
        CENTERCROP,
        
        /**
         * 宽度充满
         */
        WIDTHCROP,
    }

    /**
     * 从列表中选择合适的大小尺寸
     * 
     * 规则：
     * image1.在长宽比在我们阈值范围内时，选择长宽乘积最接近我们需求的
     * 2.如果规则1的情况下没找到合适的，就使用超出阈值外最接近我们需求的
     * 
     * @param sizes
     * @param properWidth
     * @param properHeight
     * @return
     */
    public static Size getProperSize(List<Size> sizes, int properWidth, int properHeight){
        if (sizes == null || sizes.size() == 0 || properWidth == 0 || properHeight == 0) {
            return null;
        }
        
        final int NEED_SIZE = properWidth * properHeight;
        Size properRateSize = null;
        Size indexSize;
        int curProperRateDif = Integer.MAX_VALUE;

        for (int i = 0; i < sizes.size(); i++) {
            indexSize = sizes.get(i);
            int dif = Math.abs(indexSize.width * indexSize.height - NEED_SIZE);

            // 如果这种比例比刚才那种更合适，则替换
            if (dif < curProperRateDif) {
                properRateSize = indexSize;
                curProperRateDif = dif;
            }
        }
        
        Size backSize = properRateSize;
        
        return backSize;
    }
    
    /**
     * 选择合适的预览尺寸
     * @return
     */
    public static Size getProperPreviewSize(Parameters parameters, int needWidth, int needHeight) {
        if (parameters == null) {
            return null;
        }
        
        List<Size> sizes = parameters.getSupportedPreviewSizes();
        
        return getProperSize(sizes, needWidth, needHeight);
    }
    
    /**
     * 获取合适的照相尺寸
     * @return
     */
    public static Size getProperPictureSize(Parameters parameters, int needWidth, int needHeight){
        if (parameters == null) {
            return null;
        }
        
        List<Size> sizes = parameters.getSupportedPictureSizes();
        
        return getProperSize(sizes, needWidth, needHeight);
    }
    
    /**
     * 获取合适的视频尺寸
     * @return
     */
    public static Size getProperVideoSize(Parameters parameters, int needWidth, int needHeight){
        if (parameters == null) {
            return null;
        }
        
        List<Size> sizes = parameters.getSupportedVideoSizes();
        
        // 存在部分机型，不存在支持的视频类型，此时取值预览类型的数据
        if (sizes == null) {
            sizes = parameters.getSupportedPictureSizes();
        }
        
        return getProperSize(sizes, needWidth, needHeight);
    }
    
    /**
     * 根据比例来调整页面的大小
     * 
     * 采用短边填满，长边留白的方式
     * 如果width/height 的比例小于rate,则v的最终布局大小时，宽度使用width,高度使用等比例大小
     * 如果width/height 的比例大于rate,则v的最终布局大小时，高度使用height,宽度使用等比例大小
     */
    public static void resizeView(View v, float width, float height, float rate){
        LayoutParams params = v.getLayoutParams();
        if (width / height < rate) {
            params.width    = (int)width;
            params.height   = (int)(width / rate);
        } else {
            params.width    = (int)(height * rate);
            params.height   = (int)height;
        }
        v.setLayoutParams(params);
    }
    
    /**
     * 根据比例来调整页面的大小
     * 
     * 采用长边填满，短边超出的方式
     * 如果width/height 的比例大于rate,则最终布局大小，宽度使用width,高度使用等比例大小
     * 如果width/height 的比例小于rate,则最终布局大小，高度使用height,宽度使用等比例大小
     * 
     * @param v
     * @param width
     * @param height
     * @param rate
     */
    public static void resizeBigView(View v, float width, float height, float rate){
        LayoutParams params = v.getLayoutParams();
        if (width / height > rate) {
            params.width    = (int)width;
            params.height   = (int)(width / rate);
        } else {
            params.width    = (int)(height * rate);
            params.height   = (int)height;
        }
        
        
        v.setLayoutParams(params);
    }

    /**
     * 采用宽度填满的方式来调整页面大小
     * 
     * @param v
     * @param width
     * @param height
     * @param rate
     */
    public static void resizeViewByWidth(View v, float width, float height, float rate) {
        LayoutParams params = v.getLayoutParams();
        params.width    = (int)width;
        params.height   = (int)(width / rate);
        
        v.setLayoutParams(params);
    }
    
    /**
     * 调整预览界面大小
     */
    public static void resizePreView(View v, float width, float height, float rate, PreViewScaleMode mode){
        int screenW = 720;
        int screenH = 1280;
//        int screenW = DeviceUtil.getScreenWidth(Env.getContext());
//        int screenH = DeviceUtil.getScreenHeight(Env.getContext());

        // 保证初始传入的大小不超出屏幕
        float nWidth    = Math.min(width, screenW);
        float nHeight   = Math.min(height, screenH);
        
       switch (mode) {
        case CENTERCROP:
            resizeBigView(v, nWidth, nHeight, rate);
            break;
        case CENTERINSIDE:
            resizeView(v, nWidth, nHeight, rate);
            break;
        case WIDTHCROP:
            resizeViewByWidth(v, nWidth, nHeight, rate);
            break;
        default:
            break;
       } 
    }

    public static Camera.Size getOptimalPreviewSize(Camera.Parameters parameters,
                                                    Camera.Size pictureSize, int viewHeight) {

        if (parameters == null || pictureSize == null) {
            return null;
        }

        List<Size> sizes = parameters.getSupportedPreviewSizes();

        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) pictureSize.width / pictureSize.height;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = pictureSize.height;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;

            if (optimalSize != null && size.height > viewHeight) {
                break;
            }

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        //Log.e("CameraHelper",
        //        "optimalSize : width=" + optimalSize.width + " height=" + optimalSize.height);
        return optimalSize;
    }

    //  这里只使用于旋转了90度
    public static Rect calculateTapArea(View v, float oldx, float oldy, float coefficient) {

        float x = oldy;
        float y = v.getHeight() - oldx;

        float focusAreaSize = 300;

        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / v.getWidth() * 2000 - 1000);
        int centerY = (int) (y / v.getHeight() * 2000 - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
}
