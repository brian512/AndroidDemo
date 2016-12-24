package com.brian.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.brian.common.Env;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * 获取设备的信息
 */
public class DeviceUtil {


    /**
     * 获取分辨率，格式：640x480
     */
    public static String getResolution(Context context) {
        String resolution = context.getResources().getDisplayMetrics().widthPixels
                + "x" + context.getResources().getDisplayMetrics().heightPixels;
        return resolution;
    }

    /**
     * 获取屏幕密度
     */
    public static int getScreenDensity(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）

        return densityDpi;
    }

    /**
     * 获取分辨率的宽度
     */
    private static int sScreenWidth = 0;

    public static int getScreenWidth(Context context) {
        if (sScreenWidth == 0) {
            sScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }
        return sScreenWidth;
    }

    /**
     * 获取分辨率的高度
     */
    private static int sScreenHeight = 0;

    public static int getScreenHeight(Context context) {
        if (sScreenHeight == 0) {
            sScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        }
        return sScreenHeight;
    }

    /**
     * 状态栏的高度；注：上面获取的屏幕高度包含了状态栏的高度
     * PS：
     * image1、有些时候获取不到
     * 2、注意此函数不能在Activity的onCreate执行，否则获取状态栏高度为0
     */
    private static int sStatusHeight = 0;

    public static int getStatusHeight(Activity activity) {
        if (sStatusHeight == 0) {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            sStatusHeight = frame.top;
        }
        return sStatusHeight;
    }

    /**
     * 新方法：状态栏的高度，反射的方法获取
     * @return
     */
    public static int getStatusHeightNew() {

        if (sStatusHeight != 0) {
            return sStatusHeight;
        }
        int sbar = 38;// 默认为38，貌似大部分是这样的
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            sbar = Env.getContext().getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // 保存结果下次就不计算了
        sStatusHeight = sbar;

        return sbar;
    }


    /**
     * 获取手机厂家
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取序列号
     */
    public static String getSerial() {
        return Build.SERIAL;
    }


    /**
     * 获取手机操作系统
     */
    public static String getSystemId() {
        String systemId = Build.VERSION.RELEASE;
        return systemId;
    }


    /**
     * 获取手机SDK版本号
     */
    public static int getSdkVersion() {
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion;
    }

    /**
     * 获取CPU类型
     */
    private static String sCPU = null;

    public static String getCPU() {

        // 获取开销较长，只获取一次
        if (sCPU != null) {
            return sCPU;
        }
        sCPU = "";

        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""}; // image1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
            sCPU = cpuInfo[0];
        } catch (IOException e) {

        }
        return sCPU;
    }

    /**
     * 获取linux内核版本号
     */
    private static String sKernel = null;

    public static String getKernel() {

        // 获取开销较长，只获取一次
        if (sKernel != null) {
            return sKernel;
        }
        sKernel = "";

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {

        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);
        String result = "";
        String line;

        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
                // result += " ";
            }
        } catch (IOException e) {

        }

        if (result != "") {
            String Keyword = "version ";
            int index = result.indexOf(Keyword);
            line = result.substring(index + Keyword.length());
            index = line.indexOf(" ");
            sKernel = line.substring(0, index);
        }
        return sKernel;
    }

    /**
     * 获取imei号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telMg = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telMg.getDeviceId();
    }


    /**
     * 获取手机号码
     */
    public static String getMobileNum(Context context) {
        TelephonyManager telMg = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mobileNum = telMg.getLine1Number();
        if (mobileNum == null) {
            mobileNum = "";
        }
        return mobileNum;
    }


    /**
     * 返回内置sd卡路径,带后置分隔符 要获取下载目录, 请使用DownloadConfig.getDownloadPath
     *
     * @return
     */
    public static String getSDCardDir() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (!sdcardExist) {
            return null;
        }

        if (null != sdcardPath && !sdcardPath.endsWith("/")) {
            sdcardPath = sdcardPath + "/";
        }
        return sdcardPath;
    }

    /**
     * 返回内置sd卡路径,带后置分隔符 要获取下载目录, 请使用DownloadConfig.getDownloadPath
     *
     * @return
     */
    public static String getPrimarySDCard() {
        // return Environment.getExternalStorageDirectory().getPath();
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        if (null != sdcardPath && !sdcardPath.endsWith("/")) {
            sdcardPath = sdcardPath + "/";
        }
        return sdcardPath;
    }

    /**
     * 返回外置sd卡路径,带后置分隔符 返回除内置存储器外, 可用空间最大的存储器
     *
     * @return
     */
    private static String sSlaveSDCard = null;

    public static String getSlaveSDCard() {

        if (sSlaveSDCard != null) {
            return sSlaveSDCard;
        }

        // 内置SD卡路径
        String primarySDCard = getPrimarySDCard();

        ArrayList<String> storageArrayList = getMountedDevicesList();

		/*
         * ----------------------------------------------------------------------
		 * ------- 特殊情况： 有些手机加载SD卡比较特殊，在设备列表中找不到该路径，这里山寨一点，加入一些
		 * 常用的外置SD路径，如果设别列表路径没有，则从里面猜一个
		 * ------------------------------------------
		 * -----------------------------------
		 */
        storageArrayList.add("/mnt/external1");
        //
        // ... 如有需要后续这里可以再加其他路径
        // ...

        final int INVALID_INDEX = -1;
        int indexOfMaxSize = INVALID_INDEX;
        long maxSize = 0;
        for (int i = 0; i < storageArrayList.size(); i++) {

            String storagePath = storageArrayList.get(i);

            // 过来内置SD卡
            if (FileUtil.isPathEqual(storagePath, primarySDCard)) {
                continue;
            }

            // 过来不存在的路径
            if (FileUtil.isDirExist(storagePath) == false) {
                continue;
            }

            // 比较取内存最大的路径
            long availableSize = getAvailableSizeOf(storagePath);
            if (availableSize > maxSize) {
                maxSize = availableSize;
                indexOfMaxSize = i;
            }
        }

        String path;
        if (indexOfMaxSize == INVALID_INDEX) {
            path = null;
        } else {
            path = storageArrayList.get(indexOfMaxSize);
            if (!path.endsWith("/")) {
                path += "/";
            }
        }

        sSlaveSDCard = path;
        return path;
    }


    /**
     * 通过读取/etc/vold.fstab文件来解析出已加载的存储器, 得到其路径列表
     *
     * @return 可用存储器路径列表
     */
    public static ArrayList<String> getMountedDevicesList() {

        // mount配置文件: /etc/vold.fstab
        final File VOLD_FSTAB = new File(Environment.getRootDirectory()
                .getAbsoluteFile()
                + File.separator
                + "etc"
                + File.separator
                + "vold.fstab");

        // mount命令语法: dev_mount <label> <mount_point> <part> <sysfs_path1...>
        // mount命令示例: dev_mount sdcard /mnt/sdcard image1
        // /devices/platform/mmci-omap-hs.image1/mmc_host/mmc0
        final String MOUNT = "dev_mount";
        // final int INDEX_LABEL = image1;
        final int INDEX_MOUNT_POINT = 2;
        // final int INDEX_PARTITION = 3;
        final int INDEX_SYSFS_PATH = 4;

        ArrayList<String> volumnPathList = new ArrayList<String>();
        if (VOLD_FSTAB.exists() == false) {
            return volumnPathList;
        }

        ArrayList<String> mountCmdLines = new ArrayList<String>();
        try {
            mountCmdLines.clear();
            BufferedReader reader = new BufferedReader(new FileReader(
                    VOLD_FSTAB));
            String textLine = null;
            while ((textLine = reader.readLine()) != null) {
                if (textLine.startsWith(MOUNT)) {
                    mountCmdLines.add(textLine);
                }
            }
            reader.close();
            mountCmdLines.trimToSize();
        } catch (IOException e) {
        }

        for (final String mountCmdLine : mountCmdLines) {
            if (mountCmdLine == null) {
                continue;
            }

            String[] infos = mountCmdLine.split(" ");
            if (infos == null || infos.length < INDEX_SYSFS_PATH) {
                continue;
            }

            String path = infos[INDEX_MOUNT_POINT];
            if (path == null) {
                continue;
            }

            if (!new File(path).exists()) {
                continue;
            }

            volumnPathList.add(path);
        }

        return volumnPathList;
    }

    /**
     * 获取指定磁盘的可用空间
     *
     * @param storagePath
     * @return
     */
    public static long getAvailableSizeOf(final String storagePath) {
        StatFs stat = new StatFs(storagePath);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取扩展SD卡的总大小
     *
     * @return
     */
    public static long getTotalExtSdCardMemorySize() {
        String path = getSlaveSDCard();
        if (path == null)
            return 0;
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取扩展SD卡的空余大小
     *
     * @return
     */
    public static long getFreeExtSdCardMemorySize() {
        // String path = getSlaveSDCard();
        String path = getSDCardDir();
        if (path == null)
            return 0;
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        // long totalBlocks = stat.getFreeBlocks();
        long availableBocks = stat.getAvailableBlocks();
        long totalUsedBlocks = stat.getBlockCount() - availableBocks;

        return totalUsedBlocks * blockSize;
    }

    /**
     * 获取SD卡的总大小
     *
     * @return
     */
    public static long getTotalExternalMemorySize() {
        String path = Environment.getExternalStorageDirectory().getPath();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取SD卡的剩余大小（包括不可用部分）
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        String path = Environment.getExternalStorageDirectory().getPath();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取SD卡的剩余大小
     *
     * @return
     */
    public static long getFreeExternalMemorySize() {
        String path = Environment.getExternalStorageDirectory().getPath();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getFreeBlocks();
        return totalBlocks * blockSize;
    }

    /**
     * 获取系统内存的剩余大小(not ram)
     */
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取系统内存的总大小(not ram)
     */
    static public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 判断系统的内置SD卡是否为内存虚拟出来的
     */
    @SuppressLint("NewApi")
    static public boolean isExternalStorageEmulated() {

        if (Build.VERSION.SDK_INT >= 11) {
            boolean b = Environment.isExternalStorageEmulated(); // SDK版本号要高于11才能用
            return b;

        } else {
            return false;

        }

    }

    /**
     * 判断系统的内置SD卡是否可插拔
     */
    @SuppressLint("NewApi")
    static public boolean isExternalStorageRemovable() {

        if (Build.VERSION.SDK_INT >= 11) {
            boolean b = Environment.isExternalStorageRemovable(); // SDK版本号要高于11才能用
            return b;

        } else {
            return false;

        }
    }

    /**
     * 获取序列号
     *
     * @return
     */
    public static String getSerialId(Context context) {

        String seria_num = null;
        try {

            Class<?> c = Class.forName("android.os.SystemProperties");
            Method m = c.getMethod("get", String.class);
            seria_num = (String) m.invoke(c, "ro.serialno");

        } catch (Exception e) {
        }
        if (seria_num == null || seria_num.equals("")) {
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tManager.getDeviceId();
            return deviceId != null ? deviceId : "unknown";
        }

        return seria_num;
    }


    /**
     * 获取CPU类型
     */
    public static String getCpuType() {
        String cpuType = null;
        Process process = null;
        InputStream is = null;
        try {
            process = Runtime.getRuntime().exec("getprop ro.board.platform");
            is = process.getInputStream();
            byte buffer[] = new byte[128];
            is.read(buffer);

            cpuType = new String(buffer);
            int index = cpuType.indexOf("\n");
            if (index != -1) {
                cpuType = cpuType.substring(0, index);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (process != null) {
                process.destroy();
            }
        }

        return cpuType;
    }


    /***
     * 通过读取"/proc/meminfo"系统内存信息文件获取，因为
     * 从API16开始才添加了从ActivityManager读取MemoryInfo.TotalMem 的方法，因此需要从文件读取 MemTotal:
     * 94096 kB MemFree: 1684 kB
     *
     * @return
     */
    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2 = "";
        String[] arrayOfString;
        long initial_memory = 0;
        FileReader localFileReader = null;
        BufferedReader localBufferedReader = null;
        try {
            localFileReader = new FileReader(str1);
            localBufferedReader = new BufferedReader(localFileReader, 8192);

            str2 = localBufferedReader.readLine(); // 读取meminfo第一行，总的内存信息

            arrayOfString = str2.split("\\s+"); // 多个空格,回车,换行等空白符

            // 获得系统总内存，单位是KB，乘以1024转换为Byte
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;

            localFileReader.close();
            localBufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                localFileReader.close();
                localBufferedReader.close();
            } catch (Exception ex) {

            }
        }

        return initial_memory;
    }

    /**
     * 获取总的内存大小,设置成静态的，UI可以直接调用获取
     *
     * @return 总的内存大小
     */
    public static long getTotalMemorySize() {
        long totalMemory = getTotalMemory();
        return totalMemory;
    }

    /**
     * 获取剩余内存大小
     *
     * @return 剩余的内存大小
     */
    public static long getHasMemorySize(Context context) {
        MemoryInfo memoryInfo = new MemoryInfo();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.getMemoryInfo(memoryInfo);
        long hasMemory = memoryInfo.availMem;
        return hasMemory;
    }


    /**
     * 获得CPU频率
     *
     * @return
     */
    public static String getCurCpuFreq() {
        String result = "N/A";
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 获取CPU最大频率（单位KHZ）
     * "/system/bin/cat" 命令行
     * "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
     *
     * @return
     */
    public static long getMaxCpuFreq() {
        String result = null;
        long cpuFreq = -1;
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
            cpuFreq = Long.valueOf(result);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cpuFreq;
    }

    /**
     * 获取cpu 内核数
     *
     * @return
     */
    public static int getCpuNum() {
        int cpuNum = 1;
        File file = new File("/sys/devices/system/cpu/cpu1");
        if (file.exists())
            cpuNum = 2;

        return cpuNum;
    }


    /**
     * Checks if OpenGL ES 2.0 is supported on the current device.
     *
     * @param context the context
     * @return true, if successful
     */
    public static boolean supportsOpenGLES2(final Context context) {
        final ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    /**
     * 判断当前设备是否为模拟器
     *
     * @return
     */
    public static boolean isSimulator() {
        try {
            // 判断imei是否非法
            TelephonyManager tm = (TelephonyManager) Env.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }

            // 判断设备型号
            if ((Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"))) {
                return true;
            }

//            // 判断CPU架构，这里的假设是CPU只可能是ARM的，这种判断方式可能不准确
//            String cpuModelStr = getCPU();
//            JDLog.log(cpuModelStr);
//            if (!TextUtils.isEmpty(cpuModelStr) && !cpuModelStr.contains("arm")) {
//                return true;
//            }

        } catch (Exception ioe) {

        }
        return false;
    }

    /**
     * 判断当前设备是否为魅族手机
     *
     * @return
     */
    public static boolean isMeizu() {
        Build build = new Build();
        if (build.BRAND.indexOf("Meizu") != -1) {
            return true;
        }
        return false;
    }
}
