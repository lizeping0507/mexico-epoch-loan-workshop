package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : AppKeysResult
 * @createTime : 2022/3/25 12:16
 * @description : App端所需配置信息
 */
@Data
public class AppKeysResult implements Serializable {
    /**
     * afKey
     */
    private String afKey;
    /**
     * advanceKey
     */
    private String advanceKey;
    /**
     * advance签名密钥
     */
    private String advanceSecretKey;
    /**
     * 风控签名私钥
     */
    private String riskPrivateKey;
    /**
     * 风控SDK签名密钥
     */
    private String riskSecretKey;
}
