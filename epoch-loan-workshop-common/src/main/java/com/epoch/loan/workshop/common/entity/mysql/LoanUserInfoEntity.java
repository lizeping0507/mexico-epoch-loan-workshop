package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanUserInfoEntity
 * @createTime : 2022/4/18 14:24
 * @description : 用户详细信息
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
     * 手机号
     */
    private String mobile;

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
     * INE/IFE地址
     */
    private String papersAddress;

    /**
     * INE/IFE州
     */
    private String papersState;

    /**
     * INE/IFE父亲姓氏
     */
    private String papersFatherName;

    /**
     * INE/IFE姓名
     */
    private String papersName;

    /**
     * INE/IFE母亲姓氏
     */
    private String papersMotherName;

    /**
     * INE/IFE全名
     */
    private String papersFullName;

    /**
     * INE/IFE证件id
     */
    private String papersId;

    /**
     * INE/IFE选民id
     */
    private String papersVoterId;

    /**
     * INE/IFE 性别
     */
    private String papersGender;

    /**
     * INE/IFE 年龄
     */
    private Integer papersAge;

    /**
     * INE/IFE 出生日期
     */
    private String papersDateOfBirth;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 税号
     */
    private String rfc;

    /**
     * 身份证号
     */
    private String curp;

    /**
     * 用户填写 父亲姓氏
     */
    private String customFatherName;

    /**
     * 用户填写 姓名
     */
    private String customName;

    /**
     * 用户填写 母亲名字
     */
    private String customMotherName;

    /**
     * 用户填写 全名
     */
    private String customFullName;

    /**
     * 用户填写 生日
     */
    private String customDateOfBirth;

    /**
     * 用户填写 年龄
     */
    private String customAge;

    /**
     * 用户填写 性别
     */
    private String customGenter;

    /**
     * 上传证件的正面图片路径
     */
    private String frontPath;

    /**
     * 上传证件的背面图片路径
     */
    private String backPath;

    /**
     * 上传人脸的图片路径
     */
    private String facePath;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
