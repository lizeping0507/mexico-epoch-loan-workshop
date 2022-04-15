package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : PayStatus
 * @createTime : 2021/11/22 16:00
 * @description : 支付状态
 */
public class LoanRepaymentPaymentRecordStatus {

    /**
     * 创建
     */
    public final static int CREATE = 10;

    /**
     * 进行中
     */
    public final static int PROCESS = 20;

    /**
     * 异常
     */
    public final static int EXCEPTION = 30;

    /**
     * 成功
     */
    public final static int SUCCESS = 40;

    /**
     * 失败
     */
    public final static int FAILED = 50;
}
