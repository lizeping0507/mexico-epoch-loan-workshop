package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanSMSChannelConfigEntity
 * @createTime : 2022/4/20 14:35
 * @description : 短信渠道
 */
@Data
public class LoanSMSChannelConfigEntity {
    /**
     * 渠道名称
     */
    private String name;

    /**
     * 渠道权重
     */
    private Integer proportion;

    /**
     * 渠道状态
     */
    private Integer status;

    /**
     * 渠道配置
     */
    private String config;

    /**
     * 反射类
     */
    private String reflex;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;


}
