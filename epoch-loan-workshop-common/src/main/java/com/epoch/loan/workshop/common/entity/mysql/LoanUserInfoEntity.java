package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

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
    private String user_id;

    /**
     * GPS(实时)
     */
    private String gps;

    /**
     * GPS地址(实时)
     */
    private String gps_address;

    /**
     * 注册GPS
     */
    private String register_gps;

    /**
     * 注册GPS地址
     */
    private String register_address;

    /**
     * IP(实时)
     */
    private String ip;

    /**
     * IP地址(实时)
     */
    private String ip_address;

    /**
     * 紧急联系人信息(JSON)
     */
    private String contacts;

    /**
     * 月收入
     */
    private String monthly_income;

    /**
     * 发薪周期
     */
    private String pay_period;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 工资发放方式
     */
    private String pay_method;

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
    private String children_number;


    /**
     * 借款目的
     */
    private String loan_purpose;

    /**
     * 居住类型
     */
    private String live_type;

    /**
     * 地址
     */
    private String papers_address;

    /**
     * 父亲名字
     */
    private String papers_father_name;

    /**
     * 全名
     */
    private String papers_full_name;

    /**
     * 母亲名字
     */
    private String papers_mother_name;

    /**
     * 证件iid
     */
    private String papers_id;

    /**
     * 姓名
     */
    private String papers_name;

    /**
     * 选民id
     */
    private String papers_voter_id;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 创建时间
     */
    private String createTime;

}
