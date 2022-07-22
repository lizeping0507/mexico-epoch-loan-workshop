package com.epoch.loan.workshop.common.constant;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.constant
 * @className : AfEventField
 * @createTime : 2022/07/18 11:40
 * @Description: af 打点事件
 */
public class AfEventField {

    /**
     * 注册事件
     */
    public static String AF_REGISTER_CODE = "register";

    /**
     * 实名认证完成
     */
    public static String AF_NAME_INFO = "nameinfo";

    /**
     * 基本信息完成
     */
    public static String AF_BASE_INFO = "baseinfo";

    /**
     * 生成订单
     */
    public static String AF_FIRST_ORDER = "firstorder";

    /**
     * 用户首次生成订单
     */
    public static String AF_FIRST_PERSON = "firstperson";

    /**
     * 用户完成绑卡
     */
    public static String AF_BANK_ORDER = "bankorder";

    /**
     * 用户首次完成绑卡
     */
    public static String AF_BANK_PERSON = "bankperson";

    /**
     * 用户完成进件
     */
    public static String AF_SECOND_ORDER = "secondorder";

    /**
     * 用户完成首次进件（
     */
    public static String AF_SECOND_PERSON = "secondperson";

    /**
     * 审批通过
     */
    public static String PASSORDER = "passperson";
}
