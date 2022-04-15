package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformUserBasicInfoEntity
 * @createTime : 2021/11/19 16:36
 * @description : 用户基本信息实体类 TODO 老表
 */
@Data
public class PlatformUserBasicInfoEntity {

    /**
     * 用户id,关联user表
     */
    private Long id;
    /**
     * 建立时间
     */
    private Date createTime;
    /**
     * 第一个名字
     */
    private String firstName;
    /**
     * 姓氏
     */
    private String lastName;
    /**
     * 中间名称
     */
    private String middleName;
    /**
     * 婚姻状况
     */
    private Integer marital;
    /**
     * 个人邮箱
     */
    private String email;
    /**
     * 职位
     */
    private Integer occupation;
    /**
     * 工资范围
     */
    private Integer salary;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 学历
     */
    private Integer education;
    /**
     * 贷款目的
     */
    private Integer loanPurpose;
}
