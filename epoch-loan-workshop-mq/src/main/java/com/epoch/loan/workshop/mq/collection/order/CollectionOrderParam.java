package com.epoch.loan.workshop.mq.collection.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-08 11:23
 * @Description: 订单推送参数类
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection.order
 */
@Data
public class CollectionOrderParam implements Serializable {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * app 名称
     */
    private String appName;

    /**
     * 放款时间
     */
    private Date loanTime;

    /**
     * 借款类型，1-首贷，2-续贷
     */
    private Integer orderType;

    /**
     * 期限
     */
    private Integer term;

    /**
     * 期限类型，1-天，2-月
     */
    private Integer termType;

    /**
     * 订单状态，0-未结清，1-已结清
     */
    private Integer orderStatus;

    /**
     * 应还时间
     */
    private Date billTime;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 放款金额
     */
    private Double loanAmount;

    /**
     * 实际到账金额
     */
    private Double actualLoanAmount;

    /**
     * 应还款金额
     */
    private Double shouldRepayAmount;

    /**
     * 服务费
     */
    private Double serviceAmount;

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

    /**
     * 三方用户id
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
     * 申请时gps地址
     */
    private String applyAddress;

    /**
     * 身份证邦地址
     */
    private String idProvince;

    /**
     * 注册邦地址
     */
    private String regProvince;

    /**
     * 申请邦地址
     */
    private String applyProvince;

    /**
     * 用户客群
     */
    private String userType;
}
