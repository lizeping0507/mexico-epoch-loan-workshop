package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : ProductListParams
 * @createTime : 2022/3/25 12:18
 * @description : 产品列表请求参数封装
 */
@Data
public class ProductListParams extends BaseParams {

    /**
     * GPS经纬度
     */
    public String gps;
    /**
     * GPS地址
     */
    public String gpsAddress;
    /**
     * 产品分类
     */
    private Integer type;
}
