package com.epoch.loan.workshop.mq.remittance.payment.in;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.in;
 * @className : InPayPayOutQueryParam
 * @createTime : 2022/2/16 11:47
 * @description : Inpay 放款查询参数
 */
@Data
public class InPayPayOutQueryParam {
    /**
     * 商户号
     */
    private String merchantid;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 签名
     */
    private String sign;
}
