package com.epoch.loan.workshop.account.repayment.yeah;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.repayment.yeah;
 * @className : YeahPayParams
 * @createTime : 2022/3/8 11:00
 * @description : YeahPay待收参数
 */
@Data
public class YeahPayParams {
    /**
     * ⽀付⾦额
     */
    private String amount;

    /**
     * 商户ID
     */
    private String merchantID;

    /**
     * 固定值：6
     */
    private String payType = "6";

    /**
     * 商户订单ID，每次请求必须唯⼀
     */
    private String merchantOrderId;

    /**
     * 消费的商品名称
     */
    private String productName;

    /**
     * 商品描述
     */
    private String productDescription;

    /**
     * 商户⽤户ID
     */
    private String merchantUserId;

    /**
     * 商户⽤户名
     */
    private String merchantUserName;

    /**
     * 商户⽤户邮箱
     */
    private String merchantUserEmail;

    /**
     * ⽤户身份证ID
     */
    private String merchantUserCitizenId;

    /**
     * ⽤户设备ID
     */
    private String merchantUserDeviceId;

    /**
     * ⽤户IP
     */
    private String merchantUserIp;

    /**
     * 国籍码
     */
    private String countryCode;

    /**
     * 币种代码：
     */
    private String currency;

    /**
     * ⽤户⼿机号
     */
    private String merchantUserPhone;

    /**
     * 商户端跳转地址
     */
    private String redirectUrl;

    /**
     * 拓展字段
     */
    private Object ext;

    /**
     * 签名
     */
    private String sign;
}
