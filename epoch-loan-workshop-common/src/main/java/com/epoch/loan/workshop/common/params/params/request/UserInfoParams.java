package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : UserInfoParams
 * @createTime : 2022/3/29 16:08
 * @description : 用户基本信息请求参数
 */
@Data
public class UserInfoParams extends BaseParams {
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
     * 收入范围
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
