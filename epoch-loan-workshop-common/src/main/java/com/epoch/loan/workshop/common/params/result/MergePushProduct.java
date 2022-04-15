package com.epoch.loan.workshop.common.params.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : MergePushProductListDTO
 * @createTime : 2022/3/24 18:23
 * @description : 多推产品列表接口返回封装
 */
@Data
public class MergePushProduct implements Serializable {

    private static final long serialVersionUID = 116541653165465L;

    /**
     * 产品id
     */
    private Long id;

    /**
     * 商户id
     */
    private Long merchantId;

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
