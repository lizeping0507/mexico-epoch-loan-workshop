package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity.mysql;
 * @className : PlatformUserAuthEntity
 * @createTime : 2022/4/24 14:12
 * @description : 用户认证信息
 */
@Data
public class PlatformUserAuthEntity {
    /**
     * 主键
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 基本信息认证状态，10未认证/未完善，20认证中/完善中，30已认证/已完善
     */
    private String baseInfoState;
    /**
     * 手机运营商认证状态 ，10未认证/未完善，20认证中/完善中，30已认证/已完善
     */
    private String phoneState;
    /**
     * 身份证信息认证状态，10未认证/未完善，20认证中/完善中，30已认证/已完善
     */
    private String idCardState;
    /**
     * 其他信息（补充信息）认证状态，10未认证/未完善，20认证中/完善中，30已认证/已完善
     */
    private String otherInfoState;
}
