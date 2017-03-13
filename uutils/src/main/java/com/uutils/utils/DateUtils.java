package com.uutils.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2015/12/17.
 */
public class DateUtils {

    public static int getDateNum(long times) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year * 10000 + month * 100 + day;
    }

    public static long getStartMillsInOneDay() {
        return getStartMillsInOneDay(Locale.getDefault());
    }

    public static long getStartMillsInOneDay(Locale locale) {
        long t = Calendar.getInstance(locale).getTimeInMillis();
        return (t - t % (24 * 3600 * 1000));
    }

    public static String getDateString(long time) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        return sFormat.format(new Date(time));
    }

    public static String getFormatTime(long time) {
        SimpleDateFormat sFormat = new SimpleDateFormat("hh:mm:ss", Locale.US);
        return sFormat.format(new Date(time));
    }

    public static String getFormatDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date date = new Date(time);
        return sdf.format(date);
    }

    public static String getFormatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    public static long utcToDate(String str) {
        return toDate(str, "EEE, dd MMM yyyy hh:mm tumblrvideo", Locale.US);
    }

    public static long toDate(String str, String format, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        Date dt = null;
        try {
            dt = sdf.parse(str);
        } catch (ParseException e) {

        }
        if (dt != null)
            return dt.getTime();
        return 0;
    }
}
