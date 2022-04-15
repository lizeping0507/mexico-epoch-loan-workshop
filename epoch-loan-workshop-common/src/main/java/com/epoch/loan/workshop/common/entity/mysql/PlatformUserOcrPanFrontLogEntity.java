package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformUserOcrPanFrontLogEntity
 * @createTime : 2021/11/19 18:24
 * @description : 用户ocr识别pan卡日志实体类 TODO 老表
 */
@Data
public class PlatformUserOcrPanFrontLogEntity {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * pan卡号
     */
    private String panNo;

    /**
     * 父亲名称
     */
    private String father;

    /**
     * Pan卡签发时间
     */
    private String dateOfIssue;

    /**
     * 出生日期
     */
    private String date;

    /**
     * 名称
     */
    private String name;

    /**
     * pan卡号清晰度分
     */
    private Double panNoConf;

    /**
     * pan卡号码清晰度分
     */
    private Double dateOfIssueConf;

    /**
     * 父亲名字清晰度分
     */
    private Double fatherConf;

    /**
     * 出生日期清晰度分
     */
    private Double dateConf;

    /**
     * 名字清晰度分
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
}
