package com.epoch.loan.workshop.mq.remittance.payment.glob;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.glob
 * @className : GlobPayPayOutQueryParam
 * @createTime : 2022/3/31 11:29
 * @description : GlobPay 放款查询参数
 */
@Data
public class GlobPayPayOutQueryParam {
    /**
     * 商户号: 平台分配商户号
     */
    private String mchId;
    /**
     * 代付单号: 订单号必须唯一, 16 - 32 个字符
     */
    private String payOrderId;
    /**
     * MD5签名
     */
    private String sign;
}
