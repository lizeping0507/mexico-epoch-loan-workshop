package com.epoch.loan.workshop.common.entity;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
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

}
