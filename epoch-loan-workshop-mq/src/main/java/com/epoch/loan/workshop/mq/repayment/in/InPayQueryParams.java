package com.epoch.loan.workshop.mq.repayment.in;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.repayment.in;
 * @className : InPayQueryParams
 * @createTime : 2022/4/12 14:48
 * @description : InPay待收查询参数
 */
@Data
public class InPayQueryParams {
    /**
     * 商户号
     */
    private String merchantid;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 签名
     */
    private String sign;
}