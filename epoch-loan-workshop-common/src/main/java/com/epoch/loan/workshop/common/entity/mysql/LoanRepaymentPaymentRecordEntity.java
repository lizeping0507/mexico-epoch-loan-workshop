package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : LoanRepaymentPaymentRecordEntity
 * @createTime : 2021/12/18 17:49
 * @description : 还款订单支付记录详情记录表
 */
@Data
public class LoanRepaymentPaymentRecordEntity {

    /**
     * id
     */
    private String id;

    /**
     * id
     */
    private String orderId;

    /**
     * 订单账单Id
     */
    private String orderBillId;

    /**
     * 渠道Id
     */
    private String paymentId;

    /**
     * 业务Id
     */
    private String businessId;

    /**
     * 发起支付金额
     */
    private Double amount;

    /**
     * 实际支付金额
     */
    private Double actualAmount;

    /**
     * 支付渠道手续费
     */
    private Double payFee;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 银行卡姓名
     */
    private String name;

    /**
     * 支付事件
     */
    private String event;

    /**
     * 请求数据
     */
    private String request;

    /**
     * 响应数据
     */
    private String response;

    /**
     * 查询请求数据
     */
    private String searchRequest;

    /**
     * 查询响应数据
     */
    private String searchResponse;

    /**
     * 支付状态
     */
    private Integer status;

    /**
     * 支付方式 0:线下 1.线上
     */
    private Integer type;

    /**
     * clabe
     */
    private String clabe;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
