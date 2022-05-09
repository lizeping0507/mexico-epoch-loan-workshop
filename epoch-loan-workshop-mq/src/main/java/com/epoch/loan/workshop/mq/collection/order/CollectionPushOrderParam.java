package com.epoch.loan.workshop.mq.collection.order;

import com.epoch.loan.workshop.mq.collection.repay.CollectionRepaymentPlanParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-08 14:40
 * @Description: 推送的订单信息
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection.order
 */
@Data
public class CollectionPushOrderParam implements Serializable {

    /**
     * 订单信息
     */
    private CollectionOrderParam orderInfo;

    /**
     * 还款计划
     */
    private List<CollectionRepaymentPlanParam> repaymentPlan;

    /**
     * 用户基本信息
     */
    private CollectionUserInfoParam userInfo;

    /**
     * 通讯录
     */
    private List<CollectionContact> contact;

    /**
     * 紧急联系人
     */
    private List<CollectionEmerContact> emerContact;

    /**
     * 认证信息
     */
    private CollectionAuthInfoParam authInfo;
}
