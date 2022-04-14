package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service;
 * @className : RepaymentService
 * @createTime : 2022/2/10 16:36
 * @description : 还款处理
 */
public interface RepaymentService {

    /**
     * 放款发起
     * @param params 放款参数
     * @return 支付地址
     */
    String repayment(RepaymentParams params);
}
