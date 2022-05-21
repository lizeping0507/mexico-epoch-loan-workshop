package com.epoch.loan.workshop.common.util;

/**
 * app域名相关工具
 */
public class AppDomainUtil {
    /**
     * 静态资源域名前缀
     */
    private static final String STATIC_DOMAIN_TEMPLATE = "https://static.";
    /**
     * pandaPay OXXO方式还款H5
     */
    private static final String PANDAPAY_OXXO_PREFIX = "/pay/pandaPayOXXO.html?id=";
    /**
     * pandaPay SPEI方式还款H5
     */
    private static final String PANDAPAY_SPEI_PREFIX = "/pay/pandaPaySPEI.html?id=";
    /**
     * pandaPay SPEI方式还款H5
     */
    private static final String REPAYMENT_SUCCESS_PREFIX = "/pay/success.html";
    /**
     * pandaPay SPEI方式还款H5
     */
    private static final String REPAYMENT_FAIL_PREFIX = "/pay/fail.html";

    /**
     * 获取App顶级域名
     *
     * @param appName app名称
     * @return app请求域名
     */
    public static String getAppTLD(String appName) {
        // app顶级域名判断
        switch (appName) {
            case "CreditPeso":
                return "creditopesos.com";
            case "PesoMax":
                return "peso-max.com";
            default:
                return null;
        }
    }

    /**
     * 拼接app资源请求域名
     *
     * @param appName app名称
     * @return app请求域名
     */
    public static String splicingAppResourceDoamin(String appName) {
        return STATIC_DOMAIN_TEMPLATE + getAppTLD(appName);
    }

    /**
     *  pandaPay OXXO方式还款H5
     * @param appName
     * @param id
     * @return
     */
    public static String splicingPandapayOXXORepaymentH5Url(String appName,String id) {
        return splicingAppResourceDoamin(appName) + PANDAPAY_OXXO_PREFIX + id;
    }
    /**
     *  pandaPay 支付成功页面
     * @param appName
     * @return
     */
    public static String splicingRepaymentSuccessH5Url(String appName) {
        return splicingAppResourceDoamin(appName) + REPAYMENT_SUCCESS_PREFIX;
    }
    /**
     *  pandaPay 支付失败页面
     * @param appName
     * @return
     */
    public static String splicingRepaymentFailH5Url(String appName) {
        return splicingAppResourceDoamin(appName) + REPAYMENT_FAIL_PREFIX;
    }

    /**
     * pandaPay SPEI方式还款H5
     * @param appName
     * @param id
     * @return
     */
    public static String splicingPandapaySPEIRepaymentH5Url(String appName, String id) {
        return splicingAppResourceDoamin(appName) + PANDAPAY_SPEI_PREFIX + id;
    }
}
