package com.epoch.loan.workshop.mq.remittance.payment.fast;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.fast;
 * @className : FastPayPayoutParam
 * @createTime : 2022/2/15 15:02
 * @description : TODO
 */
@Data
public class FastPayPayoutParam {
    /**
     * 商户号
     */
    private String merchantNo;
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 金额
     */
    private String amount;

    /**
     * 支付类型
     */
    private Integer type;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 用户真实姓名
     */
    private String name;

    /**
     * 用户银行卡号/upi 账号
     */
    private String account;

    /**
     * ifscCode
     */
    private String ifscCode;

    /**
     * 传透参数，原样返回
     */
    private String ext;

    /**
     * 版本号 目前 2.0.1,文档标识
     */
    private String version;

    /**
     * MD5 签名
     */
    private String sign;

}
