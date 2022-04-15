package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : YeahPayRepamentCallbackParam
 * @createTime : 2022/3/9 17:46
 * @description : yeahPay 还款回调参数
 */
@Data
public class YeahPayRepamentCallbackParam {
    /**
     * 是	平台订单号
     */
    private String orderId;
    /**
     * 是	平台支付单号
     */
    private String payorderId;
    /**
     * 是	商户订单号
     */
    private String merchantOrderId;
    /**
     * 是	状态 0处理中，1成功，2失败
     */
    private int status;
    /**
     * 是	金额
     */
    private Double amount;
    /**
     * 是	状态说明
     */
    private String msg;
    /**
     * 是	签名
     */
    private String sign;

}
