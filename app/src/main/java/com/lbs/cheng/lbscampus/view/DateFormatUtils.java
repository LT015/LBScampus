package com.lbs.cheng.lbscampus.view;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateFormatUtils {

    private static final String DATE_FORMAT_PATTERN_YMD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_PATTERN_YMD_HM = "yyyy-MM-dd HH:mm";

    /**
     * 时间戳转字符串
     *
     * @param timestamp     时间戳
     * @param isPreciseTime 是否包含时分
     * @return 格式化的日期字符串
     */
    public static String long2Str(long timestamp, boolean isPreciseTime) {
        return long2Str(timestamp, getFormatPattern(isPreciseTime));
    }

    private static String long2Str(long timestamp, String pattern) {
        return new SimpleDateFormat("HH:mm").format(new Date(timestamp));
    }

    public static String long2String(long timestamp){
        return new SimpleDateFormat("HH:mm:ss").format(new Date(timestamp));
    }

    /**
     * 字符串转时间戳
     *
     * @param dateStr       日期字符串
     * @param isPreciseTime 是否包含时分
     * @return 时间戳
     */
    public static long str2Long(String dateStr, boolean isPreciseTime) {
        return str2Long(dateStr, getFormatPattern(isPreciseTime));
    }

    /**
     * Time转str
     *
     * @param date
     * @param isPreciseTime 是否包含时分
     * @return 时间戳
     */
    public static String time2Str(Time date, boolean isPreciseTime) {
        return new SimpleDateFormat("HH:mm").format(date);
    }

    public static Time str2Time(String date) {
        Date d = null;
        try {
            d = new SimpleDateFormat("HH:mm").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Time time = new Time(d.getTime());

        return time;
    }

    /*
    **  把HH:mm:ss 转为HH:mm
     */
    public static String getRightText(String time){
        Date d = null;
        try {
            d = new SimpleDateFormat("HH:mm").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("HH:mm").format(new Time(d.getTime()));

    }



    private static long str2Long(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.CHINA).parse(dateStr).getTime();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static String getFormatPattern(boolean showSpecificTime) {
        if (showSpecificTime) {
            return DATE_FORMAT_PATTERN_YMD_HM;
        } else {
            return DATE_FORMAT_PATTERN_YMD;
        }
    }

}
