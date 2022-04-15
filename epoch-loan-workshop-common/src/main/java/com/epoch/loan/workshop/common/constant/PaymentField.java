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


    // =========== YeahPay 相关字段定义 ===========
    // 配置字段
    public static final String YEAHPAY_APP_ID = "appId";
    public static final String YEAHPAY_APP_KEY = "appKey";
    public static final String YEAHPAY_PAYOUT_URL = "payOutUrl";
    public static final String YEAHPAY_PAY_URL = "payUrl";
    public static final String YEAHPAY_TOKENURL = "tokenUrl";
    public static final String YEAHPAY_QUERY_URL = "queryUrl";
    public static final String YEAHPAY_NAME = "name";
    public static final String YEAHPAY_NOTIFYURL = "notifyUrl";
    public static final String YEAHPAY_MERCHANTID = "merchantId";
    // 请求字段
    public static final String YEAHPAY_AUTHORIZATION = "Authorization";
    public static final String YEAHPAY_TOKEN_PARAM = "grant_type=client_credentials";
    public static final String YEAHPAY_BASIC = "Basic ";
    public static final String YEAHPAY_BEARER = "Bearer ";
    public static final String YEAHPAY_COUNTRY_CODE = "IN";
    public static final String YEAHPAY_CURRENCY = "INR";
    public static final String YEAHPAY_ADDRCITY = "addrCity";
    public static final String YEAHPAY_ADDRSTREET = "addrStreet";
    public static final String YEAHPAY_ADDRNUMBER = "addrNumber";
    public static final String YEAHPAY_PRODUCT = "repayment";
    // 结果解析字段
    public static final String YEAHPAY_CODE = "code";
    public static final String YEAHPAY_ERRORCODE = "errorCode";
    public static final String YEAHPAY_RESULT = "result";
    public static final String YEAHPAY_RESULT_STATUS = "status";
    public static final String YEAHPAY_CHECKPAGEURL = "checkPageUrl";
    public static final String YEAHPAY_ORDERPAYMENTLOAD = "orderPaymentLoad";
    // 结果参照值
    public static final String YEAHPAY_SUCCESS_VAL = "1";
    public static final String YEAHPAY_FAILED_VAL1 = "2";
    public static final String YEAHPAY_FAILED_VAL2 = "5";
    public static final String YEAHPAY_PROCESS_VAL = "";
    public static final String YEAHPAY_SUCCESS_CODE_VAL = "1000";
    public static final String YEAHPAY_MAXIMUM_MSG_VAL = "you have reached the maximum trying times for today";


    // =========== FastPay 放款相关字段定义 ===========
    // 配置字段
    public static final String FASTPAY_MERCHANTNO = "merchantNo";
    public static final String FASTPAY_TYPE = "type";
    public static final String FASTPAY_NOTIFYURL = "notifyUrl";
    public static final String FASTPAY_VERSION = "version";
    public static final String FASTPAY_PAYOUT_URL = "payoutUrl";
    public static final String FASTPAY_QUERY_URL = "queryUrl";
    public static final String FASTPAY_KEY = "key";
    // 请求字段
    public static final Object FASTPAY_ORDERNO = "orderNo";
    // 结果解析字段
    public static final String FASTPAY_CODE = "code";
    public static final String FASTPAY_PAOUT_STATUS = "status";
    // 结果参照值
    public static final String FASTPAY_SUCCESS_VAL = "1";
    public static final String FASTPAY_FAILED_VAL = "3";
    public static final String FASTPAY_SUCCESS_CODE_VAL = "0";


    // =========== INPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String INPAY_MERCHANT_ID = "merchantId";
    public static final String INPAY_KEY = "key";
    public static final String INPAY_NOTIFYURL = "notifyUrl";
    public static final String INPAY_PAYMENT_MODE = "paymentMode";
    public static final String INPAY_PAYOUT_URL = "payoutUrl";
    public static final String INPAY_PAY_URL = "payUrl";
    public static final String INPAY_QUERY_URL = "queryUrl";
    public static final String INPAY_IN_QUERY_URL = "payinqueryUrl";
    // 请求字段

    // 结果解析字段
    public static final String INPAY_CODE = "code";
    public static final String INPAY_DATA = "data";
    public static final String INPAY_STATUS = "status";
    public static final String INPAY_URL = "url";
    public static final String INPAY_ORDER_NUMBER = "order_number";
    // 结果参照值
    public static final String INPAY_SUCCESS_VAL = "payout_success";
    public static final String INPAY_FAILED_VAL = "payout_fail";
    public static final String INPAY_PROCESS_VAL = "payout_ing";
    public static final String INPAY_IN_SUCCESS_VAL = "payin_success";
    public static final String INPAY_IN_FAILED_VAL = "payin_fail";
    public static final String INPAY_IN_PROCESS_VAL = "payin_ing";
    public static final String INPAY_SUCCESS_CODE_VAL = "0";

    // =========== SUNFLOWERPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String SUNFLOWERPAY_MERCHANT_ID = "merchantId";
    public static final String SUNFLOWERPAY_KEY = "key";
    public static final String SUNFLOWERPAY_NOTIFYURL = "notifyUrl";
    public static final String SUNFLOWERPAY_PAYOUT_URL = "payoutUrl";
    public static final String SUNFLOWERPAY_QUERY_URL = "queryUrl";
    // 请求字段

    // 结果解析字段
    public static final String SUNFLOWERPAY_CODE = "ret_code";
    public static final String SUNFLOWERPAY_STATUS = "trade_status";

    // 结果参照值
    public static final String SUNFLOWERPAY_SUCCESS_VAL = "1";
    public static final String SUNFLOWERPAY_FAILED_VAL = "2";
    public static final String SUNFLOWERPAY_SUCCESS_CODE_VAL = "0000";


    // =========== OCEANPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String OCEANPAY_MERCHANT_ID = "merchantId";
    public static final String OCEANPAY_KEY = "key";
    public static final String OCEANPAY_NOTIFYURL = "notifyUrl";
    public static final String OCEANPAY_PAYOUT_URL = "payoutUrl";
    public static final String OCEANPAY_QUERY_URL = "queryUrl";
    // 请求字段

    // 结果解析字段
    public static final String OCEANPAY_CODE = "is";
    public static final String OCEANPAY_MSG = "msg";
    public static final String OCEANPAY_DATA = "data";
    public static final String OCEANPAY_STATUS = "status";
    // 结果参照值
    public static final String OCEANPAY_MSG_SUCCESS_VAL = "success";
    public static final String OCEANPAY_SUCCESS_VAL = "11";
    public static final String OCEANPAY_FAILED_VAL = "00";
    public static final Integer OCEANPAY_SUCCESS_CODE_VAL = 1;


    // =========== ACPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String ACPAY_MERCHANT_ID = "merchantId";
    public static final String ACPAY_KEY = "key";
    public static final String ACPAY_MD5 = "md5";
    public static final String ACPAY_NOTIFYURL = "notifyUrl";
    public static final String ACPAY_PAYOUT_URL = "payoutUrl";
    public static final String ACPAY_QUERY_URL = "queryUrl";
    // 请求字段

    // 结果解析字段
    public static final String ACPAY_CODE = "code";
    public static final String ACPAY_DATA = "data";
    public static final String ACPAY_STATUS = "status";
    // 结果参照值
    public static final String ACPAY_SUCCESS_VAL = "1";
    public static final String ACPAY_FAILED_VAL = "0";
    public static final Integer ACPAY_SUCCESS_CODE_VAL = 1;


    // =========== INCASHPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String INCASHPAY_MERCHANT_ID = "merchantId";
    public static final String INCASHPAY_KEY = "key";
    public static final String INCASHPAY_PAYOUT_URL = "payoutUrl";
    public static final String INCASHPAY_PAYIN_URL = "payinUrl";
    public static final String INCASHPAY_QUERY_URL = "queryUrl";
    public static final String INCASHPAY_NOTIFY_URL = "notifyUrl";
    public static final String INCASHPAY_IN_QUERY_URL = "payinqueryurl";
    public static final String CALLBACK_URL = "callbackUrl";
    // 请求字段

    // 结果解析字段
    public static final String INCASHPAY_CODE = "code";
    public static final String INCASHPAY_DATA = "data";
    public static final String INCASHPAY_STATUS = "status";
    public static final String INCASHPAY_MSG = "errorMessages";
    public static final String INCASGPAY_ORDER_NUMBER = "platOrderId";
    public static final String INCASHPAY_URL = "url";
    // 结果参照值
    public static final String INCASHPAY_SUCCESS_VAL = "PAY_SUCCESS";
    public static final String INCASHPAY_FAILED_VAL = "PAY_FAIL";
    public static final Integer INCASHPAY_SUCCESS_CODE_VAL = 200;
    public static final String INCASHPAY_IN_SUCCESS_VAL = "PAY_SUCCESS";
    public static final String INCASHPAY_IN_FAILED_VAL = "PAY_FAIL";


    // =========== TRUSTHPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String TRUSTPAY_MERCHANT_ID = "merchantId";
    public static final String TRUSTPAY_KEY = "key";
    public static final String TRUSTPAY_PAYOUT_URL = "payoutUrl";
    public static final String TRUSTPAY_PAY_URL = "payUrl";
    public static final String TRUSTPAY_QUERY_URL = "queryUrl";
    public static final String TRUSTPAY_IN_QUERY_URL = "payinqueryurl";
    public static final String TRUSTPAY_NOTIFY_URL = "notifyUrl";
    public static final String TRUSTPAY_CALLBACK_URL = "callbackUrl";
    // 请求字段

    // 结果解析字段
    public static final String TRUSTPAY_CODE = "code";
    public static final String TRUSTPAY_DATA = "data";
    public static final String TRUSTPAY_STATUS = "status";
    public static final String TRUSTPAY_ORDER_NUMBER = "platOrderId";
    public static final String TRUSTPAY_URL = "url";
    // 结果参照值
    public static final String TRUSTPAY_SUCCESS_VAL = "3";
    public static final String TRUSTPAY_FAILED_VAL = "2";
    public static final Integer TRUSTPAY_SUCCESS_CODE_VAL = 1;


    // =========== QEPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String QEPAY_MERCHANT_ID = "merchantId";
    public static final String QEPAY_KEY = "key";
    public static final String QEPAY_PAYOUT_URL = "payoutUrl";
    public static final String QEPAY_QUERY_URL = "queryUrl";
    public static final String QEPAY_NOTIFY_URL = "notifyUrl";
    // 请求字段
    public static final String QEPAY_BANK_CODE = "bankCode";
    // 结果解析字段
    public static final String QEPAY_CODE = "respCode";
    public static final String QEPAY_RESULT = "tradeResult";
    // 结果参照值
    public static final String QEPAY_SUCCESS_VAL = "1";
    public static final String QEPAY_FAILED_VAL = "2";
    public static final String QEPAY_REJECT_VAL = "3";
    public static final String QEPAY_SUCCESS_CODE_VAL = "SUCCESS";

    // =========== HRPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String HRPAY_MERCHANT_ID = "merchantId";
    public static final String HRPAY_KEY = "key";
    public static final String HRPAY_PAYOUT_URL = "payoutUrl";
    public static final String HRPAY_QUERY_URL = "queryUrl";
    public static final String HRPAY_NOTIFY_URL = "notifyUrl";
    // 请求字段
    public static final String HRPAY_BANK_CODE = "bankCode";
    // 结果解析字段
    public static final String HRPAY_CODE = "returncode";
    public static final String HRPAY_STATUS = "status";
    public static final String HRPAY_TRADE_STATE = "trade_state";
    // 结果参照值
    public static final String HRPAY_SUCCESS_VAL = "SUCCESS";
    public static final String HRPAY_FAILED_VAL = "REFUSE";
    public static final String HRRAY_QUERY_SUCCESS_CODE_VAL = "00";
    public static final Integer HRPAY_SUCCESS_CODE_VAL = 1;

    // =========== GLOBPAY 放款相关字段定义 ===========
    // 配置字段
    public static final String GLOBPAY_MERCHANT_ID = "merchantId";
    public static final String GLOBPAY_KEY = "key";
    public static final String GLOBPAY_PAYOUT_URL = "payoutUrl";
    public static final String GLOBPAY_QUERY_URL = "queryUrl";
    public static final String GLOBPAY_NOTIFY_URL = "notifyUrl";
    // 请求字段
    public static final String GLOBPAY_PRODUCT_ID = "productId";
    public static final String GLOBPAY_MODE = "mode";
    // 结果解析字段
    public static final String GLOBPAY_CODE = "code";
    public static final String GLOBPAY_STATUS = "status";
    // 结果参照值
    public static final String GLOBPAY_SUCCESS_VAL_1 = "3";
    public static final String GLOBPAY_SUCCESS_VAL_2 = "4";
    public static final String GLOBPAY_FAILED_VAL_1 = "0";
    public static final String GLOBPAY_FAILED_VAL_2 = "5";
    public static final Integer GLOBPAY_SUCCESS_CODE_VAL = 200;

}