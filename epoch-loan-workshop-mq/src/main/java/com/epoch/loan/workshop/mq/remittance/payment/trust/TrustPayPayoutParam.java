package com.epoch.loan.workshop.mq.remittance.payment.trust;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.trust;
 * @className : TrustPayPayoutParam
 * @createTime : 2022/3/26 14:08
 * @description : TrustPay放款参数
 */
@Data
public class TrustPayPayoutParam {

    /**
     * 商户号
     */
    private String merchant;
    /**
     * 商户订单号,唯一,长度50以内
     */
    private String orderId;
    /**
     * 	代付金额，单位卢比，允许两位小数
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
     * 客户邮箱
     */
    private String customEmail;

    /**
     * 收款人银行账号
     */
    private String bankAccount;
    /**
     * 收款人IFSC CODE, 格式：4位英文 + 第5位 0 + 6位英文或数字 如ABCD0789456
     */
    private String ifscCode;
    /**
     * 异步通知回调地址
     */
    private String notifyUrl;

    /**
     * 签名
     */
    private String sign;
}
