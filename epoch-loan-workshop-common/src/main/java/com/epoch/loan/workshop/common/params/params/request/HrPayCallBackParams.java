package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : HrPayCallBackParams
 * @createTime : 2022/3/30 11:06
 * @description : HrPay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class HrPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;
    /**
     * 商户编号
     */
    private String memberid;
    /**
     * 商户订单号
     */
    private String orderid;
    /**
     * 平台支付流水号
     */
    private String transaction_id;
    /**
     * 实际代付金额
     */
    private String amount;
    /**
     * 交易时间
     */
    private String datetime;
    /**
     * 交易状态码
     */
    private String returncode;
    /**
     * 失败原因
     */
    private String msg;
    /**
     * 签名
     */
    private String sign;
}
