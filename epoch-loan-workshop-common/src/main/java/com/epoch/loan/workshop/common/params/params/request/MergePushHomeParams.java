package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : MergePushHomeParams
 * @createTime : 2022/3/21 11:29
 * @description : 多推首页接口请求数据封装
 */
@Data
public class MergePushHomeParams extends BaseParams {

    /**
     * 注册地址
     */
    private String registerAddr;

    /**
     * 经纬度
     */
    private String gpsLocation;
}
