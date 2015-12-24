package com.mmga.mmgahottweet.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final long MINUTE = 60 * 1000;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;
    private static final long MONTH = 30 * DAY;
    private static final long YEAR = 365 * DAY;


    static Date date;
    static Calendar calendar = Calendar.getInstance();

    public static String parseDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeNow = System.currentTimeMillis();
        return simpleDate(timeNow - date.getTime());
//        return null;

    }

    private static String simpleDate(long time) {
        if (time < 0) {
            return " ";
        } else if (time < MINUTE) {
            return "刚刚";
        } else if (time < HOUR) {
            return "" + time / MINUTE + "分钟前";
        } else if (time < DAY) {
            return "" + time / HOUR + "小时前";
        } else if (time < WEEK) {
            return "" + time / DAY + "日前";
        } else {
            Calendar.getInstance().setTimeInMillis(time);
            return "" + Calendar.YEAR + "-" + Calendar.MONTH + "-" + Calendar.DAY_OF_MONTH;
        }

    }


}
