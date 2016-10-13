/*
 * Copyright (C) 2014 Togic Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brian.testandroid.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateTimeUtil {

    public static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private static final Calendar GMT_CALENDAR = Calendar.getInstance(GMT_ZONE);

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String[] DATE_PATTERNS = {
        "EEE, dd MMM yyyy HH:mm:ss Z", // RFC 822, updated by RFC 1123
        "EEEE, dd-MMM-yy HH:mm:ss Z", // RFC 850, obsoleted by RFC 1036
        "EEE MMM d HH:mm:ss yyyy" // ANSI C's asctime() format
    };

    public static String format(long time) {
        time = time / 1000;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60);
    }

    public static String formatTimeOffset(long time) {
        time = time / 1000;
        int day = (int) (time / 86400);
        int hour = (int) ((time % 86400) / 3600);
        int min = (int) ((time % 3600) / 60);
        StringBuilder builder = new StringBuilder();
        if (day > 0) {
            builder.append(day).append("天");
            builder.append(hour).append("时");
        } else {
            if (hour > 0) {
                builder.append(hour).append("时");
            }
        }
        min = min > 0 ? min : 1;
        builder.append(min).append("分");
        return builder.toString();
    }

    public static String formatDate(long time) {
        return formatDate(time, DEFAULT_DATE_PATTERN);
    }

    public static String formatDate(long time, String pattern) {
        SimpleDateFormat formatter = null;
        if (StringUtil.isEmptyString(pattern)) {
            formatter = new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.CHINA);
        } else {
            formatter = new SimpleDateFormat(pattern, Locale.CHINA);
        }

        return formatter.format(new Date(time));
    }

    public static String getCurrentDate(String pattern) {
        SimpleDateFormat formatter = null;
        if (StringUtil.isEmptyString(pattern)) {
            formatter = new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.CHINA);
        } else {
            formatter = new SimpleDateFormat(pattern, Locale.CHINA);
        }
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    public static final String formatFileModifyDate(long time) {
        final Calendar c = GMT_CALENDAR;
        c.setTimeInMillis(time);
        return String.format(Locale.US, "%ta, %<td %<tb %<tY %<tT GMT", c);
    }

    public static final Date parseDate(String time) {
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.US);
                df.setTimeZone(GMT_ZONE);
                return df.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Date(0);
    }

    public static Date playTimeToDate(String playTime) {
        String[] temp = playTime.split(":");
        int hour = Integer.valueOf(temp[0]);
        int minute = Integer.valueOf(temp[1]);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return c.getTime();
    }

    public static String getTimeFormatString(int duration) {
        int sumSecond = duration % 60;
        int sumMinute = (duration / 60) % 60;
        int sumHour = ((duration / 60) / 60);

        String formatString = String.format(Locale.CHINA, "%02d:%02d:%02d", sumHour,
                sumMinute, sumSecond);
        return formatString;

    }

    public static int getHour(int time) {
        return (time / 60) / 60;
    }

    public static int getMinute(int time) {
        return (time / 60) % 60;
    }

    public static int getSecond(int time) {
        return time % 60;
    }

    public static String getCalendar(long timer) {
        SimpleDateFormat format = new SimpleDateFormat("M月d日", Locale.CHINA);
        return format.format(new Date(timer));
    }

    public static String getCalendarTime(long timer) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return format.format(new Date(timer));
    }

    public static String getCalendar(long timer, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
        return format.format(new Date(timer));
    }
    
    /**
     * 根据时间戳获取时间字符串
     * 
     * @param timeStamp
     * @return
     */
    public static String getDate(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm",
                Locale.CHINA);
        return sdf.format(timeStamp);
    }

    /**
     * 获取当前时间字符串
     * 
     * @return
     */
    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm",
                Locale.CHINA);
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 通过时间字符串得到时间戳
     * 
     * @param strDate
     * @return
     */
    public static String getTimeStamp(String strDate) {

        // 注意format的格式要与日期String的格式相匹配
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        try {
            Date date = df.parse(strDate);
            return String.valueOf(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
