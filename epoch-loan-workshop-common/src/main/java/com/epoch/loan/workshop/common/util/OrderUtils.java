package com.epoch.loan.workshop.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * * 订单编码码生成器，生成27位编码，
 * * @生成规则 2位单号类型+17位时间戳+8位(随机数)
 */
public class OrderUtils {

    /**
     * 订单类别头
     */
    private static final String ORDER_CODE = "";

    /**
     * 充值类别头
     */
    private static final String DEPOSIT_ORDER = "CZ";

    /**
     * 提现类别头
     */
    private static final String WITHDRAW_ORDER = "TX";

    /**
     * 随机数总长度
     */
    private static final int MAX_LENGTH = 8;

    /**
     * 生成时间戳
     */
    private static String getDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    /**
     * 生成固定长度随机码
     *
     * @param n 长度
     */

    private static long getRandom(long n) {
        long min = 1, max = 9;
        for (int i = 1; i < n; i++) {
            min *= 10;
            max *= 10;
        }
        long rangeLong = (((long) (new Random().nextDouble() * (max - min)))) + min;
        return rangeLong;
    }

    /**
     * 生成不带类别标头的编码
     *
     * @param userId
     */
    private static synchronized String getCode() {
        return getDateTime() + getRandom(MAX_LENGTH);
    }


    /**
     * 生成订单单号编码(调用方法)
     */

    public static String getOrderCode() {
        return ORDER_CODE + getCode();
    }


    /**
     * 生成充值单号编码（调用方法）
     */
    public static String getDepositCode() {
        return DEPOSIT_ORDER + getCode();
    }


    /**
     * 生成提现单号编码(调用方法)
     */
    public static String getWithdrawCode() {
        return WITHDRAW_ORDER + getCode();
    }
}