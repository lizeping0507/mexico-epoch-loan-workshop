package com.epoch.loan.workshop.common.params.result;

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
     * 用户id
     */
    private String userId;

    /**
     * 是否需要抓取
     */
    private Boolean needCatchData;

    /**
     * Appid
     */
    private String appId;

    /**
     * 用户数据抓取
     */
    private String dataNo;
}
