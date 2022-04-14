package com.epoch.loan.workshop.common.config;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : URL 常量类
 * @createTime : 2022/2/17 14:36
 * @description : TODO 一句话描述该类的功能
 */
public class URL {
    /*一级*/

    public static final String API = "/api";

    /**
     * 支付回调
     */
    public static final String PAYMENT_CALL_BACK = API + "/paymentCallBack";

    /**
     * 还款回调
     */
    public static final String REPAYMENT_CALL_BACK = API + "/repaymentCallBack";

    /**
     * 还款
     */
    public static final String REPAYMENT = API + "/repayment";

    /**
     * 订单
     */
    public static final String ORDER = API + "/order";

    /*二级*/
    /**
     * InPay
     */
    public static final String INPAY = "/inpay";

    /**
     * YEAHPAY
     */
    public static final String YEAHPAY = "/yeahpay";

    /**
     * SUNFLOWERPAY
     */
    public static final String SUNFLOWERPAY = "/sunflowerpay";

    /**
     * OCEANPAY
     */
    public static final String OCEANPAY = "/oceanpay";

    /**
     * ACPAY
     */
    public static final String ACPAY = "/acpay";

    /**
     * INCASHPAY
     */
    public static final String INCASHPAY = "/incash";

    /**
     * INCASHXIDPAY
     */
    public static final String INCASHXJDPAY = "/incashxjd";

    /**
     * TRUSTPAY
     */
    public static final String TRUSTPAY = "/trust";

    /**
     * QEPAY
     */
    public static final String QEPAY = "/qe";
    /**
     * HRPAY
     */
    public static final String HRPAY = "/hr";
    /**
     * GLOBPAY
     */
    public static final String GLOBPAY = "/glob";
    /**
     * 合同
     */
    public static final String ORDER_CONTRACT = "/contract";

}
