package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.result.Result;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service;
 * @className : PaymentCallbackService
 * @createTime : 2022/2/10 16:36
 * @description : 支付回调
 */
public interface PaymentCallbackService {
    /**
     * yeahPay支付回调处理
     *
     * @param params yeahPay支付回调参数
     * @return String
     */
    String yeahPay(YeahPayCallBackParams params) throws Exception;

    /**
     * inPay支付回调处理
     *
     * @param params inPay支付回调参数
     * @return String
     */
    String inPay(InPayCallBackParams params) throws Exception;

    /**
     * sunFlowerPay支付回调处理
     *
     * @param params sunFlowerPay支付回调参数
     * @return String
     */
    String sunFlowerPay(SunFlowerPayCallBackParams params) throws Exception;

    /**
     * oceanPay支付回调处理
     *
     * @param params oceanPay支付回调参数
     * @return String
     */
    String oceanPay(OceanPayCallBackParams params) throws Exception;

    /**
     * acPay支付回调处理
     *
     * @param params acPay支付回调参数
     * @return String
     */
    String acPay(AcPayCallBackParams params) throws Exception;
    /**
     * incashPay支付回调处理
     *
     * @param params incashPay支付回调参数
     * @return String
     */
    Object incashPay(IncashPayCallBackParams params) throws Exception;
    /**
     * incashXjdPay支付回调处理
     *
     * @param params incashXjdPay支付回调参数
     * @return String
     */
    Object incashXjdPay(IncashPayCallBackParams params) throws Exception;
    /**
     * trustPay支付回调处理
     *
     * @param params trustPay支付回调参数
     * @return String
     */
    Object trustPay(TrustPayCallBackParams params) throws Exception;
    /**
     * qePay支付回调处理
     *
     * @param params qePay支付回调参数
     * @return String
     */
    Object qePay(QePayCallBackParams params) throws Exception;
    /**
     * hrPay支付回调处理
     *
     * @param params hrPay支付回调参数
     * @return String
     */
    Object hrPay(HrPayCallBackParams params) throws Exception;
    /**
     * globPay支付回调处理
     *
     * @param params globPay支付回调参数
     * @return String
     */
    Object globPay(GlobPayCallBackParams params) throws Exception;

    /**
     * 检查订单是否存在
     *
     * @param poutId
     * @return
     */
    boolean checkOrder(String poutId);
}
