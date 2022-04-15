package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformUserEntity
 * @createTime : 2021/11/19 16:36
 * @description : 用户基本信息实体类 TODO 老表
 */
@Data
public class PlatformUserEntity {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String loginName;

    /**
     * 密码
     */
    private String passwd;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 注册ip地址
     */
    private String registerIp;

    /**
     * 注册客户端
     */
    private String registerClient;

    /**
     *
     */
    private String uuid;

    /**
     * 渠道表主键
     */
    private Long channelId;

    /**
     * facebook表主键
     */
    private Long fbId;

    /**
     * 注册地址
     */
    private String registerAddr;

    /**
     * 用户gps定位
     */
    private String gpsLocation;

    /**
     * 注册时androidid
     */
    private String androidId;

    /**
     * 注册时的googleplay广告id
     */
    private String gpsAdId;

    /**
     * appId
     */
    private Long appId;

    /**
     * app版本
     */
    private String appVersion;
}
