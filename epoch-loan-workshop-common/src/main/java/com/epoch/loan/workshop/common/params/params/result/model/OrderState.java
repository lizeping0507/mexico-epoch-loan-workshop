package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : OrderState
 * @createTime : 2022/3/24 18:23
 * @description : 订单状态信息封装
 */
@Data
public class OrderState implements Serializable {

    /**
     * 时间
     */
    private String createTime;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 状态说明
     */
    private String orderStatusRemark;
}
