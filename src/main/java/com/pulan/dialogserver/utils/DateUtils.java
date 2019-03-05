package com.pulan.dialogserver.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public final static String DATEFORMAT_1 = "yyyy-MM";
    public final static String DATEFORMAT_2 = "yyyy-MM-dd";
    public final static String DATEFORMAT_3 = "yyyy-MM-dd HH:mm";
    public final static String DATEFORMAT_4 = "HH:mm:ss";
    public final static String DATEFORMAT_5 = "MM-dd HH:mm";

    /**
     * 将时间字符串按指定格式转换成时间类型
     *
     * @param date   时间字符串
     * @param format 指定转换格式
     * @return 返回转换结果
     * @throws Exception
     */
    public static Date getDate(String date, String format) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date parse = simpleDateFormat.parse(date);
        return parse;
    }

    /**
     * 当前时间格式化输出
     *
     * @return 返回长时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static Date getCurrentDate() {
        return new java.sql.Date(new Date().getTime());
    }

    /**
     * 将时间按指定格式转换成字符串
     *
     * @param date   时间
     * @param format 指定转换格式
     * @return 返回转换结果
     * @throws Exception
     */
    public static String getStringForDate(Date date, String format) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String dateFormat = simpleDateFormat.format(date);
        return dateFormat;
    }

    /**
     * 将时间字符串转成指定格式的字符串
     *
     * @param date      时间字符串
     * @param oldFormat 传入的时间字符串的格式
     * @param newFormat 需要转换成的字符串格式
     * @return 返回转换结果
     * @throws Exception
     */
    public static String getStringForDateString(String date, String oldFormat, String newFormat) throws Exception {
        if (null == oldFormat) {
            oldFormat = DATEFORMAT_1;
        }
        Date date1 = getDate(date, oldFormat);
        String stringForDate = getStringForDate(date1, newFormat);
        return stringForDate;
    }


}
