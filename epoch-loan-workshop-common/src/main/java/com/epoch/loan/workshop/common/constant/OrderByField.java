package com.epoch.loan.workshop.common.constant;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.constant
 * @className : OrderByField
 * @createTime : 2022/04/25 14:35
 * @Description: 排序字段常量
 */
public class OrderByField {

    /**
     * 倒序排序
     */
    public final static String DESC = "DESC";

    /**
     * 正序排序
     */
    public final static String ASC = "ASC";

    // ================ 订单表排序字段 ===================

    /**
     * 审批通过时间
     */
    public final static String EXAMINE_PASS_TIME = "examine_pass_time";

    /**
     * 更新时间
     */
    public final static String UPDATE_TIME = "update_time";

    /**
     * 申请时间
     */
    public final static String APPLY_TIME = "apply_time";

    /**
     * 创建时间
     */
    public final static String CREATE_TIME = "create_time";
}
