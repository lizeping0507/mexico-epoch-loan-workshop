package com.epoch.loan.workshop.mq.remittance.payment.ac;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.ac;
 * @className : AcPayPayoutParam
 * @createTime : 2022/3/01 15:20
 * @description : AcPay放款参数
 */
@Data
public class AcPayPayoutParam {

    /**
     * 商户号  平台分配商户号
     */
    private String merId;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 代付金额
     */
    private String money;
    /**
     * 姓名（随意填）
     */
    private String name;
    /**
     * 银行卡号/ vpa
     * 1、银行卡转账，填银行卡号码
     * 2、UPI转账，填写UPI账号
     */
    private String ka;
    /**
     * IFSC码（银行卡必填）
     */
    private String zhihang;
    /**
     * 银行卡(随意填)
     */
    private String bank;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 通知地址
     */
    private String notifyUrl;
    /**
     * 随机字符串（最长32位）
     */
    private String nonceStr;
    /**
     * 签名
     */
    private String sign;
}
