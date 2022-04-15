package com.epoch.loan.workshop.mq.repayment.trust;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.repayment.trust;
 * @className : TrustPayQueryParams
 * @createTime : 2022/4/12 14:48
 * @description : TrustPay待收查询参数
 */
@Data
public class TrustPayQueryParams {
    /**
     * 商户号
     */
    private String merchant;

    /**
     * 商户订单号
     */
    private String orderId;

    /**
     * 签名
     */
    private String sign;
}