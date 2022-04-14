package com.epoch.loan.workshop.common.entity;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : LoanOrderModelEntity
 * @createTime : 2021/12/17 11:11
 * @description : 订单模型
 */
@Data
public class LoanOrderModelEntity {
    /**
     * 组名
     */
    private String groupName;

    /**
     * 顺序
     */
    private int sort;

    /**
     * 状态
     */
    private int status;

    /**
     * 描述
     */
    private String describe;
}
