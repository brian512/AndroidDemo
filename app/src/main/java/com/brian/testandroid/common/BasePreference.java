package com.brian.testandroid.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * 封装了preference基本的操作
 * Created by Brian on 2016/9/10 0010.
 */
public class BasePreference {

    private SharedPreferences mPreference;
    private static BasePreference sInstance = new BasePreference();

    public static BasePreference initPreference(String name) {
        if (TextUtils.isEmpty(name)) {
            sInstance.mPreference = PreferenceManager.getDefaultSharedPreferences(Env.getContext());
        } else {
            sInstance.mPreference = Env.getContext().getSharedPreferences(name, MODE_PRIVATE);
        }
        return sInstance;
    }

    /**
     * 读取对应的键值
     */
    public String getString(String key, String defaultValue) {
        return mPreference.getString(key, defaultValue);
    }

    /**
     * 读取对应的键值
     */
    public int getInt(String key, int defaultValue) {
        return mPreference.getInt(key, defaultValue);
    }

    /**
     * 读取对应的键值
     */
    public long getLong(String key, long defaultValue) {
        return mPreference.getLong(key, defaultValue);
    }

    /**
     * 将键值对写入配置文件
     */
    public boolean getBoolean(String key, boolean value) {
        return mPreference.getBoolean(key, value);
    }

    /**
     * 将键值对写入配置文件
     */
    public void putString(String key, String value) {
        // 编辑SharedPreferences对象
        SharedPreferences.Editor editor = mPreference.edit();
        // 插入一个数据
        editor.putString(key, value);
        // 提交数据
        editor.apply();
    }

    /**
     * 将键值对写入配置文件
     */
    public void putInt(String key, int value) {
        // 编辑SharedPreferences对象
        SharedPreferences.Editor editor = mPreference.edit();
        // 插入一个数据
        editor.putInt(key, value);
        // 提交数据
        editor.apply();
    }

    /**
     * 将键值对写入配置文件
     */
    public void putLong(String key, long value) {
        // 编辑SharedPreferences对象
        SharedPreferences.Editor editor = mPreference.edit();
        // 插入一个数据
        editor.putLong(key, value);
        // 提交数据
        editor.apply();
    }

    /**
     * 将键值对写入配置文件
     */
    public void putBoolean(String key, boolean value) {
        // 编辑SharedPreferences对象
        SharedPreferences.Editor editor = mPreference.edit();
        // 插入一个数据
        editor.putBoolean(key, value);
        // 提交数据
        editor.apply();
    }

}
