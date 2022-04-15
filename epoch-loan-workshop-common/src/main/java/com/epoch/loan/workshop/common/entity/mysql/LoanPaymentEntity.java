package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanRemittanceOrderRecordEntity
 * @createTime : 2021/12/18 17:49
 * @description : 放款支付渠道表
 */
@Data
public class LoanPaymentEntity {

    /**
     * 渠道Id
     */
    private String id;

    /**
     * 渠道名称
     */
    private String name;

    /**
     * 渠道配置
     */
    private String config;

    /**
     * 渠道排序
     */
    private Integer sort;

    /**
     * 渠道Logo
     */
    private String banner;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 模式
     */
    private String model;

    /**
     * 查询url
     */
    private String url;

    /**
     * 更新时间
     */
    private Date updateTime;
}
