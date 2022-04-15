package com.epoch.loan.workshop.account.repayment.incash;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.incash;
 * @className : IncashPayParams
 * @createTime : 2022/4/12 15:47
 * @description : IncashPay待收参数
 */
@Data
public class InCashPayParams {
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
     * 是	String	金额，单位卢币(最多保留两位小数)
     */
    private String amount;
    /**
     * 是	String	客户姓名
     */
    private String customName;
    /**
     * 是	String	客户电话
     */
    private String customMobile;
    /**
     * 是	String	客户email地址
     */
    private String customEmail;
    /**
     * 是	String	异步通知回调地址
     */
    private String notifyUrl;
    /**
     * 是	String	页面回跳地址（客户操作 支付成功或失败后跳转页面。）
     */
    private String callbackUrl;
    /**
     * 是	String	签名
     */
    private String sign;
}