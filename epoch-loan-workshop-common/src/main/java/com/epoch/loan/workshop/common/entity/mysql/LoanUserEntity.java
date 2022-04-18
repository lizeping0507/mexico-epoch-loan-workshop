package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity.mysql;
 * @className : LoanUser
 * @createTime : 2022/4/18 11:36
 * @description : 用户表
 */
@Data
public class LoanUserEntity {
    /**
     * 用户id
     */
    private String id;
    /**
     * 安卓id
     */
    private String androidId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * af推广id
     */
    private String gaId;
    /**
     * 设备ID
     */
    private String imei;
    /**
     * 手机平台(android、ios)
     */
    private String platform;
    /**
     * 登录名 手机号
     */
    private String loginName;
    /**
     * 密码
     */
    private String passowrd;
    /**
     * 所属app名称
     */
    private String appName;
    /**
     * app版本
     */
    private String appVersion;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date careteTime;
}
