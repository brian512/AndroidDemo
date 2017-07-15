package com.brian.common.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.brian.common.Env;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 读取Assets文件
 * Created by huamm on 2016/11/16 0016.
 */

public class AssetsUtil {

    public static String getFileContent(String filePath) {
        InputStream inputStream = getStream(filePath);
        if (inputStream != null) {
            StringBuilder builder = FileUtil.readFromInputStream(inputStream);
            if (builder != null) {
                return builder.toString();
            }
        }
        return "";
    }

    public static Bitmap readBitmap(String filePath) {
        InputStream inputStream = getStream(filePath);
        if (inputStream != null) {
            return BitmapFactory.decodeStream(inputStream);
        }
        return null;
    }

    public static boolean checkFile(String filePath) {
        return getStream(filePath) != null;
    }

    private static InputStream getStream(String filePath) {
        try {
            return Env.getContext().getAssets().open(filePath);
        } catch (IOException e) {
            JDLog.logError("file is not exist : " + filePath);
            JDLog.printError(e);
        }
        return null;
    }

    public static boolean copyFileFromAssets(String assetsFilePath, String toFilePath) {
        JDLog.log("assetsFilePath=" + assetsFilePath + "; toFilePath=" + toFilePath);
        InputStream stream = getStream(assetsFilePath);
        if (stream != null) {
            OutputStream out = null;
            try {
                out = new FileOutputStream(toFilePath);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                return true;
            } catch (Exception e) {
                JDLog.printError(e);
                return false;
            } finally {
                FileUtil.closeIO(stream);
                FileUtil.closeIO(out);
            }
        }
        return false;
    }

    public static void copyDirFromAssets(String assetsDirPath, String toDirPath) {
        AssetManager assetManager = Env.getContext().getResources().getAssets();
        String[] files = null;
        try {
            files = assetManager.list(assetsDirPath);
        } catch (Exception e) {
            JDLog.printError(e);
        }
        if (files == null || files.length <= 0) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(assetsDirPath + "/" + files[i]);
                out = new FileOutputStream(toDirPath + files[i]);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
            } catch (Exception e) {
                JDLog.printError(e);
            } finally {
                FileUtil.closeIO(in);
                FileUtil.closeIO(out);
            }
        }
    }
}
