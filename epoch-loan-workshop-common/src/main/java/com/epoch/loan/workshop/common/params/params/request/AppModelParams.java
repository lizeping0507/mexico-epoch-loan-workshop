package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : AppModelParams
 * @createTime : 2022/3/21 11:29
 * @description : 获取产品模式接口参数封装
 */
@Data
public class AppModelParams extends BaseParams {

    /**
     * GPS经纬度
     */
    public String gps;
    /**
     * GPS地址
     */
    public String gpsAddress;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 产品Id
     */
    private String productId;
    /**
     * 申请时的经纬度
     */
    private String approvalGps;
    /**
     * 申请时的地址
     */
    private String approvalAddr;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * afid
     */
    private String appsflyerId;
    /**
     * 系统
     */
    private String platform;
    /**
     * 贷超包类型 1-多推包
     */
    private Integer appType;
}
