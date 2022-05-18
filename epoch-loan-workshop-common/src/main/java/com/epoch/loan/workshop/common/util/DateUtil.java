package com.epoch.loan.workshop.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Package com.longway.daow.util
 * @Description: 日期操作工具类
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/8/21 15:42
 */
public class DateUtil {
    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>();

    private static final Object OBJECT = new Object();

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(Date date) {
        long ts = date.getTime();
        return String.valueOf(ts);
    }

    /*
     * 将时间戳转换为时间
     */
    public static Date stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        return new Date(lt);
    }

    /**
     * 获取SimpleDateFormat
     *
     * @param pattern 日期格式
     * @return
     * @throws RuntimeException
     */
    private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
        SimpleDateFormat dateFormat = THREAD_LOCAL.get();
        if (dateFormat == null) {
            synchronized (OBJECT) {
                if (dateFormat == null) {
                    dateFormat = new SimpleDateFormat(pattern);
                    dateFormat.setLenient(false);
                    THREAD_LOCAL.set(dateFormat);
                }
            }
        }
        dateFormat.applyPattern(pattern);
        return dateFormat;
    }


    /**
     * 获取两个日期相差的分钟
     *
     * @param date      前者
     * @param otherDate 后者
     * @return
     */
    public static int getIntervalMinute(Date date, Date otherDate) {
        int diff = (int) (date.getTime() - otherDate.getTime()) / 1000 / 60;
        return diff;
    }


    /**
     * 获取两个日期相差的天数
     *
     * @param date      前者
     * @param otherDate 后者
     * @param pattern   日期格式
     * @return
     */
    public static int getIntervalDays(String date, String otherDate, String pattern) {
        return getIntervalDays(StringToDate(date, pattern), StringToDate(otherDate, pattern));
    }

    /**
     * 获取两个时间的相差天数
     *
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差天数。如果失败则返回-1
     */
    public static int getIntervalDays(Date date, Date otherDate) {
        int num = -1;
        if (date != null && otherDate != null) {
            long time = Math.abs(date.getTime() - otherDate.getTime());
            num = (int) (time / (24 * 60 * 60 * 1000));
        }
        return num;
    }

    /**
     * 获取两个时间的相差年数
     *
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差年数。如果失败则返回-1
     */
    public static int getIntervalYears(Date date, Date otherDate) {
        int num = -1;
        if (date != null && otherDate != null) {
            Calendar calStartTime = Calendar.getInstance();
            Calendar calEndTime = Calendar.getInstance();
            calStartTime .setTime(date);
            calEndTime .setTime(otherDate);
            num = calEndTime.get(Calendar.YEAR) - calStartTime.get(Calendar.YEAR);
        }
        return num;
    }

    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date     日期
     * @param dateType 日期格式
     * @return 数值
     */
    private static int getInteger(Date date, int dateType) {
        int num = 0;
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            num = calendar.get(dateType);
        }
        return num;
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date     日期
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期
     */
    private static Date addInteger(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, amount);
            myDate = calendar.getTime();
        }
        return myDate;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date    日期字符串
     * @param pattern 日期格式
     * @return 日期
     */
    public static Date StringToDate(String date, String pattern) {
        Date myDate = null;
        if (date != null) {
            try {
                myDate = getDateFormat(pattern).parse(date);
            } catch (Exception e) {
            }
        }
        return myDate;
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String DateToString(Date date, String pattern) {
        String dateString = null;
        if (date != null) {
            try {
                dateString = getDateFormat(pattern).format(date);
            } catch (Exception e) {
            }
        }
        return dateString;
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date        旧日期字符串
     * @param olddPattern 旧日期格式
     * @param newPattern  新日期格式
     * @return 新日期字符串
     */
    public static String StringToString(String date, String olddPattern, String newPattern) {
        return DateToString(StringToDate(date, olddPattern), newPattern);
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加年份后的日期
     */
    public static Date addYear(Date date, int yearAmount) {
        return addInteger(date, Calendar.YEAR, yearAmount);
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date        日期
     * @param monthAmount 增加数量。可为负数
     * @return 增加月份后的日期
     */
    public static Date addMonth(Date date, int monthAmount) {
        return addInteger(date, Calendar.MONTH, monthAmount);
    }

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date      日期
     * @param dayAmount 增加数量。可为负数
     * @return 增加天数后的日期
     */
    public static Date addDay(Date date, int dayAmount) {
        return addInteger(date, Calendar.DATE, dayAmount);
    }

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date       日期
     * @param hourAmount 增加数量。可为负数
     * @return 增加小时后的日期
     */
    public static Date addHour(Date date, int hourAmount) {
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
    }

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date         日期
     * @param minuteAmount 增加数量。可为负数
     * @return 增加分钟后的日期
     */
    public static Date addMinute(Date date, int minuteAmount) {
        return addInteger(date, Calendar.MINUTE, minuteAmount);
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date         日期
     * @param secondAmount 增加数量。可为负数
     * @return 增加秒钟后的日期
     */
    public static Date addSecond(Date date, int secondAmount) {
        return addInteger(date, Calendar.SECOND, secondAmount);
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date) {
        return getInteger(date, Calendar.YEAR);
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期
     * @return 月份
     */
    public static int getMonth(Date date) {
        return getInteger(date, Calendar.MONTH) + 1;
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期
     * @return 天
     */
    public static int getDay(Date date) {
        return getInteger(date, Calendar.DATE);
    }

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date 日期
     * @return 小时
     */
    public static int getHour(Date date) {
        return getInteger(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date 日期
     * @return 分钟
     */
    public static int getMinute(Date date) {
        return getInteger(date, Calendar.MINUTE);
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date 日期
     * @return 秒钟
     */
    public static int getSecond(Date date) {
        return getInteger(date, Calendar.SECOND);
    }

    /**
     * 返回当前的 yyyy-MM-dd
     *
     * @return
     */
    public static String getDefault() {
        return getDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 返回当前的 yyyyMMddHHmmssSSS
     *
     * @return
     */
    public static String getDefault2() {
        return getDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    /**
     * 返回当前的 yyyyMMdd
     *
     * @return
     */
    public static String getDefault3() {
        return getDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 返回当前的 yyyyMMddHH
     *
     * @return
     */
    public static String getDefault4() {
        return getDateFormat("yyyyMMddHH").format(new Date());
    }

    /**
     * 返回当前的 yyyyMMddHHmm
     *
     * @return
     */
    public static String getDefault5() {
        return getDateFormat("yyyyMMddHHmm").format(new Date());
    }

    /**
     * 返回当前的 yyyyMM
     *
     * @return
     */
    public static String getDefault6() {
        return getDateFormat("yyyyMM").format(new Date());
    }

    /**
     * 返回当前的 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDefault7() {
        return getDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 返回当前的 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDefault8() {
        return getDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }


    /**
     * 返回 yyyyMMdd格式
     *
     * @return
     */
    public static String getString(Date date) {
        return getDateFormat("yyyyMMdd HH:mm:ss").format(date);
    }

    /**
     * 返回前一天的 yyyyMMdd格式
     *
     * @param date
     * @return
     */
    public static String getYesterdayString(Date date) {
        date = addDay(date, -1);
        return getString(date);
    }

    /**
     * 获取当前日期是星期几数字<br>
     *
     * @param dt
     * @return 当前日期是星期几数字
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取上个月第一天yyyymmdd
     *
     * @return
     */
    public static String getLastMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getString(calendar.getTime());
    }

    /**
     * 获取上个月最后一天yyyymmdd
     *
     * @return
     */
    public static String getLastMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return getString(calendar.getTime());
    }

    /**
     * 获取当日零点时间
     *
     * @return Date
     */
    public static Date getStartForDay() {
        return getStartForDay(new Date());
    }

    /**
     * 获取指定时间 当日零点时间
     *
     * @return Date
     */
    public static Date getStartForDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
