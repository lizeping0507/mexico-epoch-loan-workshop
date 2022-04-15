package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : ProductDetailParams
 * @createTime : 2022/3/25 12:18
 * @description : 产品详情接口请求参数封装
 */
@Data
public class ProductDetailParams extends BaseParams {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 产品Id
     */
    private Long productId;

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
}
