package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : PaymentField
 * @createTime : 2021/3/10 21:59
 * @description : 放款相关字段定义
 */
public class PaymentField {

    // ------------放款(代付)通用字段定义------------
    // 放款发起状态：成功、失败、进行、特殊状态
    public static final int PAYOUT_REQUEST_SUCCESS = 1;
    public static final int PAYOUT_REQUEST_FAILED = 0;
    public static final int PAYOUT_REQUEST_PROCESS = 2;
    public static final int PAYOUT_REQUEST_SPECIAL = 3;
    // 放款查询状态：进行、失败、成功、查询异常、特殊状态
    public static final int PAYOUT_PROCESS = 2;
    public static final int PAYOUT_FAILED = 0;
    public static final int PAYOUT_SUCCESS = 1;
    public static final int PAYOUT_QUERY_ERROR = 3;
    public static final int PAYOUT_QUERY_SPECIAL = 4;

    // ------------还款(代收)通用字段定义------------
    // 放款发起状态：成功、失败、进行、特殊状态
    public static final int PAY_REQUEST_SUCCESS = 1;
    public static final int PAY_REQUEST_FAILED = 0;
    public static final int PAY_REQUEST_PROCESS = 2;
    public static final int PAY_REQUEST_SPECIAL = 3;
    // 放款查询状态：进行、失败、成功、查询异常、特殊状态
    public static final int PAY_PROCESS = 2;
    public static final int PAY_FAILED = 0;
    public static final int PAY_SUCCESS = 1;
    public static final int PAY_QUERY_ERROR = 3;
    public static final int PAY_QUERY_SPECIAL = 4;
    public static final String PAY_TYPE_SPEI = "SPEI";
    public static final String PAY_TYPE_OXXO = "OXXO";



    // =========== PANDAPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String PANDAPAY_APPID = "appId";
    public static final String PANDAPAY_KEY = "apiKey";
    public static final String PANDAPAY_PAYMENT_SOURCES_TYPE = "paymentSourcesType";
    public static final String PANDAPAY_PAYMENT_BANK_ACCOUNT = "bankAccount";
    public static final String PANDAPAY_PAYMENT_BANK_CODE = "bankCode";
    public static final String PANDAPAY_PAYMENT_RFC = "rfc";
    public static final String PANDAPAY_PAYMENT_ACCOUNT_TYPE = "accountType";
    public static final String PANDAPAY_PAYMENT_TYPE = "paymentType";
    public static final String PANDAPAY_PAYMENT_COMPANY_ID = "paymentCompanyId";
    public static final String PANDAPAY_PAYOUT_URL = "payoutUrl";
    public static final String PANDAPAY_PAY_URL = "payUrl";
    public static final String PANDAPAY_OXXO_PAY_URL = "oxxoPayUrl";
    public static final String PANDAPAY_QUERY_URL = "queryUrl";
    public static final String PANDAPAY_IN_QUERY_URL = "payinqueryUrl";
    public static final String PANDAPAY_IN_OXXO_QUERY_URL = "oxxoPayinqueryUrl";
    public static final String PANDAPAY_NOTIFY_URL = "notifyUrl";
    public static final String PANDAPAY_PAYOUT_PREFIX_CODE = "prefixCode";
    // 请求字段

    // 结果解析字段
    public static final String PANDAPAY_DESCRIPCION_ERROR = "descripcionError";
    public static final String PANDAPAY_STATUS = "estado";
    public static final String PANDAPAY_TRANSACTIONID = "transactionId";
    public static final String PANDAPAY_PAY_CLABE = "clabe";

    // 结果参照值
    public static final String PANDAPAY_SUCCESS_VAL = "Success";
    public static final String PANDAPAY_FAILED_VAL_1 = "Cancel";
    public static final String PANDAPAY_FAILED_VAL_2 = "Refund";

}