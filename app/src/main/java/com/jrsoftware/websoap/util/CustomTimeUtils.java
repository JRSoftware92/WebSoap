package com.jrsoftware.websoap.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.os.SystemClock;

/**
 * Created by jriley on 1/2/16.
 *
 * Utility Class for common time methods
 */
public final class CustomTimeUtils {

    private static final String UTC = "UTC";

    public static Date getDate(int typeCode, int value){
        Calendar calendar = Calendar.getInstance();
        calendar.add(typeCode, value);

        return calendar.getTime();
    }

    public static long getDateMillis(int typeCode, int value){
        Calendar calendar = Calendar.getInstance();
        calendar.add(typeCode, value);

        return calendar.getTimeInMillis();
    }

    public static long today(){ return daysFromToday(0); }

    public static long hoursFromNow(int numHours){
        return getDateMillis(Calendar.HOUR, numHours);
    }

    public static long daysFromToday(int numDays){
        return getDateMillis(Calendar.DATE, numDays);
    }

    public static long weeksFromToday(int numWeeks){
        return getDateMillis(Calendar.WEEK_OF_YEAR, numWeeks);
    }

    public static long monthsFromToday(int numMonths){
        return getDateMillis(Calendar.MONTH, numMonths);
    }

    public static long yearsFromToday(int numYears){
        return getDateMillis(Calendar.YEAR, numYears);
    }

    /**
     *
     * @param millis - Long time in milliseconds
     * @return - Provided time converted to seconds
     */
    public static long millisToSec(long millis){
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    /**
     *
     * @param millis - Long time in milliseconds
     * @return - Provided time converted to minutes
     */
    public static long millisToMin(long millis){
        return TimeUnit.MILLISECONDS.toMinutes(millis);
    }

    /**
     *
     * @param sec - Long time in seconds
     * @return - Provided time converted to milliseconds
     */
    public static long secToMillis(long sec){
        return TimeUnit.SECONDS.toMillis(sec);
    }

    /**
     *
     * @param min - Long time in minutes
     * @return - Provided time converted to milliseconds
     */
    public static long minToMillis(long min){
        return TimeUnit.MINUTES.toMillis(min);
    }

    /**
     *
     * @param min - Long time in minutes
     * @return - Provided time converted to seconds
     */
    public static long minToSecs(long min){
        return TimeUnit.MINUTES.toSeconds(min);
    }

    /**
     *
     * @param sec - Long time in seconds
     * @return - Provided time converted to minutes
     */
    public static long secToMin(long sec){
        return TimeUnit.SECONDS.toMinutes(sec);
    }

    /**
     * Converts a long millisecond value to a 00:00 timestamp
     * @param value - Long time value in milliseconds
     * @return - String timestamp
     */
    public static String asTime(long value){
        long sec = millisToSec(value);
        long min = sec / 60;
        long remainder = sec % 60;

        return String.format("%02d:%02d", min, remainder);
    }

    /**
     * Converts a 00:00 timestamp to a long millisecond value
     * @param timeStamp - String time value in 00:00 format
     * @return - long millsecond vlaue
     */
    public static long fromTimeStamp(String timeStamp){
        String[] arr = timeStamp.split(":");
        if(arr.length < 2)
            return 0;

        long min = CustomMathUtils.safeLong(arr[0]);
        long sec = CustomMathUtils.safeLong(arr[1]);

        sec += (min * 60);

        return secToMillis(sec);
    }

    public static long fromDateStamp(String dateStamp) throws ParseException {
        if(dateStamp == null || dateStamp.length() < 1)
            return -1;

        SimpleDateFormat format = getDateFormat();
        Date date = format.parse(dateStamp);

        return date.getTime();
    }


    public static long fromUTCDate(String timeStamp) throws ParseException {
        if(timeStamp == null || timeStamp.length() < 1)
            return -1;

        SimpleDateFormat format = getUTCFormat();
        Date date = format.parse(timeStamp);

        return date.getTime();
    }

    public static long fromShortUTCDate(String timeStamp) throws ParseException {
        if(timeStamp == null || timeStamp.length() < 1)
            return -1;

        SimpleDateFormat format = getShortUTCFormat();
        Date date = format.parse(timeStamp);

        return date.getTime();
    }

    public static String asTimeStamp(long value){
        SimpleDateFormat format = getTimeFormat();
        Date date = new Date(value);

        return format.format(date);
    }

    public static String asDateStamp(long value) {
        SimpleDateFormat format = getDateFormat();
        Date date = new Date(value);

        return format.format(date);
    }

    /**
     * Converts a long millisecond value to a 00:00 timestamp
     * @param value - Long time value in milliseconds
     * @return - String timestamp
     */
    public static String asUTCDate(long value){
        if(value < 0)
            return "";

        SimpleDateFormat format = getUTCFormat();
        Date date = new Date(value);

        return format.format(date);
    }

    public static String asShortUTCDate(long value){
        if(value < 0)
            return "";

        SimpleDateFormat format = getShortUTCFormat();
        Date date = new Date(value);

        return format.format(date);
    }

    private static SimpleDateFormat getTimeFormat(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone(UTC));

        return format;
    }

    private static SimpleDateFormat getDateFormat(){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone(UTC));

        return format;
    }

    private static SimpleDateFormat getUTCFormat(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone(UTC));

        return format;
    }

    private static SimpleDateFormat getShortUTCFormat(){
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone(UTC));

        return format;
    }


}

