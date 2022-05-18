package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : RegisterResult
 * @createTime : 2022/3/21 11:29
 * @description : 注册
 */
@Data
@NoArgsConstructor
public class RegisterResult implements Serializable {
    /**
     * token
     */
    private String token;
    /**
     * 用户Id
     */
    private String userId;
}
