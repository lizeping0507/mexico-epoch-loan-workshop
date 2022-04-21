package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params.result.model
 * @className : RemittanceAccountList
 * @createTime : 2022/4/21 14:45
 * @description : 放款账户
 */
@Data
public class RemittanceAccountList implements Serializable {
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
     * 类型
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
