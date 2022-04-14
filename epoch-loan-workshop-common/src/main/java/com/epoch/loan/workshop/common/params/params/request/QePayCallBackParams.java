package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : QePayCallBackParams
 * @createTime : 2022/3/29 16:21
 * @description : QePay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class QePayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;
    /**
     * 订单状态:1：代付成功2：代付失败
     */
    private String tradeResult;
    /**
     * 商家转账单号:代付使用的转账单号
     */
    private String merTransferId;
    /**
     * 商户代码:平台分配唯一
     */
    private String merNo;
    /**
     * 平台订单号:平台唯一
     */
    private String tradeNo;
    /**
     * 代付金额
     */
    private String transferAmount;
    /**
     * 订单时间
     */
    private String applyDate;
    /**
     * 版本号:默认1.0
     */
    private String version;
    /**
     * 回调状态:默认SUCCESS
     */
    private String respCode;
    /**
     * 签名:不参与签名
     */
    private String sign;
    /**
     * 签名方式:MD5 不参与签名
     */
    private String signType;
}
