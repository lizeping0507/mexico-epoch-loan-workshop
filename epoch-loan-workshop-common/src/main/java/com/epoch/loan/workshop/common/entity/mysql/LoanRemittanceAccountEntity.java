package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanRemittanceAccountEntity
 * @createTime : 2022/4/21 15:07
 * @description : 放款账户实体类
 */
@Data
public class LoanRemittanceAccountEntity {

    /**
     * 账户id
     */
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 账户账户
     */
    private String accountNumber;

    /**
     * 银行
     */
    private String bank;

    /**
     * 姓名
     */
    private String name;

    /**
     * 类型 0:借记卡 1:clabe账户
     */
    private Integer type;

    /**
     * 标记放款卡
     */
    private Integer markLoanCard;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
