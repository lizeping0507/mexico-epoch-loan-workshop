package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.util.List;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : OrderDetailResult
 * @createTime : 2022/3/25 12:16
 * @description : 贷超多推接口结果封装
 */
@Data
public class MergePushLoanResult {

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 订单标识
     */
    private String orderNo;

    /**
     * 可申请额度
     */
    private Long availableCredit;

    /**
     * 总额度
     */
    private Long totalCredit;

    /**
     * 已用额度
     */
    private Long usedCredit;

    /**
     * 按钮状态
     */
    private String buttonStatus;

    /**
     * 额度锁定 0-不锁，1-锁定
     */
    private Integer locked;

    /**
     * 可申请产品数
     */
    private String canLoanNum;

    /**
     * 待还款数
     */
    private Integer repaymentNum;

    /**
     * 产品列表
     */
    private List<MergePushProductListResult> productList;

    /**
     * 按钮状态标识
     */
    private Integer buttonStatusIndentify;
}
