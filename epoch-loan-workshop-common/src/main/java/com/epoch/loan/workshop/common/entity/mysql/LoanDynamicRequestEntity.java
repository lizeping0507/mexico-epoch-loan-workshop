package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanDynamicRequestEntity
 * @createTime : 2022/3/19 17:20
 * @description : 动态请求配置
 */
@Data
public class LoanDynamicRequestEntity implements Serializable {
    /**
     * 原始Url
     */
    private String url;

    /**
     * 映射Url
     */
    private String mappingUrl;

    /**
     * 回参字段映射
     */
    private String mappingResponseParams;

    /**
     * 入参字段映射
     */
    private String mappingRequestParams;

    /**
     * 回参混淆字段
     */
    private String mappingVirtualParams;

}
