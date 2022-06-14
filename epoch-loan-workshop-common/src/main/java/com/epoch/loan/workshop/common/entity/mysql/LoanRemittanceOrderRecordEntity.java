package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanRemittanceOrderRecordEntity
 * @createTime : 2021/12/18 17:49
 * @description : 订单支付记录表
 */
@Data
public class LoanRemittanceOrderRecordEntity {

    /**
     * 支付Id
     */
    private String id;

    /**
     * 订单Id
     */
    private String orderId;

    /**
     * 支付渠道Id
     */
    private String paymentId;

    /**
     * 订单金额
     */
    private Double amount;

    /**
     * 收款人姓名
     */
    private String name;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 收款人账户
     */
    private String remittanceAccount;

    /**
     * 银行
     */
    private String bank;

    /**
     * rfc信息
     */
    private String rfc;

    /**
     * 身份证
     */
    private String curp;

    /**
     * 类型 0:借记卡 1:clabe账户
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 放款成功之后回调事件
     */
    private String event;

    /**
     * 进行中支付记录id
     */
    private String processRemittancePaymentRecordId;

    /**
     * 已成功支付记录Id
     */
    private String successRemittancePaymentRecordId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
