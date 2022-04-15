package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformUserOcrAadharFrontLogEntity
 * @createTime : 2021/11/19 17:55
 * @description : 用户OCR识别aadhar正面日志 TODO 老表
 */
@Data
public class PlatformUserOcrAadharFrontLogEntity {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * aadhar卡持卡人名称
     */
    private String name;

    /**
     * aadhaar卡持卡人生日
     */
    private String dob;

    /**
     * aadhaar卡持人性别
     */
    private String gender;

    /**
     * aadhaar卡号
     */
    private String aadhaar;

    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 错误信息
     */
    private String errMessage;

    /**
     * 卡的种类 已验证的卡类型，包括 PAN_FRONT、AADHAAR_FRONT、AADHAAR_BACK
     */
    private String cardType;

    /**
     * aadhaar卡持人生日清晰度分
     */
    private Double dobConf;

    /**
     * aadhaar卡持人性别识别清晰度分
     */
    private Double genderConf;

    /**
     * aadhaar卡号清晰度分
     */
    private Double aadhaarConf;

    /**
     * aadhar卡持卡人名称清晰度分
     */
    private Double nameConf;

    /**
     * 唯一标识
     */
    private String tag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
