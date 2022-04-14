package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : OrderBillStatus
 * @createTime : 2021/11/29 11:14
 * @description : 订单账单状态
 */
public class OrderBillStatus {
    /**
     * 创建
     */
    public final static int CREATE = 10;

    /**
     * 在途
     */
    public final static int WAY = 20;

    /**
     * 逾期
     */
    public final static int DUE = 30;

    /**
     * 废弃
     */
    public final static int ABANDONED = 40;

    /**
     * 结清
     */
    public final static int COMPLETE = 50;

    /**
     * 结清-有逾期
     */
    public final static int DUE_COMPLETE = 51;
}
