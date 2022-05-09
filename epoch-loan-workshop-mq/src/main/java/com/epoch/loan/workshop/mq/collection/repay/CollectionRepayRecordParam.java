package com.epoch.loan.workshop.mq.collection.repay;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.collection;
 * @className : CollectionRepayRecordParam
 * @createTime : 2022/3/7 11:43
 * @description : TODO
 */
@Data
public class CollectionRepayRecordParam implements Serializable {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 还款记录id
     */
    private String thirdRepayRecordId;
    /**
     * 还款总金额
     */
    private Double repayAmount;
    /**
     * 已还款项总金额
     */
    private Double returnedBillAmount;
    /**
     * 已还逾期管理费
     */
    private Double returnedPentaltyAmount;
    /**
     * 款项类型，1-本金，2-罚息，3-服务费
     */
    private Integer fundType;
    /**
     * 还款时间
     */
    private Date repayTime;
    /**
     * 还款方式，1-线上正常还款，2-线上减免还款，3-线下正常还款，4-线下减免还款，5-部分还款
     */
    private Integer repayType;
    /**
     * 支付状态，1-待支付，2-支付中，3-支付成功，4-支付失败
     */
    private Integer payStatus;

}
