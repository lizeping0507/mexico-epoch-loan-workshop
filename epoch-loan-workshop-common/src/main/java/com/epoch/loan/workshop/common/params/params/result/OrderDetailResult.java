package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.LoanRepaymentRecordDTO;
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
    private Double approvalAmount;

    /**
     * 服务费
     */
    private Double incidentalAmount;

    /**
     * 实际到账金额
     */
    private Double actualAmount;

    /**
     * 利息
     */
    private Double interest;

    /**
     * 罚息
     */
    private Double penaltyInterest;

    /**
     * 预计还款金额(总)
     */
    private Double estimatedRepaymentAmount;

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
     * 申请时间
     */
    private Date applyTime;

    /**
     * 放款时间
     */
    private Date loanTime;

    /**
     * 预计还款时间
     */
    private Date expectedRepaymentTime;

    /**
     * 还款时间
     */
    private Date actualRepaymentTime;
}
