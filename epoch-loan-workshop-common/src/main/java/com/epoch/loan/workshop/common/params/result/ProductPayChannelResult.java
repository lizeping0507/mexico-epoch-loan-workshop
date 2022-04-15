package com.epoch.loan.workshop.common.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductPayChannelResult
 * @createTime : 2022/3/25 12:17
 * @description : 获取产品支付渠道接口响应参数封装
 */
@Data
public class ProductPayChannelResult implements Serializable {

    /**
     * 支付方式
     */
    private String payWay;

    /**
     * 渠道名称
     */
    private String payChannelName;

    /**
     * k请求url
     */
    private String paySdkUrl;

    /**
     * H5请求url
     */
    private Object payUrl;

}
