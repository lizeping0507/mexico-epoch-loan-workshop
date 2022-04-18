package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : OrderContractResult
 * @createTime : 2021/4/7 11:12
 * @description : 合同响应结果
 */
@Data
public class OrderContractResult implements Serializable {

    /**
     * 机构名称
     */
    private String companyName;

    /**
     * 机构地址
     */
    private String companyAddr;

    /**
     * 机构名称 + " " + 机构地址
     */
    private String companyNameAddr;

    /**
     * 机构邮箱
     */
    private String companyEmail;

    /**
     * 机构电话
     */
    private String companyPhone;

    /**
     * 机构logo链接
     */
    private String companyLogo;

    /**
     * NBFC名称
     */
    private String nbfcName;

    /**
     * NBFC地址
     */
    private String nbfcAddr;

    /**
     * NBFC名称+ " " + NBFC地址
     */
    private String nbfcNameAddr;

    /**
     * NBFC邮箱
     */
    private String nbfcEmail;

    /**
     * NBFC电话
     */
    private String nbfcPhone;

    /**
     * NBFC-logo链接
     */
    private String nbfcLogo;

    /**
     * 审批金额
     */
    private Double amount;

    /**
     * 借款期限
     */
    private Integer days;

    /**
     * 订单申请时间
     */
    private String acceptedTime;

    /**
     * 用户的 uuid
     */
    private String customerId;

    /**
     * 用户姓名
     */
    private String borrowerDetail;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 合同有效期
     */
    private String expirationTime;

    /**
     * 日利率
     */
    private Double rate;

    /**
     * 利息费
     */
    private Double interest;

    /**
     * 手续费 + 服务费
     */
    private Double fees;

    /**
     * 应还金额
     */
    private Double repayment;

    /**
     * 预付费用
     */
    private Double repaymentCharges;

    /**
     * 单日逾期费率
     */
    private Double overRate;

    /**
     * 合同编号
     */
    private String dpn;

    /**
     * 设备详情
     */
    private String deviceDetails;

    /**
     * 设备编号
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 用户注册地址
     */
    private String customerAddr;

}
