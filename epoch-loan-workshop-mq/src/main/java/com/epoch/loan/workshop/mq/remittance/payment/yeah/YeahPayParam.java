package com.epoch.loan.workshop.mq.remittance.payment.yeah;

import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : package com.epoch.pf.option.common.util.yeahpay.model;
 * @className : WithdrawParam
 * @createTime :  2021/7/17 11:01
 * @description : TODO
 */
public class YeahPayParam {
    /**
     * 国家码：IN 国家码币种参考
     */
    private String countryCode;
    /**
     * 币种代码:INR 国家码币种参考
     */
    private String currency;
    /**
     * 固定值：card
     */
    private String payType = "card";
    /**
     * 商户侧，代付单号
     */
    private String payoutId;
    /**
     * 代付结果通知回调地址
     */
    private String callBackUrl;
    /**
     * 需要到付的账户信息列表
     */
    private List<YeahPayDetailParam> details;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public List<YeahPayDetailParam> getDetails() {
        return details;
    }

    public void setDetails(List<YeahPayDetailParam> details) {
        this.details = details;
    }
}
