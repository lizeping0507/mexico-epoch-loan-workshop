package com.epoch.loan.workshop.common.mq.repayment.params;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq.repayment.params
 * @className : DistributionRepaymentParams
 * @createTime : 2022/4/8 12:24
 * @description : 订单还款分配队列
 */
@Data
public class DistributionRepaymentParams {
    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单账单Id
     */
    private String orderBillId;
}
