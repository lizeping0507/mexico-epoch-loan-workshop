package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : UserInfoSaveResult
 * @createTime : 2022/3/29 16:05
 * @description : 用户基本信息保存接口相应参数封装
 */
@Data
public class UserInfoSaveResult implements Serializable {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 名字
     */
    private String firstName;
    /**
     * 中间名字
     */
    private String middleName;
    /**
     * 姓氏
     */
    private String lastName;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 职业类别
     */
    private Integer occupation;
    /**
     * 婚姻状况
     */
    private Integer salary;
    /**
     * 婚姻状况
     */
    private Integer marital;
    /**
     * 学历
     */
    private Integer education;
    /**
     * 借款用途
     */
    private Integer loanPurpose;
}
