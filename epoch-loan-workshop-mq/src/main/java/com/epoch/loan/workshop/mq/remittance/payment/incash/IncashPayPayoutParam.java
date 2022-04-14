package com.epoch.loan.workshop.mq.remittance.payment.incash;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.incash;
 * @className : IncashPayPayoutParam
 * @createTime : 2022/3/25 14:52
 * @description : IncashPay放款参数
 */
@Data
public class IncashPayPayoutParam {

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
     * 是	String	签名
     */
    private String sign;


    //代付参数
    /**
     * 是	String	代付模式，值见数据字典
     */
    private String mode;
    /**
     * 否	String	收款人银行账号（mode是IMPS必须）
     */
    private String bankAccount;
    /**
     * 否	String	收款人IFSC CODE（mode是IMPS必须）
     */
    private String ifscCode;
    /**
     * 否	String	收款人UPI账户（mode是UPI必须）
     */
    private String upiAccount;


    //代收参数
    /**
     * 是	String	页面回跳地址（客户操作 支付成功或失败后跳转页面。）
     */
    private String callbackUrl;
}
