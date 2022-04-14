package com.epoch.loan.workshop.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformMerchantEntity
 * @createTime : 2021/11/24 14:22
 * @description : 机构实体类
 */
@Data
public class PlatformMerchantEntity {

    /**
     * 机构id
     */
    private String id;

    /**
     * 商户id
     */
    private String appId;

    /**
     * 商户rsa加密公钥
     */
    private String rsaPublicKey;

    /**
     * 商户验签加密类型
     */
    private String signType;

    /**
     * 商户业务数据是否加密 0:否,1:是
     */
    private String bizEnc;

    /**
     * 商户业务数据des加密密钥
     */
    private String desKey;
}
