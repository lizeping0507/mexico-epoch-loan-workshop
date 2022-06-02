package com.epoch.loan.workshop.mq.collection.reduction;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-05-30 08:35
 * @Description: 同步减免金额入参
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection
 */
@Data
public class CollectionReductionAmountParam implements Serializable {
    /**
     *  订单号
     */
    private String orderNo;

    /**
     * 已还款金额
     */
    private Double returnedAmount;

    /**
     * 减免费用
     */
    private Double reductionAmount;

    /**
     * 剩余还款金额(总)
     */
    private Double remainingRepaymentAmount;
}
