package com.epoch.loan.workshop.mq.remittance.payment.hr;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.hr
 * @className : HrPayPayOutQueryParam
 * @createTime : 2022/3/29 19:00
 * @description : HrPay 放款查询参数
 */
@Data
public class HrPayPayOutQueryParam {
    /**
     * 商户号: 平台分配商户号
     */
    private String memberid;
    /**
     * 代付单号: 订单号必须唯一, 16 - 32 个字符
     */
    private String orderid;
    /**
     * 	MD5签名
     */
    private String sign;
}
