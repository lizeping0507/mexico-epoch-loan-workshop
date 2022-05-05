package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanAppControlEntity
 * @createTime : 2021/11/22 15:45
 * @description : app版本控制表
 */
@Data
public class LoanAppControlEntity {
    /**
     * app名称
     */
    private String appName;

    /**
     * 版本号
     */
    private String appVersion;

    /**
     * 状态 0:需要更新 1:正常
     */
    private Integer status;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

}
