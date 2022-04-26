package com.epoch.loan.workshop.mq.remittance.payment.panda;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.panda;
 * @className : PandaPayPayOutQueryParam
 * @createTime : 2022/4/25
 * @description : pandaPay 放款查询参数
 */
@Data
public class PandaPayPayOutQueryParam {
    /**
     * 商户订单号
     */
    private String claveRastreo;
}
