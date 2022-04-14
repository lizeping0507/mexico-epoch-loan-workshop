package com.epoch.loan.workshop.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : LoanOrderBillEntity
 * @createTime : 2021/11/25 17:28
 * @description : 订单账单实体类
 */
@Data
public class LoanOrderBillEntity {
    /**
     * 订单账单id
     */
    private String id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单期数
     */
    private Integer stages;


    /**
     * 还款金额(总)
     */
    private Double repaymentAmount;

    /**
     * 罚息
     */
    private Double punishmentAmount;

    /**
     * 利息
     */
    private Double interestAmount;

    /**
     * 附带费用
     */
    private Double incidentalAmount;

    /**
     * 还款时间
     */
    private Date repaymentTime;

    /**
     * 实际还款时间
     */
    private Date actualRepaymentTime;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
