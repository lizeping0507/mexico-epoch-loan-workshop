package com.epoch.loan.workshop.mq.remittance.payment.trust;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.trust;
 * @className : TrustPayPayOutQueryParam
 * @createTime : 2022/3/26 11:47
 * @description : Trustpay 放款查询参数
 */
@Data
public class TrustPayPayOutQueryParam {
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
