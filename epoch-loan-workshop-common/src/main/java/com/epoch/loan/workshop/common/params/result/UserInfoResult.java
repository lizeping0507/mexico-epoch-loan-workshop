package com.epoch.loan.workshop.common.params.result;

import com.epoch.loan.workshop.common.params.result.model.Conditions;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : UserInfoResult
 * @createTime : 2022/3/29 16:04
 * @description : 基本信息响应参数封装
 */
@Data
public class UserInfoResult implements Serializable {
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
    private Conditions occupation;
    /**
     * 薪资范围
     */
    private Conditions salary;
    /**
     * 婚姻状况
     */
    private Conditions marital;
    /**
     * 学历
     */
    private Conditions education;
    /**
     * 借款用途
     */
    private Conditions loanPurpose;
    /**
     * 职业类别集合
     */
    private List<Conditions> occupationList;
    /**
     * 薪资范围集合
     */
    private List<Conditions> salaryList;
    /**
     * 婚姻状况集合
     */
    private List<Conditions> maritalList;
    /**
     * 学历集合
     */
    private List<Conditions> educationList;
    /**
     * 借款用途集合
     */
    private List<Conditions> loanPurposeList;
    /**
     * 订单号?
     */
    private String orderNo;

}
