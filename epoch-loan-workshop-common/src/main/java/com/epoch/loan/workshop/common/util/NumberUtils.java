package com.epoch.loan.workshop.common.util;

/**
 * @Package com.longway.daow.util
 * @Description: 数字工具类
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/8/31 14:10
 */
public class NumberUtils {

    /**
     * 正则判断是否为正数
     *
     * @param str 数字
     * @return
     */
    public static boolean isPositive(String str) {
        return str.matches("[0-9]+.?[0-9]*");
    }

    /**
     * 正则判断是否为数字
     *
     * @param str 数字
     * @return
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?[0-9]+.?[0-9]*");
    }
}