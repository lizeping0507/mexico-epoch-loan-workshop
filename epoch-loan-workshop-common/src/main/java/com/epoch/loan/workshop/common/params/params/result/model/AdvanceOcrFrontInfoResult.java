package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result.model
 * @className : AdvanceOcrFrontInfoResult
 * @createTime : 2022/04/20 19:09
 * @Description: advance证件识别请求响应 INE卡正面结果
 */
@Data
public class AdvanceOcrFrontInfoResult implements Serializable {

    /**
     * 生日
     */
    private String birthday;

    /**
     * 地址详细信息
     */
    private String addressAll;

    /**
     * 性别
     */
    private String gender;

    /**
     * 父亲的姓氏
     */
    private String fatherLastName;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 全名
     */
    private String fullName;

    /**
     * 卡的失效年份
     */
    private String expiryYear;

    /**
     * 证件号
     */
    private String idNumber;

    /**
     * 证件注册年月
     */
    private String registrationYearAndMonth;

    /**
     * 地区编号
     */
    private String placeNumber;

    /**
     * 选举编号
     */
    private String electionSectionNumber;

    /**
     * 发卡年份
     */
    private String issueYear;

    /**
     * 国家编号
     */
    private String stateNumber;

    /**
     * 所在地区
     */
    private String district;

    /**
     * 母亲的姓氏
     */
    private String motherLastName;

    /**
     * 名字
     */
    private String name;

    /**
     * 选民id
     */
    private String voterId;

    /**
     * 发票编号
     */
    private String invoice;

    /**
     * 州
     */
    private String state;

    /**
     * 小区地址
     */
    private String subDistrict;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 市政府编号
     */
    private String municipalNumber;

}
