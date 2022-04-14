package com.epoch.loan.workshop.mq.remittance.payment.yeah;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName :     package com.epoch.pf.option.common.util.yeahpay.model;
 * @className : WithdrawDetailParam
 * @createTime :  2021/7/17 11:12
 * @description : TODO
 */
@Data
public class YeahPayDetailParam {
    /**
     * 代付金额，数字样式的字符串
     */
    private String amount;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 银行卡收款账户（payType为card 时必填）
     */
    private String payeeAccount;
    /**
     * 银行卡收款账户用户（payType为card时必填）
     */
    private String payeeName;
    /**
     * PAN Card编码（payType为card 并且金额大于10000时必填）
     */
    private String panNum;
    /**
     * 银行IFSC编码（payType为card 时必填）
     */
    private String ifsc;
    /**
     * 电子钱包收款ID(为空即可)
     */
    private String walletId;
    /**
     * 电子钱包收款用户名(为空即可)
     */
    private String walletOwnName;

}
