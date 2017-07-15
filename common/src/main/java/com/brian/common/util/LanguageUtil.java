package com.brian.common.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * 系统语言工具
 * Created by yeguangrong on 2016/11/1.
 */

public class LanguageUtil {

    /**
     * 是否中文
     * @return
     */
    public static boolean isChinese() {
        String language = getLanguageEnv();

        if (language != null
                && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
            return true;
        else
            return false;
    }

    public static String getLanguageEnv() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }

    public static void setLangChinese(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
//        config.setLocale(Locale.SIMPLIFIED_CHINESE);
        config.locale = Locale.SIMPLIFIED_CHINESE;
        resources.updateConfiguration(config, dm);
    }

    public static void setLangEnglish(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
//        config.setLocale(Locale.ENGLISH);
        config.locale = Locale.ENGLISH;
        resources.updateConfiguration(config, dm);
    }
}
