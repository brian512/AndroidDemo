/**
 * Copyright (C) 2014 Togic Corporation. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brian.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.MemoryFile;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FileUtil {
    private static String TAG = "FileUtil";

    private static long MIN_SPACE_LEFT = 20 * (1 << 20);

    public static boolean writeFile(String filePath, InputStream stream) {
        OutputStream o = null;
        if (null == stream) {
            return false;
        }
        try {
            checkFilePath(filePath);
            o = new FileOutputStream(filePath);
            byte data[] = new byte[1024];
            int length;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(stream);
            closeStream(o);
        }
    }

    public static boolean writeFileWithBackUp(String filePath, String content,
                                              boolean append) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        String tempPath = filePath + "_temp";
        String backupPath = filePath + "_backup";
        if (writeFile(tempPath, content, append)) {
            // backup file and overwrite file
            if ((!checkFileExists(filePath) || renameFile(filePath, backupPath))
                    && renameFile(tempPath, filePath)) {
                deleteFile(tempPath);
                deleteFile(backupPath);
                return true;
            } else {
                LogUtil.d("changeFileName failed!");
                deleteFile(tempPath);
                // the file maybe renamed to _backup
                if (!checkFileExists(filePath) && checkFileExists(backupPath)) {
                    LogUtil.d("rename backup to file");
                    renameFile(backupPath, filePath);
                }
                return false;
            }
        } else {
            LogUtil.e("write file failed!");
            deleteFile(tempPath);
            return false;
        }
    }

    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }


    public static boolean writeFile(String filePath, String content, boolean append) {
        for (int i = 0; i < 5; i++) {
            if (doWriteFile(filePath, content, append)) {
                return true;
            }
        }
        return false;
    }

    private static boolean doWriteFile(String filePath, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            checkFilePath(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            updateFileLastModified(filePath);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            closeStream(fileWriter);
        }
    }

    public static boolean writeFile(String filePath, byte[] content, boolean append) {
        FileOutputStream outputStream = null;
        try {
            checkFilePath(filePath);
            outputStream = new FileOutputStream(filePath, append);
            outputStream.write(content);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(outputStream);
        }
    }

    public static boolean checkFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        if (filePath.contains("/")) {
            String folder = filePath.substring(0, filePath.lastIndexOf("/"));
            ensureFolderExists(folder);
        }
        return true;
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
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeStream(in);
        return sb;

    }

    public static boolean checkFileExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return checkFileExists(file);
    }

    public static boolean checkFileExists(File file) {
        if (file != null && file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }

    public static void ensureFolderExists(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return;
        }
        File file = new File(folderPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static void checkFileDirExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        int pos = filePath.lastIndexOf("/");
        if (pos < 0) {
        } else {
            String dir = filePath.substring(0, pos);
            ensureFolderExists(dir);
        }
    }

    public static boolean changeFileName(String oldFilePath, String newFileName) {
        for (int i = 0; i < 5; i++) {
            if (doChangeFileName(oldFilePath, newFileName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean doChangeFileName(String oldFilePath, String newFileName) {
        File oldFile = new File(oldFilePath);
        if (checkFileExists(oldFile)) {
            String c = oldFile.getParent();
            File mm = new File(c + "/" + newFileName);
            return oldFile.renameTo(mm);
        } else {
            try {
                return oldFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * rename with full path
     */
    public static boolean renameFile(String oldFilePath, String newFilePath) {
        if (TextUtils.isEmpty(oldFilePath) || TextUtils.isEmpty(newFilePath)) {
            return false;
        }
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        if (newFile.exists() && newFile.isFile() && newFile.canWrite()) {
            newFile.delete();
        }
        return renameFile(oldFile, newFile);
    }

    public static boolean renameFile(File oldFile, File newFile) {
        return checkFileExists(oldFile) && oldFile.renameTo(newFile);
    }

    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File f = new File(filePath);
        return deleteFile(f);
    }

    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return true;
        }
        return file.delete();
    }

    public static void closeStream(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean inputStreamToFile(InputStream is, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(fos);
        }
        return true;
    }

    public static boolean setFileLastModified(File f, String lastModified) {
        if (StringUtil.isEmptyString(lastModified)) {
            return false;
        }
        final Date time = DateTimeUtil.parseDate(lastModified);
        return f != null && f.exists() && f.setLastModified(time.getTime());
    }

    public static boolean setCacheFileLastModified(Context ctx,
                                                         String fileName, String lastModified) {
        return setFileLastModified(ctx.getFileStreamPath(fileName),
                lastModified);
    }

    public static void updateFileLastModified(String filePath) {
        File file = new File(filePath);
        file.setLastModified(System.currentTimeMillis());
    }

    public static long getFileLastModified(String filePath) {
        File file = new File(filePath);
        return file.lastModified();
    }

    public static void clearCache(File cacheDir, long effiveTime) {
        if (cacheDir == null || !cacheDir.isDirectory()) {
            return;
        }
        Long currentTime = System.currentTimeMillis();
        File[] fileList = cacheDir.listFiles();
        if (fileList == null || fileList.length <= 0) {
            return;
        }
        for (File cache : fileList) {
            if (currentTime - cache.lastModified() > effiveTime) {
                cache.delete();
            }
        }
    }

    public static void deleteDirsAndFiles(String filepath) {
        if (!StringUtil.isEmptyString(filepath)) {
            deleteDirsAndFiles(new File(filepath));
        }
    }

    public static void deleteDirsAndFiles(File f) {
        if (f != null && f.exists() && f.canWrite()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                File[] childs = f.listFiles();
                if (childs == null || childs.length == 0) {
                    f.delete();
                } else {
                    for (File child : childs) {
                        deleteDirsAndFiles(child);
                    }
                    f.delete();
                }
            }
        }
    }

    public static boolean isDirectoryWritable(String directory) {
        if (!StringUtil.isEmptyString(directory)) {
            File dir = new File(directory);
            return dir.canWrite();
        }
        return false;
    }

    public static void deleteFilesInDir(File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] childs = dir.listFiles();
            if (childs == null || childs.length <= 0) {
                return;
            }
            for (File child : childs) {
                if (child == null || !child.exists()) {
                    continue;
                }
                if (child.isFile()) {
                    child.delete();
                } else {
                    deleteFilesInDir(child);
                    child.delete();
                }
            }
        }
    }

    public static boolean copy(InputStream in, OutputStream out) {
        try {
            if (in != null && out != null) {
                byte[] buffer = new byte[4096];
                for (int len; (len = in.read(buffer)) > 0; ) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(out);
            closeStream(in);
        }
        return true;
    }

    public static final int UNIT_B = 1;
    public static final int UNIT_KB = 2;
    public static final int UNIT_MB = 3;
    public static final int UNIT_GB = 4;

    public static double getFileSizeFormUnit(String filePath, int unit) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFolderSize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conversionFileSizeUnit(blockSize, unit);
    }

    public static String folderSizeToString(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFolderSize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSizeToString(blockSize);
    }

    private static long getFileSize(File file){
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();

            } catch (IOException e){
                LogUtil.printError(e);
            } finally {
                closeStream(fis);
            }
        }
        return size;
    }

    public static long getFolderSize(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return 0;
        }
        File file = new File(folderPath);
        if (!file.exists() || file.isFile()) {
            return 0;
        }
        return getFolderSize(file);
    }

    public static long getFolderSize(File f) {
        long size = 0;
        File[] flist = f.listFiles();
        if (flist == null || flist.length <= 0) {
            return 0;
        }
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFolderSize(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    public static String fileSizeToString(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileSize == 0) {
            return wrongSize;
        }
        if (fileSize < 1 << 10) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1 << 20) {
            fileSizeString = df.format((double) fileSize / (1 << 10)) + "KB";
        } else if (fileSize < 1 << 30) {
            fileSizeString = df.format((double) fileSize / (1 << 20)) + "MB";
        } else if (fileSize < 1 << 40) {
            fileSizeString = df.format((double) fileSize / (1 << 30)) + "GB";
        } else {
            fileSizeString = df.format((double) fileSize / (1 << 40)) + "TB";
        }
        return fileSizeString;
    }

    private static double conversionFileSizeUnit(long fileSize, int unit) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (unit) {
            case UNIT_B:
                fileSizeLong = Double.valueOf(df.format((double) fileSize));
                break;
            case UNIT_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileSize / (1 << 10)));
                break;
            case UNIT_MB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileSize / (1 << 20)));
                break;
            case UNIT_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileSize / (1 << 30)));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public static String getValueFromFile(String filePath, String key, String seprator) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(key)
                || seprator == null) {
            return null;
        }
        BufferedReader reader = null;
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                return null;
            }
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals(key) && line.contains(seprator)) {
                    reader.close();
                    return line.substring(line.indexOf(seprator) + seprator.length());
                }
            }
            closeStream(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getAllValueFromFile(String filePath, String seprator) {
        if (TextUtils.isEmpty(filePath) || seprator == null) {
            return null;
        }
        BufferedReader reader;
        HashMap<String, String> configs = new HashMap<>();
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                return null;
            }
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(seprator)) {
                    String key = line.substring(0, line.indexOf(seprator));
                    String value = line.substring(line.indexOf(seprator) + seprator.length());
                    configs.put(key, value);
                }
            }
            closeStream(reader);
            return configs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileContent(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        StringBuilder content = new StringBuilder("");
        String temString;
        try {
            File file = new File(path);
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((temString = reader.readLine()) != null) {
                content.append(temString);
            }
            closeStream(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static boolean writeBitmap(String savePath, Bitmap bt) {
        if (TextUtils.isEmpty(savePath) || bt == null || bt.isRecycled()) {
            LogUtil.e("writeBitmap failed");
            return false;
        }
        try {
            LogUtil.v("getByteCount=" + bt.getByteCount());
            File file = new File(savePath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            if (bt.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
            } else {
                LogUtil.e("bitmap compress failed");
            }
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public final static String FILE_EXTENSION_SEPARATOR = ".";

    /**
     * read file
     *
     * @return if file not exist, return null, else return content of file
     * @throws IOException if an error occurs while operator BufferedReader
     */
    public static StringBuilder readFile(String filePath) {
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
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeStream(reader);
        }
    }

    /**
     * read file to string list, a element of list is a line
     *
     * @return if file not exist, return null, else return content of file
     * @throws IOException if an error occurs while operator BufferedReader
     */
    public static List<String> readFileToList(String filePath) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<>();
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeStream(reader);
        }
    }

    /**
     * get file name from path, not include suffix
     *
     * <pre>
     *      getFileNameWithoutExtension(null)               =   null
     *      getFileNameWithoutExtension("")                 =   ""
     *      getFileNameWithoutExtension("   ")              =   "   "
     *      getFileNameWithoutExtension("abc")              =   "abc"
     *      getFileNameWithoutExtension("a.mp3")            =   "a"
     *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
     *      getFileNameWithoutExtension("c:\\")              =   ""
     *      getFileNameWithoutExtension("c:\\a")             =   "a"
     *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
     *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
     *      getFileNameWithoutExtension("/home/admin")      =   "admin"
     *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
     * </pre>
     *
     * @return file name from path, not include suffix
     * @see
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * get file name from path, include suffix
     *
     * <pre>
     *      getFileName(null)               =   null
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\\")              =   ""
     *      getFileName("c:\\a")             =   "a"
     *      getFileName("c:\\a.b")           =   "a.b"
     *      getFileName("c:a.txt\\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     *
     * @return file name from path, include suffix
     */
    public static String getFileName(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * get folder name from path
     *
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     */
    public static String getFolderName(String filePath) {

        if (StringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * get suffix of file from path
     *
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     */
    public static String getFileExtension(String filePath) {
        if (StringUtil.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * Creates the directory named by the trailing filename of this file, including the complete directory path required
     * to create this directory. <br/>
     * <br/>
     * <ul>
     * <strong>Attentions：</strong>
     * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
     * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
     * </ul>
     *
     * @param filePath
     * @return true if the necessary directories have been created or the target directory already exists, false one of
     * the directories can not be created.
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtil.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * @see {@link #makeDirs(String)}
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     *
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtil.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * delete file or directory
     * <ul>
     * <li>if path is null or empty, return true</li>
     * <li>if path not exist, return true</li>
     * <li>if path exist, delete recursion. return true</li>
     * <ul>
     */
    public static boolean deleteFileAndDir(String path) {
        if (StringUtil.isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile() && file.canWrite()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        File[] fileList = file.listFiles();
        if (fileList == null || fileList.length <= 0) {
            return true;
        }
        for (File f : fileList) {
            if (f.isFile() && file.canWrite()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * maybe useless
     */
    public static void writeMemoryFile(String filePath, String content) {
        try {
            byte[] bytes = content.getBytes("UTF8");
            MemoryFile file = new MemoryFile(filePath, bytes.length);
            OutputStream stream = file.getOutputStream();
            stream.write(bytes);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        boolean isEqual = path1.equalsIgnoreCase(path2);
        return isEqual;
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
}
