package com.epoch.loan.workshop.mq.remittance.payment.in;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.in;
 * @className : InPayPayoutParam
 * @createTime : 2022/2/16 11:02
 * @description : InPay放款参数
 */
@Data
public class InPayPayoutParam {

    /**
     * 商户号
     */
    private String merchantid;
    /**
     * 商户订单号
     */
    private String out_trade_no;
    /**
     * 交易金额（保留两位小数）
     */
    private String total_fee;

    /**
     * 异步通知地址
     */
    private String notify_url;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 代付模式（IMPS）
     */
    private String payment_mode;

    /**
     * 账号
     */
    private String account_number;
    /**
     * IFSC
     */
    private String ifsc;
    /**
     * 客户姓名
     */
    private String customer_name;

    /**
     * 客户手机号
     */
    private String customer_mobile;

    /**
     * 客户邮箱
     */
    private String customer_email;

    /**
     * 签名
     */
    private String sign;
}
