package com.epoch.loan.workshop.common.mq.order.params;

import lombok.Data;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq.order.params
 * @className : OrderParams
 * @createTime : 2021/11/17 17:22
 * @description : 订单队列入列参数
 */
@Data
public class OrderParams {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单账单Id
     */
    private String orderBillId;
}
