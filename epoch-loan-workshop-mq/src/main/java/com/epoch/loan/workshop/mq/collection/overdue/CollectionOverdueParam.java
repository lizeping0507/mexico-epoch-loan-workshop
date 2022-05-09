package com.epoch.loan.workshop.mq.collection.overdue;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-07 18:31
 * @Description: 同步逾期费用请求参数
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection
 */
@Data
public class CollectionOverdueParam implements Serializable {
    /**
     *  订单号
     */
    private String orderNo;
    /**
     * 逾期罚息
     */
    private Double penaltyAmount;
    /**
     * 用户客群
     */
    private Integer userType;
    /**
     * 利息
     */
    private Double interest;
    /**
     * 逾期天数
     */
    private Integer penaltyDay;
    /**
     *  款项类型：1-本金，2-利息，3-服务费
     */
    private Integer fundType;


}
