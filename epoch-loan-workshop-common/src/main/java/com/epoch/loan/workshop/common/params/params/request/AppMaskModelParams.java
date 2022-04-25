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
public class AppMaskModelParams extends BaseParams {

    /**
     * GPS经纬度
     */
    public String gps;
    /**
     * GPS地址
     */
    public String gpsAddress;
}
