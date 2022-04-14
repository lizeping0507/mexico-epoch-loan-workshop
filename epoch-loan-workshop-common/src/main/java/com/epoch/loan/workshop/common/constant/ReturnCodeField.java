package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : ReturnCodeField
 * @createTime : 2021/3/10 21:59
 * @description : 访问拦截器
 */
public class ReturnCodeField {

    /**
     * 成功
     */
    public final static int OK = 0;
    /**
     * 失败
     */
    public final static int NO = 1;

    /**
     * 请求类型错误
     */
    public final static int METHOD_FAIL = 2;

    /**
     * 参数错误
     */
    public final static int PARAMS_FAIL = 3;

    /**
     * 系统异常
     */
    public final static int SYSTEM_ERROR = 4;

    /**
     * Token错误
     */
    public final static int TOKEN_FAIL = 5;

    /**
     * 不存在
     */
    public final static int NOT_EXIST = 6;

    /**
     * 服务异常
     */
    public final static int SERVICE_ERROR = 7;

    /**
     * 没有登录
     */
    public final static int NOT_LOGIN = 8;

    /**
     * 存在
     */
    public final static int EXIST = 7;


    /**
     * AppName错误
     */
    public final static int APP_NAME_FAIL = 8;


    /**
     * 强制更新
     */
    public final static int FORCE_UPDATE = 9;

    /**
     * 建议更新
     */
    public final static int PROPOSAL_UPDATE = 10;

    /**
     * 没有更新
     */
    public final static int NOT_UPDATE = 11;

    /**
     * 设备ID
     */
    public final static int DEVICE_ID_FAIL = 12;

    /**
     * App版本错误
     */
    public final static int APP_VERSION_FAIL = 13;

    /**
     * GPS错误
     */
    public final static int GPS_FAIL = 13;
}
