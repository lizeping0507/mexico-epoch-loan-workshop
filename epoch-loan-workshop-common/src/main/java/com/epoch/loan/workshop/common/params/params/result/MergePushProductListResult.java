package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : MergePushProductListResult
 * @createTime : 2022/3/25 12:16
 * @description : 贷超多推产品接口结果封装
 */
@Data
public class MergePushProductListResult {

    /**
     * 产品id
     */
    private Long id;

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 审批额度
     */
    private Integer approvalAmount;

    /**
     * 还款额度
     */
    private Integer repaymentAmount;

    /**
     * 利息
     */
    private Integer interest;

    /**
     * 是否复贷标识
     */
    private Boolean reloanType;

    /**
     * 申请额度标识 1-标识第一个额度对应产品，2-标识第二个额度对应产品
     */
    private Integer creditType;

    /**
     * 产品icon图标
     */
    private String icon;

    /**
     * 利率
     */
    private String rate;

    /**
     * 利率-纯小数点展示
     */
    private BigDecimal rateN;

    /**
     * 订单号 当前产品对应的上一笔订单号
     */
    private String orderNo;

    /**
     * 订单号 当前产品对应的上一笔订单号的状态
     */
    private String orderStatus;
}
