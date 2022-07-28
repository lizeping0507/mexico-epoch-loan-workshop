package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanAppKey
 * @createTime : 2022/3/19 17:20
 * @description : App端所需配置信息
 */
@Data
public class LoanAppKeysEntity {
    /**
     * app标识
     */
    private String appName;
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
