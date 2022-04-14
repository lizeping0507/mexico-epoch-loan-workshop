package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : PayStatus
 * @createTime : 2021/11/22 16:00
 * @description : 支付状态
 */
public class LoanRemittanceOrderRecordStatus {

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

    /**
     * 异常情况导致订单放款彻底失败 并不再继续尝试
     * 此状态手动设置
     */
    public final static int THOROUGHLY_FAILED = 60;
}
