package com.epoch.loan.workshop.account.repayment.trust;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.trust;
 * @className : TrustPayParams
 * @createTime : 2022/4/12 14:12
 * @description : TrustPay待收参数
 */
@Data
public class TrustPayParams {
    /**
     * 商户号，平台分配账号
     */
    private String merchant;

    /**
     * 商户订单号（唯一），字符长度40以内
     */
    private String orderId;

    /**
     * 金额，单位卢币(最多保留两位小数)
     */
    private String amount;

    /**
     * 客户姓名 格式：英文、小数点、空格
     */
    private String customName;

    /**
     * 客户电话 格式：10位数字
     */
    private String customMobile;

    /**
     * 客户email地址
     */
    private String customEmail;

    /**
     * 异步通知回调地址
     */
    private String notifyUrl;

    /**
     * 页面回跳地址（客户操作 支付成功或失败后跳转页面。
     */
    private String callbackUrl;

    /**
     * 签名
     */
    private String sign;
}