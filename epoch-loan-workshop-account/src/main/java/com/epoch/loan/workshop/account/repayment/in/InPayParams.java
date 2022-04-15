package com.epoch.loan.workshop.account.repayment.in;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.in;
 * @className : InPayParams
 * @createTime : 2022/4/12 14:12
 * @description : InPay待收参数
 */
@Data
public class InPayParams {
    /**
     * 商户号
     */
    private String merchantid;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 交易金额
     */
    private String total_fee;

    /**
     * 异步通知地址
     */
    private String notify_url;

    /**
     * 执行方式
     */
    private String reply_type;

    /**
     * 时间戳
     */
    private String timestamp;

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