package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.OrderState;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ComfirmApplyResult
 * @createTime : 2022/3/25 12:16
 * @description : 订单确认页接口结果封装
 */
@Data
public class ComfirmApplyResult implements Serializable {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 机构名称
     */
    private String merchantName;

    /**
     * 产品名
     */
    private String productName;

    /**
     * 申请借款时间
     */
    private String orderTime;

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
     * 放款银行账户号
     */
    private String paymentCard;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 银行卡名称
     */
    private String bankcardName;

    /**
     * 订单状态list
     */
    private List<OrderState> orderStatusList;

    /**
     * 实际还款金额
     */
    private String actualRepaymentAmount;
}
