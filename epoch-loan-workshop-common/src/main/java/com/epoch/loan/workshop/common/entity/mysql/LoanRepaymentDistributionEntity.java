package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : LoanRemittanceDistributionEntity
 * @createTime : 2021/12/18 17:49
 * @description : 还款分配实体
 */
@Data
public class LoanRepaymentDistributionEntity {
    /**
     * 模型名称
     */
    private String groupName;

    /**
     * 支付渠道id
     */
    private String paymentId;

    /**
     * 顺序
     */
    private String sort;

    /**
     * 比重
     */
    private Integer proportion;

    /**
     * 描述
     */
    private String describe;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
