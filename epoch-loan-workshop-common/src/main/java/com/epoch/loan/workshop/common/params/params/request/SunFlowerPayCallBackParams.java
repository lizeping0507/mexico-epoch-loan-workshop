package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : SunFlowerPayCallBackParams
 * @createTime : 2022/3/01 15:47
 * @description : SunFlowerPay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class SunFlowerPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;

    /**
     * 支付平台订单号
     */
    private String platform_trade_no;
    /**
     * 商户ID
     */
    private String merchant_id;
    /**
     * 商户订单号
     */
    private String merchant_trade_no;
    /**
     * 交易金额
     */
    private String amount;
    /**
     * 订单状态：0-待支付 1-支付成功 2-支付失败   4-处理中
     */
    private String trade_status;
    /**
     * 交易完成时间,格式"yyyy-MM-dd HH:mm:ss"
     */
    private String trade_finish_time;
    /**
     * 回调通知请求时间,格式"yyyy-MM-dd HH:mm:ss"
     */
    private String call_back_time;
    /**
     * 签名
     */
    private String sign;
}
