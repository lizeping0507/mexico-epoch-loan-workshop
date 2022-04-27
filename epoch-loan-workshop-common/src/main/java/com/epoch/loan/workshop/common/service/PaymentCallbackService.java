package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service;
 * @className : PaymentCallbackService
 * @createTime : 2022/2/10 16:36
 * @description : 支付回调
 */
public interface PaymentCallbackService {
    /**
     * pandaPay
     *
     * @param params pandaPay支付回调参数
     * @return String
     */
    String pandaPay(PandaPayCallBackParams params) throws Exception;


    /**
     * 检查订单是否存在
     *
     * @param poutId
     * @return
     */
    boolean checkOrder(String poutId);
}
