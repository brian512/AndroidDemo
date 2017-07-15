package com.brian.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtil {

    /**
     * gzip 压缩
     *
     * @param str
     * @return
     */
    public static byte[] gzipStringToByte(byte[] str) {
        byte[] data = null;
        if (str != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gos = new GZIPOutputStream(baos);
                gos.write(str);
                gos.flush();
                gos.finish();
                gos.close();

                data = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                JDLog.printError(e);
            }
        }

        return data;
    }

    /**
     * gzip 压缩
     *
     * @param str
     * @return
     */
    public static byte[] gzipStringToByte(String str) {
        byte[] data = null;
        if (str != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gos = new GZIPOutputStream(baos);
                gos.write(str.getBytes());
                gos.flush();
                gos.finish();
                gos.close();

                data = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                JDLog.printError(e);
            }
        }

        return data;
    }

    /**
     * gzip 解压
     *
     * @param str
     * @return
     */
    public static String ungzipString(byte[] str) {
        String result = null;

        if (str != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(str);
                GZIPInputStream gis = new GZIPInputStream(bais);
                int bytes = 0;
                byte[] buf = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while (-1 != (bytes = gis.read(buf, 0, buf.length))) {
                    baos.write(buf, 0, bytes);
                }

                result = baos.toString();
                baos.flush();
                baos.close();
                gis.close();
                bais.close();
            } catch (Exception e) {
                JDLog.printError(e);
            }
        }
        return result;
    }

    /**
     * gzip 解压
     *
     * @param str
     * @return
     */
    public static String ungzipString(String str) {
        String result = null;

        if (str != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
                GZIPInputStream gis = new GZIPInputStream(bais);
                int bytes = 0;
                byte[] buf = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while (-1 != (bytes = gis.read(buf, 0, buf.length))) {
                    baos.write(buf, 0, bytes);
                }

                result = baos.toString();
                baos.flush();
                baos.close();
                gis.close();
                bais.close();
            } catch (Exception e) {
                JDLog.printError(e);
            }
        }
        return result;
    }

}
