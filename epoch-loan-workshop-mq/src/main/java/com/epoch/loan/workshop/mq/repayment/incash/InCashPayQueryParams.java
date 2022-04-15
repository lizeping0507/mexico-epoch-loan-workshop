package com.epoch.loan.workshop.mq.repayment.incash;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.repayment.incash;
 * @className : InCashPayQueryParams
 * @createTime : 2022/4/12 14:48
 * @description : InCashPay待收查询参数
 */
@Data
public class InCashPayQueryParams {
    /**
     * 是	String	商户号，平台分配账号
     */
    private String merchant;
    /**
     * 是	String	商户订单号（唯一），字符长度40以内
     */
    private String orderId;
    /**
     * 是	String	签名
     */
    private String sign;
}