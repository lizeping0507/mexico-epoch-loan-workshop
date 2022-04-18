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
     *
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
