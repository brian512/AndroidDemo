
package com.brian.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5操作类
 */
public class Md5Util {

    /**
     * 获取MD5
     */
    public static String getMD5(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buf = key.getBytes();
            md.update(buf, 0, buf.length);
            return byteArray2HexStr(md.digest());
        } catch (NoSuchAlgorithmException e) {
            JDLog.printError(e);
        }
        return key;
    }

    /**
     * 计算文件MD5值
     */
    public static String getMD5OfFile(File file) {
        InputStream fis = null;
        byte[] buffer = new byte[4096];
        try {
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            return byteArray2HexStr(md5.digest());
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private static String byteArray2HexStr(byte[] bytes) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(32);
        for (byte b : bytes) {
            sb.append(hex[((b >> 4) & 0xF)]).append(hex[(b & 0xF)]);
        }
        return sb.toString();
    }

}
