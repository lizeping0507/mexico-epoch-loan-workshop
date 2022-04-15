package com.epoch.loan.workshop.mq.remittance.payment.sunflower;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.sunflower;
 * @className : SunFlowerPayPayoutParam
 * @createTime : 2022/3/01 11:29
 * @description : SunFlowerPay放款参数
 */
@Data
public class SunFlowerPayPayoutParam {

    /**
     * 商户ID
     */
    private String merchant_id;
    /**
     * 商户订单号
     */
    private String merchant_trade_no;
    /**
     * 交易金额，例：100.00
     */
    private String amount;
    /**
     * 支付方式：BANK/VPA
     */
    private String pay_type;
    /**
     * 订单描述
     */
    private String description;
    /**
     * 用户姓名
     */
    private String customer_name;
    /**
     * 用户邮箱
     */
    private String customer_email;
    /**
     * 用户手机号
     */
    private String customer_mobile;
    /**
     * 用户地址
     */
    private String customer_address;
    /**
     * 银行帐号（pay_type 为BANK 时必填）
     */
    private String account_no;
    /**
     * 银行IFSC编码（pay_type 为BANK 时必填）
     */
    private String ifsc;
    /**
     * 用户的UPI ID(pay_type 为VPA时必填)
     */
    private String upi_id;
    /**
     * ⽀付结果异步回调URL
     */
    private String notify_url;
    /**
     * 发送请求时间，格式"yyyy-MM-dd HH:mm:ss"
     */
    private String timestamp;
    /**
     * 签名
     */
    private String sign;

}
