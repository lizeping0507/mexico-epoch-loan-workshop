package com.epoch.loan.workshop.mq.remittance.payment.ac;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.ac;
 * @className : AcPayPayOutQueryParam
 * @createTime : 2022/3/01 15:31
 * @description : Acpay 放款查询参数
 */
@Data
public class AcPayPayOutQueryParam {

    /**
     * 商户号
     */
    private String merId;

    /**
     * 商户代付订单号
     */
    private String orderId;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 签名
     */
    private String sign;
}
