package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : LoginResult
 * @createTime : 2022/3/21 11:29
 * @description : 登录
 */
@Data
@NoArgsConstructor
public class LoginResult implements Serializable {
    /**
     * token
     */
    private String token;
    /**
     * token
     */
    private String userId;
}
