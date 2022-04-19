package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanUserEntity
 * @createTime : 2022/4/18 14:24
 * @description : 用户信息
 */
@Data
public class LoanUserEntity {
    /**
     * 用户ID
     */
    private String id;

    /**
     * 安卓ID
     */
    private String androidId;

    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 推广ID
     */
    private String gaId;

    /**
     * 手机imei
     */
    private String imei;

    /**
     * 运行平台
     */
    private String platform;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * App名称
     */
    private String appName;

    /**
     * App版本
     */
    private String appVersion;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

}
