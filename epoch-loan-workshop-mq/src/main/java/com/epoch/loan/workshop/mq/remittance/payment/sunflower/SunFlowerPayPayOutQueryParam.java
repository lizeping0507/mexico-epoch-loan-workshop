package com.epoch.loan.workshop.mq.remittance.payment.sunflower;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.sunflower;
 * @className : SunFlowerPayPayOutQueryParam
 * @createTime : 2022/3/01 11:59
 * @description : SunFlowerPay 放款查询参数
 */
@Data
public class SunFlowerPayPayOutQueryParam {
    /**
     * 商户ID
     */
    private String merchant_id;

    /**
     * 交易类型：payin-收款，payout-付款
     */
    private String trade_type;

    /**
     * 商户订单号
     */
    private String merchant_trade_no;

    /**
     * 发送请求时间，格式"yyyy-MM-dd HH:mm:ss"
     */
    private String timestamp;

    /**
     * 签名
     */
    private String sign;
}
