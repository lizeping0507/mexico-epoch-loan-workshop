package com.epoch.loan.workshop.mq.collection.repay;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.collection;
 * @className : CollectionRepaymentPlanParam
 * @createTime : 2022/3/7 11:21
 * @description : 还款计划参数
 */
@Data
public class CollectionRepaymentPlanParam implements Serializable {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 还款计划id
     */
    private String thirdRepayPlanId;
    /**
     * 1-本金，2-罚息，3-服务费，4-利息
     */
    private Integer fundType;
    /**
     * 原始还款金额
     */
    private Double billAmount;
    /**
     * 已还金额
     */
    private Double returnedAmount;
    /**
     * 还款计划状态，10-已还款，11-部分还款，20-未还款
     */
    private Integer repaymentPlanStatus;
    /**
     * 应还款时间
     */
    private Date shouldRepayTime;
    /**
     * 还款时间
     */
    private Date repayTime;
}
