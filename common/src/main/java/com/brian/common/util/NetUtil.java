package com.brian.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 判断网络的的工具类
 */
public abstract class NetUtil {

    /**
     * 网络类型，取值为自动以
     */
    public static final int NETWORK_TYPE_NONE   = 0;
    public static final int NETWORK_TYPE_WIFI   = 1;
    public static final int NETWORK_TYPE_2G     = 2;
    public static final int NETWORK_TYPE_3G     = 3;
    public static final int NETWORK_TYPE_4G     = 4;

    /**
     * 获取当前网络类型
     * 
     * @param context
     * @return
     */
    public static int getNetType(Context context) {

        int type = NETWORK_TYPE_NONE;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // 判断2G/3G/4G
        if (null != mobileInfo && mobileInfo.isConnected()) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int netType = tm.getNetworkType();

            // 取值参考TelephonyManager.getNetworkClass

            switch (netType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    type = NETWORK_TYPE_2G;
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    type = NETWORK_TYPE_3G;
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    type = NETWORK_TYPE_4G;
                    break;
                default:
                    type = NETWORK_TYPE_NONE;
                    break;
            }

        }
        // 判断wifi
        if (null != wifiInfo && wifiInfo.isConnected()) {
            type = NETWORK_TYPE_WIFI;
        }
        return type;
    }

    /**
     * 网络是否可用
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        Context ct = context.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) ct
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            return false;
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (null != info) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 当前是否wifi网络
     * 
     * @param context
     * @return
     */
    public static boolean isWifiNet(Context context) {
        boolean bRet = false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo && wifiInfo.isConnectedOrConnecting()) {
            bRet = true;
        }
        return bRet;
    }

    /**
     * 当前是否2G/3G/4G网络
     * 
     * @param context
     * @return
     */
    public static boolean isMobileNet(final Context context) {
        boolean ret = false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo mobileInfo = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != mobileInfo && mobileInfo.isConnectedOrConnecting()) {
                ret = true;
            }
        }
        return ret;
    }

	
	/**
	 * 获取连接wifi的MAC地址（BSSID）
	 */
//	public static String getWifiMacAddress(Context context) {
//		WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo wifiInfo = wifi_service.getConnectionInfo();
//		String bssid = wifiInfo.getBSSID();
//		if (bssid == null) {
//			bssid = "";
//		}
//		return bssid;
//	}	
    public static String getWifiMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取链接wifi的名称（SSID）
     */
    public static String getWifiSsid(Context context) {
        WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if (ssid == null) {
            ssid = "";
        }
        return ssid;
    }

    /**
     * 获取本地ip，格式192.168.1.0
     */
    public static String getlocalIP(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0)
            return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    /**
     * 获取ip地址
     * 
     * @param context
     * @return
     * @throws SocketException
     */
    public static String getIPAddress(Context context) throws SocketException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } else {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        return null;
    }
}
