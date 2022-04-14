package com.epoch.loan.workshop.common.entity;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : LoanMaskEntity
 * @createTime : 2021/11/22 15:45
 * @description : 身包产品配置实体类
 */
@Data
public class LoanMaskEntity {
    /**
     * App名称
     */
    private String appName;

    /**
     * 阈值
     */
    private String level;

    /**
     * 产品Id
     */
    private String productId;
}
