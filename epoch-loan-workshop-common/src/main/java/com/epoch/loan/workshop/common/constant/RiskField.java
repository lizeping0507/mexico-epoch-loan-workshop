package com.epoch.loan.workshop.common.constant;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.constant
 * @className : RiskField
 * @createTime : 2022/04/24 19:48
 * @Description: 风控相关常量
 */
public class RiskField {

    /**
     * 风控版本
     */
    public static  String RISK_VERSION_V1 = "1.0";

    /**
     * RSA加密方式
     */
    public static  String RISK_SIGN_TYPE_RSA = "RSA";

    /**
     * RSA请求数据格式
     */
    public static  String RISK_MESSAGE_TYPE_JSON = "json";

    /**
     * 风控响应信息中
     */
    public static  String RISK_DATA_ERROR_CODE = "errorCode";

    /**
     * 风控响应信息中
     */
    public static  String RISK_DATA_ERROR_MESSAGE = "errMessage";

    /**
     * 手机号
     */
    public static String RISK_MOBILE = "mobile";

    /**
     * crup
     */
    public static String RISK_CURP = "curp";

    /**
     * CURP校验 通过响应状态码
     */
    public static  int RISK_CURP_CHECK_PASS_CODE = 8000;

    // ======= method ========

    /**
     * CURP校验 方法名
     */
    public static  String RISK_CURP_CHECK_METHOD = "riskmanagement.mexico.curp.check";
}
