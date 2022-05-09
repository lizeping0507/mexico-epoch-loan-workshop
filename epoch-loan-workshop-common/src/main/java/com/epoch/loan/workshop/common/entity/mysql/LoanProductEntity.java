package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanProductEntity
 * @createTime : 2022/2/11 11:28
 * @description : 产品实体类
 */
@Data
public class LoanProductEntity {
    /**
     * 产品id
     */
    private String id;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品图片url
     */
    private String productImgUrl;

    /**
     * 手续费
     */
    private Double processingFeeProportion;

    /**
     * 订单放款策略组
     */
    private String remittanceDistributionGroup;

    /**
     * 订单审核策略组
     */
    private String orderModelGroup;

    /**
     * 利息
     */
    private Double interest;

    /**
     * 罚息(日)
     */
    private Double penaltyInterest;

    /**
     * 订单期数
     */
    private Integer stages;

    /**
     * 每期天数
     */
    private Integer stagesDay;

    /**
     * 机构名称
     */
    private String companyName;

    /**
     * 机构地址
     */
    private String companyAddr;

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
     * 放款到账范围
     */
    private String arrivalRange;

    /**
     * 利息范围
     */
    private String interestRange;

    /**
     * 还款范围
     */
    private String repaymentRange;

    /**
     * 手续费范围
     */
    private String serviceFeeRange;

    /**
     * 手续费范围
     */
    private String amountRange;

    /**
     * 产品Icon图片url
     */
    private String icon;

    /**
     * 是否开量
     */
    private Integer isOpen;

    /**
     * 冷却天数
     */
    private Integer cdDays;

    /**
     * 冷却天数
     */
    private Integer passRate;

    /**
     * 状态 1有效
     */
    private Integer status;

    /**
     * 0--不推送 1--只推送提还系统    2-- 只推送催收系统  3--推送提还和催收
     */
    private Integer reactType;

    /**
     * 与催收、提还交互的 用于生成验证码的 key
     */
    private String productKey;
}
