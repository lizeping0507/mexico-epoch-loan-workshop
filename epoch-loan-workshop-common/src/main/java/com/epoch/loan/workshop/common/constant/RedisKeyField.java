package com.epoch.loan.workshop.common.constant;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : RedisKeyField
 * @createTime : 2021/11/3 16:16
 * @description : 项目中所有的Key名称 常量配置类
 */
public class RedisKeyField {

    /**
     * 分割
     */
    public final static String SPLIT = ":";

    /**
     * Token
     */
    public final static String TOKEN = "token" + SPLIT;

    /**
     * 用户 Token 记录
     */
    public final static String USER_TOKEN = "userToken" + SPLIT;

    /**
     * 用户缓存记录
     */
    public final static String USER_CACHE = "userCache" + SPLIT;

    /**
     * 订单逾期计算锁
     */
    public final static String ORDER_BILL_DUE_LOCK = "orderBillDueLock" + SPLIT;

    /**
     * 注册验证码
     */
    public final static String SMS_CODE_TEMPLATE = "smsCode" + SPLIT + "%s" + SPLIT + "%s";

    /**
     * advance License记录
     */
    public final static String ADVANCE_LICENSE = "advanceLicenseCacheKey";


    /**
     * MQ延时队列
     */
    public final static String MQ_DELAY = "mqDelay" + SPLIT;

    /**
     * MQ延时队列
     */
    public final static String MQ_DELAY_INDEX = "mqDelayIndex" + SPLIT;

    /**
     * app 相关配置
     */
    public final static String APP_CONFIG = "appConfig" + SPLIT;

    /**
     * app AF_APP_ID配置
     */
    public final static String AF_APP_ID = SPLIT + "afAppId";

    /**
     * app AF_APP_ID配置
     */
    public final static String AF_APP_KEY = SPLIT + "afAppKey";
}
