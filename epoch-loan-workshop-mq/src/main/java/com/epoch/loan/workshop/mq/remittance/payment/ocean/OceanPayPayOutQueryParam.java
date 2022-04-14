package com.epoch.loan.workshop.mq.remittance.payment.ocean;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.ocean;
 * @className : OceanPayPayOutQueryParam
 * @createTime : 2022/2/16 11:47
 * @description : Inpay 放款查询参数
 */
@Data
public class OceanPayPayOutQueryParam {

    /**
     * 商户号
     */
    private String code;

    /**
     * 商户代付订单号
     */
    private String merissuingcode;

    /**
     * 签名
     */
    private String signs;
}
