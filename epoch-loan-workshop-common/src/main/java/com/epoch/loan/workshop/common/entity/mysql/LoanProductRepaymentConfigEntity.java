package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : LoanProductRepaymentConfigEntity
 * @createTime : 2021/12/18 17:49
 * @description : 还款分配配置
 */
@Data
public class LoanProductRepaymentConfigEntity {
    /**
     * 模型名称
     */
    private String groupName;

    /**
     * 挑选策略
     */
    private String strategyName;

}
