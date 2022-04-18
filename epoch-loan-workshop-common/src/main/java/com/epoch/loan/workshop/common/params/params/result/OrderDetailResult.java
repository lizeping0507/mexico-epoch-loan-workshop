package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.OrderState;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : OrderDetailResult
 * @createTime : 2022/3/25 12:16
 * @description : 订单详情接口结果封装
 */
@Data
public class OrderDetailResult implements Serializable {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 产品名
     */
    private String productName;

    /**
     * 机构名称
     */
    private String merchantName;

    /**
     * 申请额度
     */
    private String approvalAmount;

    /**
     * 还款时间
     */
    private String repaymentTime;

    /**
     * 实际到账金额
     */
    private String actualAmount;

    /**
     * 还款金额
     */
    private String repaymentAmount;

    /**
     * 申请借款时间
     */
    private String orderTime;

    /**
     * 银行卡名称
     */
    private String bankcardName;

    /**
     * 银行账户号
     */
    private String paymentCard;

    /**
     * 实际还款金额
     */
    private String actualRepaymentAmount;

    /**
     * 订单状态list
     */
    private List<OrderState> orderStatusList;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * appid
     */
    private String appId;

    /**
     * 是否需要上报抓取数据
     */
    private Boolean needCatchData;

    /**
     * 借款协议
     */
    private String checkUrl;
}
