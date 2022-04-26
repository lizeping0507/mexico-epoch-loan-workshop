package com.epoch.loan.workshop.mq.repayment.panda;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.repayment.panda;
 * @className : OxxoPandaPayQueryParams
 * @createTime : 2022/4/25
 * @description : OxxoPandaPay待收查询参数
 */
@Data
public class OxxoPandaPayQueryParams {
    /**
     * reference
     */
    private String reference;
}