package com.epoch.loan.workshop.common.constant;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.constant;
 * @className : ResultEnum
 * @createTime : 2022/3/22 14:28
 * @description : 响应信息枚举
 */
public enum ResultEnum {
    /**
     * 请求成功
     */
    SUCCESS(2000, "Success"),

    // ======= 重定向 需要进一步操作完成请求 ========
    /**
     * 默认重定向
     */
    REDIRECT(3002, "Request redirect"),

    // ======= 客户端错误 ========
    /**
     * 客户端错误
     */
    CLIENT_ERROR(4000, "Client error"),
    /**
     * 请求方式错误
     */
    METHOD_ERROR(4001, "Method error"),
    /**
     * 请求参数错误
     */
    PARAM_ERROR(4002, "Param error"),
    /**
     * 缺少必要参数
     */
    MISSING_REQUIRED_PARAMS(4003, "Missing required parameters"),
    /**
     * 数据不存在
     */
    NO_EXITS(4004, "The data does not exist"),
    /**
     * 需要登录
     */
    NO_LOGIN(4005, "Please login again"),
    /**
     * 版本异常 需要升级
     */
    VERSION_ERROR(4006, "Dear customers, we have added some functions in the latest version. Unfortunately, this current version does not support it. You need update the app before you can proceed."),
    /**
     * url 未映射
     */
    URL_NOT_MAPPING(4007, "Url is not mapping"),
    /**
     * 手机号不存在
     */
    PHONE_NO_EXIT(4008, "Unrecorded phone number, please enter the correct one"),
    /**
     * 密码不正确
     */
    PASSWORD_INVALID(4009, "The password is incorrect，please try again"),
    /**
     * 手机号已存在
     */
    PHONE_EXIT(4010, ""),
    /**
     * 验证码错误
     */
    SMS_CODE_ERROR(4011, "OTP error, please try again"),

    /**
     * 还款账户异常
     */
    REMITTANCE_ACCOUNT_ERROR(4012, "Account error"),

    /**
     * 订单异常
     */
    ORDER_ERROR(4013, "Order error"),

    /**
     * 验证码错误
     */
    SMS_CODE_SEND_FAILED(4011, "smsCode send failed"),


    // ======= 服务端错误 ========
    /**
     * 服务端错误
     */
    SERVER_ERROR(5000, "Server error"),

    /**
     * 服务端错误
     */
    SERVICE_ERROR(5001, "Service exception"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(5002, "System exception, please try again later"),

    /**
     * 超时
     */
    TIMEOUT_ERROR(5003, "Request is timeout"),

    /**
     * 同步错误
     */
    SYNCHRONIZATION_ERROR(5004, "Synchronization error"),

    // ======= KYC错误========

    /**
     * 证件扫描失败，请重新扫描证件
     */
    KYC_SCAN_CARD_ERROR(6000, "The ID scan failed, please scan the ID again."),

    /**
     * 活体检测失败，请重新上传
     */
    KYC_Liveness_ERROR(6001, "Liveness detection failed, please upload again."),

    /**
     * 人脸匹配失败，请重新上传图片
     */
    KYC_FACE_COMPARISON_ERROR(6002, "Face matching failed, please upload the image again."),

    /**
     * 上传文件失败
     */
    KYC_UPLOAD_FILE_ERROR(6040, "Failed to upload file, please upload the image again."),

    /**
     * Rfc或者INE/IFE格式错误
     */
    RFC_INF_ERROR(6010, "Error de format RFC o INE/IFE."),

    /**
     * INE/IFE证件id 已经被认证了
     */
    INF_CERTIFIED_ERROR(6020, "INE/IFE ha sido certificado."),

    /**
     * INE/IFE证件id 已经被其他用户认证了
     */
    INF_CERTIFIED_OTHER_ERROR(6021, "The INE/IFE has been authenticated by other users"),

    /**
     * RFC 已经被认证了
     */
    RFC_CERTIFIED_ERROR(6030, "RFC ha sido certificado."),

    /**
     * RFC 已经被其他用户认证了
     */
    RFC_CERTIFIED_OTHER_ERROR(6031, "The RFC has been authenticated by other users"),

    /**
     * 根据手机号查询 当前账号认证信息与主账户不匹配
     */
    RFC_INF_CERTIFIED_ERROR(6040, "La información de certificación de la cuenta principal no satisfacen los requisitos que exigen la cuenta principal"),

    /**
     * 根据INE/IFE证件id查询 当前账号认证信息与主账户不匹配
     */
    RFC_MOBILE_CERTIFIED_ERROR(6041, "La información de certificación de la cuenta principal no satisfacen los requisitos que exigen la cuenta principal"),

    /**
     * 根据RFC查询 当前账号认证信息与主账户不匹配
     */
    INF_MOBILE_CERTIFIED_OTHER_ERROR(6042, "La información de certificación de la cuenta principal no satisfacen los requisitos que exigen la cuenta principal"),

    /**
     * 与风控交互出错
     */
    LOAN_RISK_EACH_ERROR(6050, "Internal communication error"),

    /**
     * CURP校验失败
     */
    RFC_CHECK_CURP_ERROR(6051, "CURP verification failed"),


    // 业务错误
    /**
     * 被拒
     */
    REJECTED_ERROR(7000, ""),

    /**
     * 多投被拒
     */
    DELIVERY_REJECTED_ERROR(7001, "delivery rejected"),


    ;


    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }
}
