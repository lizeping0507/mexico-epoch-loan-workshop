package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : UserTypeResult
 * @createTime : 2022/3/25 12:16
 * @description : 客群结果
 */
@Data
public class UserTypeResult implements Serializable {

    private Integer userType;
}
