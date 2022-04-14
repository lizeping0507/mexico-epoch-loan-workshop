package com.epoch.loan.workshop.mq.remittance.payment.qe;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.qe;
 * @className : QePayPayOutQueryParam
 * @createTime : 2022/3/29 15:26
 * @description : QePay 放款查询参数
 */
@Data
public class QePayPayOutQueryParam {
    /**
     * 签名方式:MD5不参与签名
     */
    private String sign_type;
    /**
     * 签名:不参与签名
     */
    private String sign;
    /**
     * 商户代码:平台分配唯一
     */
    private String mch_id;
    /**
     * 商家转账单号:代付使用的转账单号
     */
    private String mch_transferId;
}
