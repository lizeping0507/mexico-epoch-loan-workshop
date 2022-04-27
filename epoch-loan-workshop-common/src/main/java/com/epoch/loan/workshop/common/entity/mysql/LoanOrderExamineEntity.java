package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanOrderEntity
 * @createTime : 2021/12/16
 * @description : 订单审核
 */
@Data
public class LoanOrderExamineEntity {
    /**
     * id
     */
    private String id;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 请求参数
     */
    private String request;

    /**
     * 响应参数
     */
    private String response;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
