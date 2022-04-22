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
     * 产品Id
     */
    private String productId;

    /**
     * 申请时的经纬度
     */
    private String gps;

    /**
     * 申请时的地址
     */
    private String gpsAddress;
}
