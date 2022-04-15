package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : InPayCallBackParams
 * @createTime : 2022/2/14 10:47
 * @description : Inpay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class InPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;

    /**
     * 系统订单号
     */
    private String order_number;
    /**
     * 状态（交易中：payout_ing，交易成功：payout_success，交易失败：payout_fail）
     */
    private String status;
    /**
     * 商户号
     */
    private String merchantid;
    /**
     * 商户订单号
     */
    private String out_trade_no;
    /**
     * 交易金额
     */
    private String total_fee;
    /**
     * 交易手续费
     */
    private String poundage;
    /**
     * 到账金额
     */
    private String account_fee;
    /**
     * 失败信息
     */
    private String fail_info;
    /**
     * UTR回调凭证
     */
    private String utr;
    /**
     * 签名
     */
    private String sign;
}
