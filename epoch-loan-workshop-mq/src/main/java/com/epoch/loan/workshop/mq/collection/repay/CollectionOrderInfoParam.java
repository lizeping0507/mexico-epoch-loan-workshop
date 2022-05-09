package com.epoch.loan.workshop.mq.collection.repay;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.collection;
 * @className : CollectionOrderInfoParam
 * @createTime : 2022/3/7 10:56
 * @description : 订单信息
 */
@Data
public class CollectionOrderInfoParam implements Serializable {
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 用户id
     */
    private String thirdUserId;

    /**
     * 逾期天数
     */
    private Integer penaltyDays;

    /**
     * 逾期费
     */
    private Double penaltyAmount;

    /**
     * 结案时间
     */
    private Date settledTime;

    /**
     * 用户客群
     */
    private Integer userType;
}
