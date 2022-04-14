package com.epoch.loan.workshop.common.params.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params
 * @className : BaseParams
 * @createTime : 2021/3/10 21:59
 * @description : 请求参数基类
 */
@Data
@NoArgsConstructor
public class BaseParams implements Serializable {

    /**
     * 请求流水号
     */
    public String serialNo;

    /**
     * Token
     */
    public String token;

    /**
     * 设备唯一ID
     */
    public String deviceId;

    /**
     * app 名称
     */
    public String appName;

    /**
     * App版本
     */
    public String appVersion;

    /**
     * afId
     */
    public String afId;

    /**
     * gAid
     */
    public String gaId;

    /**
     * gps
     */
    public String gps;
}
