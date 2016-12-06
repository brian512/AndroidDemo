package com.brian.common.util;

import android.support.v7.app.AppCompatDelegate;

import com.brian.common.BaseActivity;

/**
 * 夜间模式辅助类
 * Created by huamm on 2016/10/31 0031.
 */

public class DayNightHelper {

    private boolean mIsDayNightEnabled = false;

    private static class SingletonHolder {
        private static final DayNightHelper INSTANCE = new DayNightHelper();
    }
    private DayNightHelper() {
    }
    public static final DayNightHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setDayNightMode(boolean enable) {
        if (mIsDayNightEnabled == enable) {
            return;
        }
        mIsDayNightEnabled = enable;
        BaseActivity activity = BaseActivity.getTopActivity();
        activity.getDelegate().setLocalNightMode(enable ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        activity.recreate();
    }
}
