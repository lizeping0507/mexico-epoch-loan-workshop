package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : OrderExamineStatus
 * @createTime : 2021/11/18 17:26
 * @description : 订单模型审核记录状态
 */
public class OrderExamineStatus {

    /**
     * 创建
     */
    public final static int CREATE = 10;

    /**
     * 等待
     */
    public final static int WAIT = 20;

    /**
     * 等待处理—超时(定时调度)
     */
    public final static int WAIT_TIME_OUT = 21;

    /**
     * 错误-重试
     */
    public final static int FAIL = 30;

    /**
     * 通过
     */
    public final static int PASS = 40;

    /**
     * 拒绝
     */
    public final static int REFUSE = 50;

}
