package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity.mysql;
 * @className : LoanUserInfo
 * @createTime : 2022/4/18 11:41
 * @description : TODO
 */
@Data
public class LoanUserInfoEntity {
    /**
     * 用户详细信息id
     */
    private String id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * GPS(实时)
     */
    private String gps;
    /**
     * GPS地址(实时)
     */
    private String gpsAddress;
    /**
     * 注册GPS
     */
    private String registerGps;
    /**
     * 注册GPS地址
     */
    private String registerAddress;
    /**
     * IP(实时)
     */
    private String ip;
    /**
     * IP地址(实时)
     */
    private String ipAddress;
    /**
     * 紧急联系人信息(JSON)
     */
    private String contacts;
    /**
     * 月收入
     */
    private String monthlyIncome;
    /**
     * 发薪周期
     */
    private String payPeriod;
    /**
     * 职业
     */
    private String occupation;
    /**
     * 工资发放方式
     */
    private String payMethod;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 学历
     */
    private String education;
    /**
     * 婚姻状况
     */
    private String marital;
    /**
     * 孩子数量
     */
    private String childrenNumber;
    /**
     * 借款目的
     */
    private String loanPurpose;
    /**
     * 居住类型
     */
    private String liveType;
    /**
     * 地址
     */
    private String papersAddress;
    /**
     * 父亲名字
     */
    private String papersFatherName;
    /**
     * 全名
     */
    private String papersFullName;
    /**
     * 母亲名字
     */
    private String papersMotherName;
    /**
     * id
     */
    private String papersId;
    /**
     * 姓名
     */
    private String papersName;
    /**
     * 选民id
     */
    private String papersVoterId;
}
