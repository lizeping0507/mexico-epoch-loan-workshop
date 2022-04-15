package com.epoch.loan.workshop.common.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : OrderList
 * @createTime : 2022/3/24 18:23
 * @description : 订单列表信息类
 */
@Data
public class OrderList implements Serializable {
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 产品图片url
     */
    private String productImageUrl;
    /**
     * 产品Icon图片url
     */
    private String productIconImageUrl;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品金额
     */
    private String productAmount;
    /**
     * 下单时间
     */
    private String orderTime;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 订单状态描述
     */
    private String orderStatusRemark;
    /**
     * 到期时间
     */
    private String repaymentTime;
    /**
     * 还款金额
     */
    private String repaymentAmount;
    /**
     * 产品Id
     */
    private Long productId;
    /**
     * 按钮文案
     */
    private String orderStatusStr;
}
