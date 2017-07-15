package com.brian.common.util;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 文件操作辅助类
 */
public class FileUtil {

    /**
     * 确保目录存在
     */
    public static boolean ensureDir(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        boolean ret = false;

        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            try {
                ret = file.mkdirs();
            } catch (SecurityException se) {
                JDLog.printError(se);
            }
        } else {
            ret = true;
        }

        return ret;
    }

    /**
     * 确保文件所在的目录存在
     *
     * @param path 文件全路径
     */
    public static boolean ensureFileParentDir(String path) {
        String parentDir = getFileParentAbsPath(path);
        return ensureDir(parentDir);
    }

    /**
     * 确保文件存在
     */
    public static boolean ensureFile(String path) {
        if (null == path) {
            return false;
        }

        boolean ret = false;

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            try {
                file.createNewFile();
                ret = true;
            } catch (IOException e) {
                JDLog.printError(e);
            }
        } else {
            ret = true;
        }

        return ret;
    }

    /**
     * 获取文件后缀名
     */
    public static String getFileSuffix(String fileName) {
        String fileType = null;
        if (fileName != null) {
            int idx = fileName.lastIndexOf(".");
            if (idx > 0) {
                fileType = fileName.substring(idx + 1, fileName.length()).toLowerCase();
            }
        }
        return fileType;
    }

    /**
     * 获取文件名
     */
    public static String getFileNameFromPath(String filePath) {
        String name = null;
        if (filePath != null) {
            int idx = filePath.lastIndexOf("/");
            if (idx > 0) {
                name = filePath.substring(idx + 1, filePath.length())
                        .toLowerCase();
            } else {
                name = filePath;
            }
        }
        return name;
    }

    /**
     * 返回文件的所在的目录的绝对路径
     *
     * @return 返回文件的所在的目录的绝对路径, 不含最后的斜杠分隔符
     */
    public static String getFileParentAbsPath(String filePath) {
        File file = new File(filePath);
        return file.getParent();
    }

    /**
     * 判断两个路径是否相等 大小写不敏感 : 存储卡的文件系统一般为FAT, 大小写不敏感
     */
    public static boolean isPathEqual(final String pathSrc, final String pathDst) {
        if (pathSrc == null || pathDst == null) {
            return false;
        }

        String path1 = pathSrc.endsWith("/") ? pathSrc : pathSrc + "/";
        String path2 = pathDst.endsWith("/") ? pathDst : pathDst + "/";
        return path1.equalsIgnoreCase(path2);
    }


    /**
     * 获取文件类型（后缀）
     */
    public static String getFileTypeByName(String name, String defaultValue) {
        String type = defaultValue;
        if (name != null) {
            int idx = name.lastIndexOf(".");
            if (idx != -1) {
                type = name.substring(idx + 1, name.length());
            }
        }
        return type;
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeIO(reader);
        }
    }

    public static StringBuilder readFromInputStream(InputStream in) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        if (reader == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            JDLog.printError(e);
        } finally {
            closeIO(reader);
        }
        return sb;
    }

    public static void writeFile(String path, String content, boolean append) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(path, append);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.isFile() && file.exists();
    }

    public static boolean isEmptyDir(String dirPath) {
        File file = new File(dirPath);
        if (file.exists() && file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null || children.length == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件夹是否存在
     */
    public static boolean isDirExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.isDirectory();
    }

    /**
     * 获取文件大小，单位字节
     */
    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }

        long size = 0;
        File file = new File(path);
        if (file.isFile()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean ret = false;

        File file = new File(path);
        if (file.exists() && file.isFile()) {
            ret = file.delete();
        }
        return ret;
    }

    public static boolean deleteDir(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean ret = false;

        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] children = file.listFiles();
            for (File c : children) {
                if (c.isDirectory()) {
                    ret = deleteDir(c.getPath());
                } else if (c.isFile()) {
                    ret = deleteFile(c.getPath());
                }
            }
            if (ret) {
                ret = file.delete();
            }
        }
        return ret;
    }

    /**
     * Java：判断文件的编码
     *
     * @param sourceFile 需要判断编码的文件
     * @return String 文件编码
     */
    public static String getFilecharset(File sourceFile) {
        String charset = null;
        byte[] first3Bytes = new byte[3];

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bis.mark(0);

            int read = bis.read(first3Bytes, 0, 3);

            if (read == -1) {
                bis.close();
                return null;
            }

            if (first3Bytes[0] == (byte) 0x5c && first3Bytes[1] == (byte) 0x75) {
                charset = "ANSI";   // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
            } else {
                charset = "GBK";
            }

            bis.reset();
        } catch (Exception e) {
            JDLog.printError(e);
        } finally {
            closeIO(bis);
        }

        return charset;
    }

    /**
     * 拷贝文件
     *
     * @param fromPath 源文件
     * @param toPath   目标文件
     * @param rewrite  是否重写
     */
    public static int copyfile(String fromPath, String toPath, Boolean rewrite) {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);

        if (!fromFile.exists() || !fromFile.isFile() || !fromFile.canRead()) {
            return -1;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        // 目标文件已存在
        if (toFile.exists()) {

            // 重写，则删除之前的文件
            if (rewrite) {
                toFile.delete();
            }
            // 否则失败
            else {
                return -1;
            }
        }
        FileInputStream fosFrom = null;
        FileOutputStream fosTo = null;
        try {
            fosFrom = new FileInputStream(fromFile);
            fosTo = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosFrom.read(bt)) > 0) {
                fosTo.write(bt, 0, c); // 将内容写到新文件当中
            }
            fosFrom.close();
            fosTo.close();
            return 0;
        } catch (Exception ex) {
            // Log.e("readfile", ex.getMessage());
            return 1;
        } finally {
            closeIO(fosFrom);
            closeIO(fosTo);
        }
    }

    public static boolean renameFile(String fileNameO, String fileNameN) {
        if (TextUtils.isEmpty(fileNameN) || TextUtils.isEmpty(fileNameO)) {
            throw new IllegalArgumentException();
        }
        File fileO = new File(fileNameO);
        if (!fileO.exists() || !fileO.isFile()) {
            return false;
        }
        File fileN = new File(fileNameN);
        if (fileN.exists() && fileN.isFile()) {
            fileN.delete();
        } else {
            ensureFileParentDir(fileNameN);
        }
        return fileO.renameTo(fileN);
    }

    /**
     * 判断一个本地文件是否是图片
     */
    public static boolean isFilePicture(String absolutePath) {
        boolean isPicture = false;

        if (!TextUtils.isEmpty(absolutePath)) {
            String[] tmpStrs = absolutePath.split(".");
            if (tmpStrs.length > 1) {

                final String[] PIC_SUFFIX = {
                        "jpg",
                        "png",
                        "bmp",
                        "jpeg",
                        "gif"
                };

                String suffix = tmpStrs[tmpStrs.length - 1];

                for (int i = 0; i < PIC_SUFFIX.length; i++) {
                    if (PIC_SUFFIX[i].equalsIgnoreCase(suffix)) {
                        isPicture = true;
                        break;
                    }
                }
            }
        }

        // 尝试解码看是否是一张图片
        if (!isPicture) {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(absolutePath, options);
            if (options.outHeight > 0 && options.outWidth > 0) {
                isPicture = true;
            }
        }

        return isPicture;
    }

    /**
     * 判断一个本地文件是否是图片
     *
     * @return 注意：这个方法不靠谱
     */
    @Deprecated
    public static boolean isFilePicture(Activity activity, Uri uri) {
        String absolutePath = UriUtil.getPath(activity, uri);
        return isFilePicture(absolutePath);
    }

    public static boolean isSDCardExist() {
        return Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
                .getExternalStorageState());
    }

    public static void closeIO(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                JDLog.printError(e);
            }
        }
    }
}
