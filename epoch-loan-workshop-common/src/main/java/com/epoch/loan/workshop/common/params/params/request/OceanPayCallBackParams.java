package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : OceanPayCallBackParams
 * @createTime : 2022/3/01 16:00
 * @description : OceanPay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class OceanPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;

    /**
     * 商户号
     */
    private String code;
    /**
     * 平台代付单号
     */
    private String issuingcode;
    /**
     * 金额
     */
    private String amount;
    /**
     * 商户订单号
     */
    private String merissuingcode;
    /**
     * 交易开始时间
     */
    private String starttime;
    /**
     * 收款人ifsc
     */
    private String ifsc;
    /**
     * 银行名称
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
     * 交易状态：SUCCESS-成功；FAIL-失败
     */
    private String returncode;
    /**
     * 返回信息，成功是utr，失败是失败提示
     */
    private String message;
    /**
     * MD5签名字段
     */
    private String signs;
}
