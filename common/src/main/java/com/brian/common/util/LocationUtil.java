package com.brian.common.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import com.brian.common.Env;
import com.brian.common.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.GPS_PROVIDER;

/**
 * 地理位置的工具类
 */
public class LocationUtil {

    /**
     * 记录最新的位置
     */
    private static Address sAddress = null;

    private static Context sContext;

    private static OnLocationListener mListener;

    /**
     * 监听地址信息
     */
    public interface OnLocationListener {
        /**
         * 获取到地址信息后回调
         */
        void onLocated(Address address);
    }

    private static LocationListener sLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            JDLog.log("location=" + location);
            notifyLocation(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            JDLog.log("provider=" + provider);
        }
        @Override
        public void onProviderEnabled(String provider) {
            JDLog.log("provider=" + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            JDLog.log("provider=" + provider);
        }
    };

    public static void initLocation(Context context) {
        sContext = context.getApplicationContext();
        ThreadPoolManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                sAddress = generateAddressByLocation(sContext, getLastKnownLocation(sContext));
            }
        });
    }

    /**
     * 判断GPS是否可用
     */
    public static boolean isGpsEnabled(Context context) {
        return getLocationManager(context).isProviderEnabled(GPS_PROVIDER);
    }

    /**
     * 打开设置GPS界面
     */
    public static void showLocationSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settingsIntent);
    }

    public static Address getAddress(Context context, OnLocationListener listener) {
        mListener = listener;
        sContext = context.getApplicationContext();

        ThreadPoolManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                notifyLocation(getLastKnownLocation(sContext));
            }
        });

        refreshLocation(context);

        return sAddress;
    }

    /**
     * 获取上次位置信息
     */
    public static Location getLastKnownLocation(Context context) {
        if (!checkPermission()) {
            JDLog.logError("no permission");
            return null;
        }
        LocationManager locationManager = getLocationManager(context);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);// 粗略定位
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费:可以使用网络来定位
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        String provider = locationManager.getBestProvider(criteria, true);//从可用的位置提供器中，匹配以上标准的最佳提供器
        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) { // 尝试其他provider
            List<String> allProviders = locationManager.getAllProviders();
            if (allProviders == null) {
                allProviders = new ArrayList<>(3);
            }
            allProviders.add(0, GPS_PROVIDER);
            allProviders.add(1, LocationManager.NETWORK_PROVIDER);
            allProviders.add(2, LocationManager.PASSIVE_PROVIDER);

            for (String str : allProviders) {
                if (locationManager.isProviderEnabled(str)) {
                    location = locationManager.getLastKnownLocation(str);
                    if (location != null) {
                        break;
                    }
                }
            }
        }
        JDLog.log("Location = "+location);
        return location;
    }

    /**
     * 更新定位，重新发起定位请求
     */
    public static void refreshLocation(Context context) {
        if (!checkPermission()) {
            JDLog.logError("no permission");
            return;
        }
        LocationManager locationManager = getLocationManager(context);
        List<String> allProviders = locationManager.getProviders(true);
        allProviders.add(0, LocationManager.GPS_PROVIDER);
        allProviders.add(1, LocationManager.NETWORK_PROVIDER);
        for (String p : allProviders) {
            JDLog.log("provider:" + p + "=" + locationManager.isProviderEnabled(p));
            if (locationManager.isProviderEnabled(p)) {
                locationManager.requestLocationUpdates(p, 60_000, 50, sLocationListener);
            }
        }
    }

    /**
     * 回调结果
     */
    private static void notifyLocation(final Location location) {
        JDLog.log("notifyLocation");
        if (mListener != null) {
            Address address = generateAddressByLocation(sContext, location);
            if (address != null) {
                mListener.onLocated(address);
            }
        }
    }

    public static Address generateAddressByLocation(Context context, Location location) {
        if (location == null) {
            JDLog.logError("location is null");
            return null;
        }
        Address address = null;
        try {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            if (!Geocoder.isPresent()) {
                JDLog.logError("isPresent=" + Geocoder.isPresent());
                return null;
            }
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            if (addresses == null || addresses.isEmpty()) {
                JDLog.logError("addresses is null");
            } else {
                address = addresses.get(0);
                JDLog.log("address=" + address.toString());
            }
        } catch (Exception e) {
            JDLog.printError(e);
        }
        return address;
    }

    public static LocationManager getLocationManager(Context context) {
        return  (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private static boolean checkPermission() {
        return ContextCompat.checkSelfPermission(Env.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(Env.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
