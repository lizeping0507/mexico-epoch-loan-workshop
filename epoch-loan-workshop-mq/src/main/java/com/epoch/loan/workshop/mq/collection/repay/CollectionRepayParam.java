package com.epoch.loan.workshop.mq.collection.repay;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.collection;
 * @className : CollectionRepayParam
 * @createTime : 2022/3/7 10:52
 * @description : 还款参数
 */
@Data
public class CollectionRepayParam implements Serializable {
    /**
     * 订单信息
     */
    private CollectionOrderInfoParam order;

    /**
     * 还款计划
     */
    private List<CollectionRepaymentPlanParam> repaymentPlan;

    /**
     * 还款记录
     */
    private List<CollectionRepayRecordParam> repayRecord;
}
