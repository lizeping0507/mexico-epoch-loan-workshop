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
public class IncashPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;
    /**
     * string	商户号
     */
    private String merchant;
    /**
     * string	商户订单号
     */
    private String orderId;
    /**
     * string	平台订单号
     */
    private String platOrderId;
    /**
     * string	金额
     */
    private String amount;
    /**
     * string	处理消息
     */
    private String msg;
    /**
     * string	交易状态，值见数据字典
     */
    private String status;
    /**
     * string	签名
     */
    private String sign;
    /**
     * url	string	收银台地址
     */
    private String url;
}
