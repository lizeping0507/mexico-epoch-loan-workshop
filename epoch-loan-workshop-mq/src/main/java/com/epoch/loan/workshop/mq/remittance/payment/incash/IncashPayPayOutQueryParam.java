package com.epoch.loan.workshop.mq.remittance.payment.incash;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.incash;
 * @className : IncashPayPayOutQueryParam
 * @createTime : 2022/3/25 14:52
 * @description : InCashpay 放款查询参数
 */
@Data
public class IncashPayPayOutQueryParam {

    //代付，代收公共模块
    /**
     * 是	String	商户号，平台分配账号
     */
    private String merchant;
    /**
     * 是	String	商户订单号（唯一），字符长度40以内
     */
    private String orderId;
    /**
     * 是	String	签名
     */
    private String sign;

}
