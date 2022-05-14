package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.LoanRepaymentRecordDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
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
     * 账单id
     */
    private String orderBillId;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 申请额度
     */
    private String approvalAmount;

    /**
     * 服务费
     */
    private String incidentalAmount;

    /**
     * 实际到账金额
     */
    private String actualAmount;

    /**
     * 利息
     */
    private String interest;

    /**
     * 罚息
     */
    private Double penaltyInterest;

    /**
     * 预计还款金额(总)
     */
    private String estimatedRepaymentAmount;

    /**
     * 实际还款金额(总)
     */
    private Double actualRepaymentAmount;

    /**
     * 剩余还款金额(总)
     */
    private Double remainingRepaymentAmount;

    /**
     * 还款记录
     */
    private List<LoanRepaymentRecordDTO> repayRecord;

    /**
     * 银行卡名称
     */
    private String bankCardName;

    /**
     * 收款方式 0:银行卡 1.clabe
     */
    private Integer receiveWay;

    /**
     * 银行账户号
     */
    private String bankCardNo;

    /**
     * 有进入审核时间就用进入审核时间，如果没有，就用创建时间
     */
    @JsonFormat(pattern = "d-M-yyyy")
    private Date applyTime;

    /**
     * 放款时间
     */
    @JsonFormat(pattern = "d-M-yyyy")
    private Date loanTime;

    /**
     * 预计还款时间
     */
    @JsonFormat(pattern = "d-M-yyyy")
    private Date expectedRepaymentTime;

    /**
     * 还款时间
     */
    @JsonFormat(pattern = "d-M-yyyy")
    private Date actualRepaymentTime;
}
