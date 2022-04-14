package com.epoch.loan.workshop.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformUserAadharDistinguishInfoEntity
 * @createTime : 2021/11/19 17:45
 * @description : 用户aadhar卡识别信息实体类 TODO 老表
 */
@Data
public class PlatformUserAadharDistinguishInfoEntity {
    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户姓名识别分数
     */
    private Double nameConf;

    /**
     * 出生日期
     */
    private String dob;

    /**
     * 生日是识别分数
     */
    private Double dobConf;

    /**
     * 性别
     */
    private String gender;

    /**
     * 性别识别分数
     */
    private Double genderConf;

    /**
     * ad卡号码
     */
    private String aadhaar;

    /**
     * ad卡识别分数
     */
    private Double aadhaarConf;

    /**
     * 父亲名称
     */
    private String father;

    /**
     * 父亲名称识别分数
     */
    private Double fatherConf;

    /**
     * 邮编
     */
    private String pin;

    /**
     * 邮编识别分数
     */
    private String pinConf;

    /**
     * ad卡上地址
     */
    private String address;

    /**
     * ad卡地址识别分数
     */
    private Double addressConf;

    /**
     * ad卡地址拆分后json
     */
    private String addressSplit;

    /**
     * ad卡地址拆分后识别分数
     */
    private Double addressSplitConf;

    /**
     * ad卡背面ad卡号
     */
    private String aadharBack;

    /**
     * ad卡背面ad卡号识别洗分数
     */
    private Double aadharBackConf;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
