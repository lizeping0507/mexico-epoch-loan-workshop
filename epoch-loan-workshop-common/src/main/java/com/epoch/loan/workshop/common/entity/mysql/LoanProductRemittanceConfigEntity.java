package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanRemittanceDistributionEntity
 * @createTime : 2021/12/18 17:49
 * @description : 放款分配配置
 */
@Data
public class LoanProductRemittanceConfigEntity {
    /**
     * 模型名称
     */
    private String groupName;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 挑选策略
     */
    private String strategyName;

}
