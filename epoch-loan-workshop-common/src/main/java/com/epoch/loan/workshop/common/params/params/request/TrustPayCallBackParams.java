package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : IncashPayCallBackParams
 * @createTime : 2022/3/25 15:34
 * @description : InCashPay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class TrustPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;
    /**
     * string	金额，单位卢比
     */
    private String amount;
    /**
     * string	商户号
     */
    private String merchant;
    /**
     * string	响应说明
     */
    private String msg;
    /**
     * string	商户订单号
     */
    private String orderId;
    /**
     * string	平台订单号
     */
    private String platOrderId;
    /**
     * string	签名，验签请忽略大小写
     */
    private String sign;
    /**
     * url	string	0 订单初始化，1 支付中，2 支付失败，3支付成功
     */
    private String status;
}
