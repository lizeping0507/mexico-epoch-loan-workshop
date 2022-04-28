package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.PreRepaymentParams;
import com.epoch.loan.workshop.common.params.params.request.RepaymentParams;
import com.epoch.loan.workshop.common.params.params.request.UtrParams;
import com.epoch.loan.workshop.common.params.params.request.PandaRepaymentCallbackParam;
import com.epoch.loan.workshop.common.params.params.result.Result;

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
     *
     * @param params 放款参数
     * @return 支付地址
     */
    String repayment(PreRepaymentParams params);

    /**
     * 回调处理
     *
     * @param params 回调参数
     * @return String
     */
    String pandaPay(PandaRepaymentCallbackParam params);

    /**
     * 调支付提供的UTR接口
     *
     * @param params utr参数
     * @return 调用支付成功与否
     * @throws Exception
     */
    Result<Object> repayUtr(UtrParams params) throws Exception;
}
