package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result.model;
 * @className : ProductData
 * @createTime : 2022/3/24 18:23
 * @description : 订单信息封装
 */
@Data
public class ProductData implements Serializable {

    /**
     * 产品Id
     */
    private Long id;

    /**
     * 商户Id
     */
    private Long merchantId;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 申请金额
     */
    private String approvalAmount;

    /**
     * 申请期限
     */
    private String approvalTerm;

    /**
     * 还款金额
     */
    private String payAmount;

    /**
     * 实际到账
     */
    private String receiveAmount;

    /**
     * 费率
     * 日费率0.098%
     */
    private String rate;

    /**
     * 放款时间
     * 1小时放款
     */
    private String loanTime;

    /**
     * 申请条件
     */
    private List<String> appConditions;

    /**
     * 贷款流程
     */
    private List<ProductProcess> loanProcess;

    /**
     * 还款方式
     */
    private String repaymentWay;

    /**
     * 提前还款
     */
    private String advRepayment;

    /**
     * 逾期政策
     */
    private String overdueDesc;

    /**
     * 过程费
     */
    private String processingFee;
}
