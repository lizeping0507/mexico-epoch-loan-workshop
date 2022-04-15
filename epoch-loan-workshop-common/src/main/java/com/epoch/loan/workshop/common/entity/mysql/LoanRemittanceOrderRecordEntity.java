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
     * 手机号
     */
    private String phone;

    /**
     * ad卡号
     */
    private String addCard;

    /**
     * pan卡号
     */
    private String panCard;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 姓名
     */
    private String name;

    /**
     * 银行卡
     */
    private String bankCard;

    /**
     * 银行ifsc编码
     */
    private String ifsc;

    /**
     * 状态
     */
    private Integer status;

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
