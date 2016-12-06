
package com.brian.common.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.View;

import com.brian.common.Env;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图像工具类
 */
public class BitmapUtil {

    private static final String TAG = BitmapUtil.class.getSimpleName();

    /**
     * 压缩图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float multiple) {
        if (bitmap == null || bitmap.isRecycled()) {
            return bitmap;
        }
        bitmap = resizeBitmap(bitmap, (int) (bitmap.getWidth() * multiple), (int) (bitmap.getHeight() * multiple));
        return bitmap;
    }

    /**
     * 按照指定尺寸缩放图像
     *
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null || newWidth == 0 || newHeight == 0) {
            return bitmap;
        }

        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        // 旋转图片 动作
        // matrix.postRotate(45);

        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    /**
     * 通过指定的矩阵重新设置图片
     *
     * @param bitmap
     * @param matrix
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, Matrix matrix) {
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    /**
     * 保存图片到文件
     *
     * @param bitmap
     * @param path
     * @return 0：成功
     * 其他：失败
     */
    public static int saveBitmap(Bitmap bitmap, String path) {
        return saveBitmap(bitmap, path, 85);
    }

    /**
     * * 保存图片到文件
     *
     * @param bitmap
     * @param path
     * @param quality 图片质量取值0 - 100；
     * @return 0：成功
     * 其他：失败
     */
    public static int saveBitmap(Bitmap bitmap, String path, int quality) {
        // 保存图片
        File saveFile = new File(path);
        if (saveFile.exists()) {
            saveFile.delete();
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(saveFile);
            bitmap.compress(CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 图片旋转信息
     *
     * @author wuwenhua
     */
    public static class ExifInfo {

        public final int rotation;
        public final boolean flipHorizontal; // 镜像

        protected ExifInfo() {
            this.rotation = 0;
            this.flipHorizontal = false;
        }

        protected ExifInfo(int rotation, boolean flipHorizontal) {
            this.rotation = rotation;
            this.flipHorizontal = flipHorizontal;
        }
    }

    /**
     * 获得图片的旋转信息
     * PS：代码参考自 com.nostra13.universalimageloader.core.decode.BaseImageDecoder.defineExifOrientation
     * //     * @param imageUri
     *
     * @return
     */
    public static ExifInfo getExifInfo(String imagePath) {
        int rotation = 0;
        boolean flip = false;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    flip = true;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "Can't read EXIF tags from file " + imagePath);
        }
        return new ExifInfo(rotation, flip);
    }

    /**
     * 修正图片角度
     *
     * @param bitmap
     * @param exif
     * @return
     */
    public static Bitmap correctExifOrientation(Bitmap bitmap, ExifInfo exif) {

        if (bitmap == null) {
            return null;
        }

        // 没有旋转直接返回原图
        if ((exif.flipHorizontal == false) && (exif.rotation == 0)) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.reset();

        // Flip bitmap if need
        if (exif.flipHorizontal) {
            matrix.postScale(-1, 1);
        }
        // Rotate bitmap if need
        if (exif.rotation != 0) {
            matrix.postRotate(exif.rotation);
        }

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 不考虑缩放，只考虑旋转角度的情况下，读取图片
     */
    public static Bitmap readBitmap(String imagePath) {
        return readBitmap(imagePath, null);
    }

    /**
     * 不考虑缩放，只考虑旋转角度的情况下，读取图片
     */
    public static Bitmap readBitmap(String imagePath, Options options) {
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagePath);

            bitmap = BitmapFactory.decodeStream(fis, null, options);
            if (bitmap != null) {
                try {
                    ExifInfo exif = getExifInfo(imagePath);
                    bitmap = correctExifOrientation(bitmap, exif); // 旋转
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 读取图片资源
     *
//     * @param context
     * @param resID
     * @return
     */
    public static Bitmap readBitmap(Resources res, int resID) {
        return readBitmap(res, resID, null);
    }

    /**
     * 读取图片资源
     *
     * @param res
     * @param resID
     * @param options
     * @return
     */
    public static Bitmap readBitmap(Resources res, int resID, Options options) {
        if (res == null) {
            return null;
        }
        try {
            return BitmapFactory.decodeResource(res, resID, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 从本地asset文件夹读取图片
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap readBitmap(Context context, Uri uri) {
        AssetFileDescriptor afd = null;
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            // 第一步 获取字节流 ，读取图片byte数组
            afd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            fis = afd.createInputStream();
            byte[] data = readStream(fis);
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                }
                fis = null;
            }

            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                afd = null;
            }
        }

        return bitmap;
    }

    /**
     * @param inStream
     * @return
     * @throws Exception
     */
    private static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        return outStream.toByteArray();
    }

    /**
     * 读取图片，考虑缩放、裁剪和旋转
     *
     * @param imagePath
     * @param width      目标宽度
     * @param height     目标高度
     * @param clipCenter 是否从中间截取， true - 从中间截取，false - 从左上角截取
     * @return
     */
    public static Bitmap readBitmap(String imagePath, int width, int height, boolean clipCenter) {

        // 读取原图（已经处理过旋转）
        Bitmap orgBitmap = readBitmap(imagePath);
        return resizeAndClipBitmap(orgBitmap, width, height, clipCenter);

    }

    /**
     * 读取图片，考虑缩放、裁剪和旋转
     *
     * @param imageUri
     * @param width      目标宽度
     * @param height     目标高度
     * @param clipCenter 是否从中间截取， true - 从中间截取，false - 从左上角截取
     * @return
     */
    public static Bitmap readBitmap(Uri imageUri, int width, int height, boolean clipCenter) {

        // 读取原图（已经处理过旋转）
        Bitmap orgBitmap = readBitmap(Env.getContext(), imageUri);
        return resizeAndClipBitmap(orgBitmap, width, height, clipCenter);

    }

    /**
     * 读取缩略图
     *
     * @param uri       如果参数是uri的情况下，先尝试获取绝对路径，用绝对路径去获取图片
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap readBitmap(Uri uri, Activity activity, int maxWidth, int maxHeight) {

        if (uri == null) {
            return null;
        }

        String imagePath = UriUtil.getAbsoluteImagePath(activity, uri);
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(imagePath)) {
            bitmap = readBitmap(imagePath, maxWidth, maxHeight);
        } else {
            bitmap = readBitmap(activity, uri, maxWidth, maxHeight);
        }

        return bitmap;
    }

    /**
     * 根据路径解析出合适分辨率的图片； PS：这里只做基本的缩放操作
     *
     * @param imagePath 图片路径
     * @param width     指定 宽度
     * @param height    指定高度
     * @return
     */
    public static Bitmap readBitmap(String imagePath, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        // 解析出图片大小
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = getOption(options.outWidth, options.outHeight, width, height);

        // 开始正真的图片解析
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeFile(imagePath, options);

            // 注意：huawei G610-U00 有一张黑色图片解析会失败（系统也读不到，这里做下容错处理）
            if (bitmap != null) {
                // 根据文件旋转信息旋转图片
                BitmapUtil.ExifInfo exif = BitmapUtil.getExifInfo(imagePath);
                bitmap = BitmapUtil.correctExifOrientation(bitmap, exif);
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
        }

        return bitmap;
    }

    /**
     * 根据指定大小获取缩略图 PS：这里只做基本的缩放操作 ，不推荐直接使用
     *
     * @param activity
     * @param uri
     * @param width
     * @param height
     * @return
     */
    protected static Bitmap readBitmap(Activity activity, Uri uri, int width, int height) {
        if (uri == null) {
            return null;
        }

        Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;

        AssetFileDescriptor afd = null;
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            // 第一步 获取字节流 ，读取图片byte数组
            afd = activity.getContentResolver().openAssetFileDescriptor(uri, "r");
            fis = afd.createInputStream();
            byte[] data = readStream(fis);

            // 第二步 获取图片宽高，绝对缩放比例
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            options.inSampleSize = getOption(options.outWidth, options.outHeight, width, height);

            // 第三步 获取图片
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // 根据文件旋转信息旋转图片
            String imagePath = UriUtil.getAbsoluteImagePath(activity, uri);
            BitmapUtil.ExifInfo exif = BitmapUtil.getExifInfo(imagePath);
            bitmap = BitmapUtil.correctExifOrientation(bitmap, exif);

        } catch (Exception e) {

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                }
                fis = null;
            }

            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                afd = null;
            }
        }

        return bitmap;
    }

    private static int getOption(int bitmapWidth, int bitmapHeight, int needWidth, int needHeight) {
        int scaleLong = Math.max(bitmapWidth, bitmapHeight) / Math.max(needWidth, needHeight) + 1;
        int scaleShort = Math.min(bitmapWidth, bitmapHeight) / Math.min(needWidth, needHeight) + 1;

        int scale = Math.max(scaleLong, scaleShort);
        return scale;
    }

    /**
     * 缩放和裁剪图片 （注意：这个函数执行起来还是挺耗时的！比readBitmap(String imagePath, int width, int height)多不少时间）
     *
     * @param orgBitmap  原图
     * @param width      目标宽度
     * @param height     目标高度
     * @param clipCenter 是否从中间截取， true - 从中间截取，false - 从左上角截取
     * @return
     */
    public static Bitmap resizeAndClipBitmap(Bitmap orgBitmap, int width, int height, boolean clipCenter) {

        /*
         * 处理流程
		 * 1、处理旋转
		 * 2、缩放到合适尺寸
		 * 3、居中截取指定的范围
		 *
         * PS：注意缩放和截图不能同时做，否则有可能因为浮点计算，出现最终尺寸没有达到预期的值
         */

        if (width <= 0 && height <= 0) {
            throw new IllegalArgumentException("size is error");
        }
        if (orgBitmap == null) {
            return null;
        }

        // 算一个合适的缩放尺寸
        // 举例：从20x10截取中间5x5的情况
        float scaleX = width * 1f / orgBitmap.getWidth();
        float scaleY = height * 1f / orgBitmap.getHeight();
        float scale = Math.max(scaleX, scaleY);// 取大的值，保证能填充满
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaleBitmap = Bitmap.createBitmap(orgBitmap, 0, 0, orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, true);
        if (scaleBitmap != orgBitmap) {
            orgBitmap.recycle();
        }

        // 计算截取区域
        int x = 0;
        int y = 0;

        // 从左上角截取一块
        if (clipCenter == false) {
            x = 0;
            y = 0;
        }
        // 从中间需要的一块
        else {

            int curWidth = scaleBitmap.getWidth();
            int curHeight = scaleBitmap.getHeight();
            if (curWidth > width) {
                x = (curWidth - width) / 2;
            }
            if (curHeight > height) {
                y = (curHeight - height) / 2;
            }
        }

        Bitmap targetBitmap = Bitmap.createBitmap(scaleBitmap, x, y, width, height, null, false);
        if (targetBitmap != scaleBitmap) {
            scaleBitmap.recycle();
        }

        return targetBitmap;
    }

    /**
     * 裁剪图片
     *
     * @param source
     * @param x      < width
     * @param y      < height
     * @param width  > 0
     * @param height > 0
     * @return
     */
    public static Bitmap clipBitmap(Bitmap source, int x, int y, int width, int height) {
        if (source == null || source.isRecycled()) {
            return null;
        }

        if (width <= 0 || height <= 0 || x >= width || y >= height) {
            throw new IllegalArgumentException("size is error");
        }
        try {
            return Bitmap.createBitmap(source, x, y, width, height);
        } catch (Exception e) {
            System.gc();
            return null;
        }
    }

    /**
     * 创建一张模糊图片
     * API 11
     * 这里用于不需要太高性能的用途
     *
     * @param bitmap
     * @return
     */
    @TargetApi(17)
    public static Bitmap blurBitmapUseSysApi(Bitmap bitmap) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        try {

            // 判断是否支持ScriptIntrinsicBlur 接口
            Class cls = Class.forName("android.renderscript.ScriptIntrinsicBlur");
            if (cls == null) {
                return null;
            }

            // Instantiate a new Renderscript
            RenderScript rs = RenderScript.create(Env.getContext());

            // Create an Intrinsic Blur Script using the Renderscript
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

            // Set the radius of the blur
            blurScript.setRadius(25.f);

            // Perform the Renderscript
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);

            //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);

            // recycle the original bitmap
            // bitmap.recycle();

            // After finishing everything, we destroy the Renderscript.
            rs.destroy();
        } catch (Exception e) {
            outBitmap = null;
        }

        return outBitmap;

    }

    /**
     * 将矩形图片转成圆角矩形图片（PS：算法比较耗时，需要200~300ms）
     *
     * @param width              图像的宽度
     * @param height              图像的高度
     * @param image          源图片
     * @param outerRadiusRat 圆角的大小
     * @return 圆角图片
     */
    public static Bitmap getRoundedCornerBitmap(int width, int height, Bitmap image, float outerRadiusRat) {

        // 根据源文件新建一个darwable对象
        Drawable imageDrawable = new BitmapDrawable(image);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, width, height);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, width, height);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        return output;
    }

    /**
     * 将矩形图片转成上方圆角矩形图片， 与 getRoundedCornerBitmap的区别是，这里只有两方两个角是圆角 （PS：算法比较耗时，需要200~300ms）
     *
     * @param width              图像的宽度
     * @param height              图像的高度
     * @param image          源图片
     * @param outerRadiusRat 圆角的大小
     * @return 圆角图片
     */
    public static Bitmap getUpRoundedCornerBitmap(int width, int height, Bitmap image, float outerRadiusRat) {

        // 根据源文件新建一个darwable对象
        Drawable imageDrawable = new BitmapDrawable(image);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, width, height);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

        // 画出下方一个矩形，用于把下面的圆角补回去
        RectF downRect = new RectF(0, height / 2, width, height);
        canvas.drawRect(downRect, paint);

        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, width, height);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        return output;
    }

    /**
     * 获取一个View的截图
     *
     * @return
     */
    public static Bitmap getViewClipBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        Bitmap bm = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
        return bm;
    }

    /**
     * 获取镜像Bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getMirrorBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        return null;
    }

    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     *
     * @param backBitmap  在底部的位图
     * @param frontBitmap 盖在上面的位图
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {

        if (backBitmap == null || backBitmap.isRecycled()) {
            LogUtil.e(TAG, "backBitmap=" + backBitmap);
            return null;
        }
        if (frontBitmap == null || frontBitmap.isRecycled()) {
            LogUtil.e(TAG, "frontBitmap=" + frontBitmap);
            return backBitmap;
        }
        Bitmap bitmap = backBitmap.copy(Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }

    /**
     * 把两个位图覆盖合成为一个位图，左右拼接
     * <p/>
     * PS:暂未验证可靠性
     *
     * @param leftBitmap
     * @param rightBitmap
     * @param isBaseMax   是否以宽度大的位图为准，true则小图等比拉伸，false则大图等比压缩
     * @return
     */
    public static Bitmap mergeBitmap_LR(Bitmap leftBitmap, Bitmap rightBitmap, boolean isBaseMax) {

        if (leftBitmap == null || leftBitmap.isRecycled()
                || rightBitmap == null || rightBitmap.isRecycled()) {
            LogUtil.e(TAG, "leftBitmap=" + leftBitmap + ";rightBitmap=" + rightBitmap);
            return null;
        }
        int height = 0; // 拼接后的高度，按照参数取大或取小
        if (isBaseMax) {
            height = leftBitmap.getHeight() > rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap.getHeight();
        } else {
            height = leftBitmap.getHeight() < rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap.getHeight();
        }

        // 缩放之后的bitmap
        Bitmap tempBitmapL = leftBitmap;
        Bitmap tempBitmapR = rightBitmap;

        if (leftBitmap.getHeight() != height) {
            tempBitmapL = Bitmap.createScaledBitmap(leftBitmap, (int) (leftBitmap.getWidth() * 1f / leftBitmap.getHeight() * height), height, false);
        } else if (rightBitmap.getHeight() != height) {
            tempBitmapR = Bitmap.createScaledBitmap(rightBitmap, (int) (rightBitmap.getWidth() * 1f / rightBitmap.getHeight() * height), height, false);
        }

        // 拼接后的宽度
        int width = tempBitmapL.getWidth() + tempBitmapR.getWidth();

        // 定义输出的bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 缩放后两个bitmap需要绘制的参数
        Rect leftRect = new Rect(0, 0, tempBitmapL.getWidth(), tempBitmapL.getHeight());
        Rect rightRect = new Rect(0, 0, tempBitmapR.getWidth(), tempBitmapR.getHeight());

        // 右边图需要绘制的位置，往右边偏移左边图的宽度，高度是相同的
        Rect rightRectT = new Rect(tempBitmapL.getWidth(), 0, width, height);

        canvas.drawBitmap(tempBitmapL, leftRect, leftRect, null);
        canvas.drawBitmap(tempBitmapR, rightRect, rightRectT, null);
        return bitmap;
    }


    /**
     * 把两个位图覆盖合成为一个位图，上下拼接
     * <p/>
     * PS:暂未验证可靠性
     *
     * @param isBaseMax 是否以高度大的位图为准，true则小图等比拉伸，false则大图等比压缩
     * @return
     */
    public static Bitmap mergeBitmap_TB(Bitmap topBitmap, Bitmap bottomBitmap, boolean isBaseMax) {

        if (topBitmap == null || topBitmap.isRecycled()
                || bottomBitmap == null || bottomBitmap.isRecycled()) {
            LogUtil.e(TAG, "topBitmap=" + topBitmap + ";bottomBitmap=" + bottomBitmap);
            return null;
        }
        int width = 0;
        if (isBaseMax) {
            width = topBitmap.getWidth() > bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        } else {
            width = topBitmap.getWidth() < bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        }
        Bitmap tempBitmapT = topBitmap;
        Bitmap tempBitmapB = bottomBitmap;

        if (topBitmap.getWidth() != width) {
            tempBitmapT = Bitmap.createScaledBitmap(topBitmap, width, (int) (topBitmap.getHeight() * 1f / topBitmap.getWidth() * width), false);
        } else if (bottomBitmap.getWidth() != width) {
            tempBitmapB = Bitmap.createScaledBitmap(bottomBitmap, width, (int) (bottomBitmap.getHeight() * 1f / bottomBitmap.getWidth() * width), false);
        }

        int height = tempBitmapT.getHeight() + tempBitmapB.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Rect topRect = new Rect(0, 0, tempBitmapT.getWidth(), tempBitmapT.getHeight());
        Rect bottomRect = new Rect(0, 0, tempBitmapB.getWidth(), tempBitmapB.getHeight());

        Rect bottomRectT = new Rect(0, tempBitmapT.getHeight(), width, height);

        canvas.drawBitmap(tempBitmapT, topRect, topRect, null);
        canvas.drawBitmap(tempBitmapB, bottomRect, bottomRectT, null);
        return bitmap;
    }

    /**
     * 根据路径解析出合适分辨率的图片
     *
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getProperBitmap(String imagePath, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        // 解析出图片大小
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = getOptionNew(options.outWidth, options.outHeight, width, height);


        // 开始正真的图片解析
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapUtil.readBitmap(imagePath, options);

            // 将图片格式化为标准大小
            bitmap = formatBitmap(bitmap);

        } catch (OutOfMemoryError e) {
        }

        return bitmap;
    }

    // 图片宽|高的最大值
    final static public int MAX_VALUE = 1280;
    final static public int MAX_WIDTH = 800;
    final static public int MAX_HEIGHT = 1280;


//    private static int getOption(int bitmapWidth ,int bitmapHeight){
//        int max = bitmapWidth > bitmapHeight ? bitmapWidth:bitmapHeight;
//        if(max < MAX_VALUE){
//            return 1;
//        }else{
//            return max / MAX_VALUE + 1;
//        }
//    }

    private static int getOptionNew(int bitmapWidth, int bitmapHeight, int needWidth, int needHeight) {

        int scaleLong = (bitmapWidth > bitmapHeight ? bitmapWidth : bitmapHeight) / (needWidth > needHeight ? needWidth : needHeight) + 1;
        int scaleShort = (bitmapWidth < bitmapHeight ? bitmapWidth : bitmapHeight) / (needWidth < needHeight ? needWidth : needHeight) + 1;

        int scale = scaleLong >= scaleShort ? scaleLong : scaleShort;
        LogUtil.log(TAG, "bitmapWidth:" + bitmapWidth + "|bitmapHeight" + bitmapHeight + "needWidth:" + needWidth + "|needHeight:" + needHeight);
        LogUtil.log(TAG, "scale" + scale);

        return scale;
    }

    /**
     * 创建指定大小的图片
     * 如果图片宽度大于高度，则旋转90度
     *
     * @param bitmap
     * @return
     */
    private static Bitmap formatBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 如果宽高都小于1280，不做处理
        if (width < MAX_VALUE && height < MAX_VALUE) {
            return bitmap;
        }

        Bitmap dstBitmap = null;
        if (width > height) {
            // 等比例缩放原图，并且旋转
            float scale = (float) width / (float) MAX_VALUE;
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postRotate(90);
            dstBitmap = BitmapUtil.resizeBitmap(bitmap, matrix);
        } else {

            // 等比例缩放原图
            float scale = (float) height / (float) MAX_VALUE;
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);

            dstBitmap = BitmapUtil.resizeBitmap(bitmap, matrix);
        }

        // 回收原图
        // PS：使用Bitmap.createBitmap这个API有坑，它创建的Bitmap有可能使用是以前的那个对象做一次转化，所以不能进行回收
//        bitmap.recycle();
        bitmap = null;
        return dstBitmap;
    }

    /**
     * 获取缩略图 用于当图片的绝对路径无法取到时，用Uri从文件系统读取图片的情况
     *
     * @param context
     * @param uri
     * @param width
     * @param height
     * @return
     */

    public static Bitmap getProperBitmap(Context context, Uri uri, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;

        AssetFileDescriptor afd = null;
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            // 第一步 获取字节流 ，读取图片byte数组
            afd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            fis = afd.createInputStream();
            byte[] data = readStream(fis);

            // 第二步 获取图片宽高，绝对缩放比例
            BitmapFactory.decodeByteArray(data, 0, data.length, options);


//            options.inSampleSize = getOption(options.outWidth, options.outHeight);
            options.inSampleSize = getOptionNew(options.outWidth, options.outHeight, width, height);
            // 第三步 获取图片
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // 将图片格式化为标准大小
            bitmap = formatBitmap(bitmap);

        } catch (Exception e) {
            LogUtil.log(TAG, "exception BitmapUtil.createBitmap ");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                }
                fis = null;
            }

            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                afd = null;
            }
        }

        return bitmap;
    }


    /**
     * 上传文件前，重命名上传文件，为了避免连续发帖时，之前的文件被锁定
     *
     * @return
     */
    public static String renameMediaFileForUpload(String path) {
        String retPath = null;

        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);

            if (f.exists() && f.isFile()) {
                String newPath = PathConfig.getMediaUploadDir() + "/" + System.currentTimeMillis()+".jpg";
                File newFile = new File(newPath);
                if (f.renameTo(newFile)) {
                    retPath = newPath;
                } else {
                    // do nothing
                }
            } else {
                // do nothing
            }

        }

        return retPath;
    }

    /**
     * 保存为jpg格式
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp, String path) {
        // 首先保存图片
        FileUtil.checkFileDirExists(path);
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 插入file数据到相册
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            // 通知相册更新
            context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
