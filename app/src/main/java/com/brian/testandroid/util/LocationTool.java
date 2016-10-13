package com.brian.testandroid.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.brian.testandroid.common.Env;
import com.brian.testandroid.common.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 通过定位当前的经纬度获取位置信息
 * Created by huamm on 2016/7/1 0001.
 */
public class LocationTool {

    private static final int INTERVAL_UPDATE_LOCATION = 60 * 1000;//一分钟更新一次

    private static final LocationTool sInstance = new LocationTool();


    private LocationManager mLocationManager;
    private Address mAddress;

    private LocationListener mLocationListener;

    private Handler mHandler;


    private LocationTool() {
    }

    public static LocationTool getInstance() {
        return sInstance;
    }

    /**
     * 监听地址信息
     */
    public interface OnLocationListener {
        /**
         * 获取到地址信息后回调
         * @param address
         */
        public void onLocated(Address address);
    }

    /**
     * 初始化定位信息，刷新定位请调用refreshLocation()，每次定位成功后会移除定位监听
     */
    public void initLocationInfo() {

        mLocationManager = (LocationManager) Env.getContext().getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LogUtil.log("location=" + location.getProvider());
                convertAddress(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        LogUtil.log("checkPermission=" + checkPermission());
        if (checkPermission()) {
            mLocationManager.requestLocationUpdates(getProvider(), INTERVAL_UPDATE_LOCATION, 0, mLocationListener);
        }

        convertAddress(getLastKnownLocation());
    }

    public void gettAddress(Location location) {
        convertAddress(location);
    }

    /**
     * 更新定位，重新发起定位请求
     */
    public void refreshLocation() {
        if (checkPermission()) {
            mLocationManager.requestLocationUpdates(getProvider(), INTERVAL_UPDATE_LOCATION, 0, mLocationListener);
        }
    }

    public Address getCurrAddress() {
        return mAddress;
    }

    /**
     * 异步转换地址
     * @param location
     */
    private void convertAddress(final Location location) {
        ThreadPoolManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                mAddress = generateAddressByLocation(location);
                if (mAddress != null) {
                    notifyLocation(mAddress);
                }
                if (checkPermission()) {
                    mLocationManager.removeUpdates(mLocationListener);
                }
            }
        });
    }

    private Address generateAddressByLocation(Location location) {
        if (location == null) {
            LogUtil.e("location is null");
            return null;
        }
        Address address = null;
        try {
            Geocoder gcd = new Geocoder(Env.getContext(), Locale.getDefault());
            if (!gcd.isPresent()) {
                LogUtil.e("isPresent=" + gcd.isPresent());
                return null;
            }
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            if (addresses == null || addresses.isEmpty()) {
                LogUtil.e("addresses is null");
            } else {
                address = addresses.get(0);
                LogUtil.log("address=" + address.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    /**
     * 获取当前城市名
     * @return
     */
    public String getCityName() {
        String cityName = "";
        if (mAddress != null) {
            cityName = mAddress.getLocality();
            if (TextUtils.isEmpty(cityName)) {
                cityName = mAddress.getSubLocality();
            }
        }
        return cityName;
    }

    /**
     * 获取当前省(中)/州(美)。。。
     * @return
     */
    public String getAdminName() {
        String adminName = "";
        if (mAddress != null) {
            adminName = mAddress.getAdminArea();
            if (TextUtils.isEmpty(adminName)) {
                adminName = mAddress.getSubAdminArea();
            }
        }
        return adminName;
    }

    /**
     * 获取国家名
     * @return
     */
    public String getCountyName() {
        if (mAddress != null) {
            return mAddress.getCountryName();
        } else {
            return "";
        }
    }

    /**
     * 获取街道名
     * @return
     */
    public String getStreetName() {
        String streetName = "";
        if (mAddress != null) {
            streetName = mAddress.getThoroughfare();
            if (TextUtils.isEmpty(streetName)) {
                streetName = mAddress.getSubThoroughfare();
            }
        }
        return streetName;
    }

    /**
     * 获取当前详细地址，可能会为空
     * @return
     */
    public String getDetailLocation() {
        if (mAddress != null) {
            return mAddress.getAddressLine(0);
        } else {
            return "";
        }
    }

    public double getLatitude() {
        if (mAddress != null) {
            return mAddress.getLatitude();
        } else {
            return 0;
        }
    }

    public double getLongitude() {
        if (mAddress != null) {
            return mAddress.getLongitude();
        } else {
            return 0;
        }
    }

    private List<OnLocationListener> mListeners = new ArrayList<>();
    public void addLocationListener(OnLocationListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    /**
     * 回调到UI线程
     * @param address
     */
    private void notifyLocation(final Address address) {
        if (!mListeners.isEmpty()) {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (OnLocationListener listener : mListeners) {
                        listener.onLocated(address);
                    }
                }
            });

        }
    }

    /**
     * 获取上次位置信息
     * @return
     */
    private Location getLastKnownLocation() {
        if (!checkPermission()) {
            return null;
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);// 粗略定位
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费:可以使用网络来定位
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        String provider = mLocationManager.getBestProvider(criteria, true);//从可用的位置提供器中，匹配以上标准的最佳提供器
        Location location = mLocationManager.getLastKnownLocation(provider);

        if (location == null) { // 尝试其他provider
            List<String> providers = mLocationManager.getAllProviders();
            if (providers != null && !providers.isEmpty()) {
                for (String str : providers) {
                    if (mLocationManager.isProviderEnabled(str)) {
                        location = mLocationManager.getLastKnownLocation(str);
                    }
                    if (location != null) {
                        break;
                    }
                }
            }
        }
        return location;
    }

    private String getProvider() {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            return LocationManager.NETWORK_PROVIDER;
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(Env.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(Env.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
