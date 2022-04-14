package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : OrderStatus
 * @createTime : 2021/11/22 16:00
 * @description : 订单状态
 */
public class OrderStatus {

    /**
     * 创建
     */
    public final static int CREATE = 10;

    /**
     * 审核中
     */
    public final static int EXAMINE_WAIT = 20;

    /**
     * 审核通过
     */
    public final static int EXAMINE_PASS = 30;

    /**
     * 审核拒绝
     */
    public final static int EXAMINE_FAIL = 40;

    /**
     * 等待放款
     */
    public final static int WAIT_PAY = 70;

    /**
     * 在途
     */
    public final static int WAY = 80;

    /**
     * 逾期
     */
    public final static int DUE = 90;

    /**
     * 结清
     */
    public final static int COMPLETE = 100;

    /**
     * 结清-有逾期
     */
    public final static int DUE_COMPLETE = 101;

    /**
     * 废弃订单
     */
    public final static int ABANDONED = 110;
}
