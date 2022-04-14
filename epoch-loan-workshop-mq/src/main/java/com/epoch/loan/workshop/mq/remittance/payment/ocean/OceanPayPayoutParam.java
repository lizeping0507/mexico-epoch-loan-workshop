package com.epoch.loan.workshop.mq.remittance.payment.ocean;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.ocean;
 * @className : OceanPayPayoutParam
 * @createTime : 2022/3/01 14:19
 * @description : OceanPay放款参数
 */
@Data
public class OceanPayPayoutParam {

    /**
     * 商户号
     */
    private String code;
    /**
     * 金额，例：10.00
     */
    private String amount;
    /**
     * 银行名称，不允许传中文
     */
    private String bankname;

    /**
     * 收款人姓名
     */
    private String accountname;

    /**
     * 收款人银行账户号
     */
    private String cardnumber;

    /**
     * 收款人手机
     */
    private String mobile;

    /**
     * 收款人邮箱
     */
    private String email;
    /**
     * 当前时间（时间戳，13位毫秒）
     */
    private String starttime;
    /**
     * 回调地址
     */
    private String notifyurl;

    /**
     * 收款人ifsc
     */
    private String ifsc;

    /**
     * 商户代付单号
     */
    private String merissuingcode;

    /**
     * 签名
     */
    private String signs;
}
