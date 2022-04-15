package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : OrderDetailParams
 * @createTime : 2022/3/30 15:06
 * @description : 订单详情请求参数封装
 */
@Data
public class OrderDetailParams extends BaseParams {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户Id
     */
    private Long userId;
}
