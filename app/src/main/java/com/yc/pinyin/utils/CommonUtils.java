package com.yc.pinyin.utils;

import com.yc.pinyin.App;
import com.yc.pinyin.domain.TimeConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by admin on 2018/1/18.
 */

public class CommonUtils {

    public static void initTrial(String time) {
        if (!StringUtils.isEmpty(time)) {
            long spaceSec = getTimeSpanByNow(time, TimeConstants.SEC);
            if (spaceSec < 24 * 60 * 60) {
                App.isTrial = true;
            } else {
                App.isTrial = false;
            }
        }
    }


    private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    /**
     * 获取当前时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @return 时间字符串
     */
    public static String getNowString() {
        return millis2String(System.currentTimeMillis(), DEFAULT_FORMAT);
    }

    /**
     * 将时间戳转为时间字符串
     * <p>格式为format</p>
     *
     * @param millis 毫秒时间戳
     * @param format 时间格式
     * @return 时间字符串
     */
    public static String millis2String(final long millis, final DateFormat format) {
        return format.format(new Date(millis));
    }

    /**
     * 获取与当前时间的差（单位：unit）
     * <p>time格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @param unit 单位类型
     *             <ul>
     *             <li>{@link TimeConstants#MSEC}: 毫秒</li>
     *             <li>{@link TimeConstants#SEC }: 秒</li>
     *             <li>{@link TimeConstants#MIN }: 分</li>
     *             <li>{@link TimeConstants#HOUR}: 小时</li>
     *             <li>{@link TimeConstants#DAY }: 天</li>
     *             </ul>
     * @return unit时间戳
     */
    public static long getTimeSpanByNow(final String time, @TimeConstants.Unit final int unit) {
        return getTimeSpan(getNowString(), time, DEFAULT_FORMAT, unit);
    }




    /**
     * 获取两个时间差（单位：unit）
     * <p>time0和time1格式都为format</p>
     *
     * @param time0  时间字符串0
     * @param time1  时间字符串1
     * @param format 时间格式
     * @param unit   单位类型
     *               <ul>
     *               <li>{@link TimeConstants#MSEC}: 毫秒</li>
     *               <li>{@link TimeConstants#SEC }: 秒</li>
     *               <li>{@link TimeConstants#MIN }: 分</li>
     *               <li>{@link TimeConstants#HOUR}: 小时</li>
     *               <li>{@link TimeConstants#DAY }: 天</li>
     *               </ul>
     * @return unit时间戳
     */
    public static long getTimeSpan(final String time0, final String time1, final DateFormat format, @TimeConstants.Unit final int unit) {
        return millis2TimeSpan(Math.abs(string2Millis(time0, format) - string2Millis(time1, format)), unit);
    }


    /**
     * 将时间字符串转为时间戳
     * <p>time格式为format</p>
     *
     * @param time   时间字符串
     * @param format 时间格式
     * @return 毫秒时间戳
     */
    public static long string2Millis(final String time, final DateFormat format) {
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static long millis2TimeSpan(final long millis, @TimeConstants.Unit final int unit) {
        return millis / unit;
    }

    /**
     * 将Date类型转为时间戳
     *
     * @param date Date类型时间
     * @return 毫秒时间戳
     */
    public static long date2Millis(final Date date) {
        return date.getTime();
    }
}
