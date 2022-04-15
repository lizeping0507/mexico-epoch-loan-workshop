package com.epoch.loan.workshop.common.mq.repayment.params;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.mq.repayment.params;
 * @className : RepaymentParams
 * @createTime : 2022/3/9 10:56
 * @description : 还款队列参数
 */
@Data
public class RepaymentParams {

    /**
     * 放款支付详情Id
     */
    private String id;
}
