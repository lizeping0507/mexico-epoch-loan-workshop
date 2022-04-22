package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanOrderEntity
 * @createTime : 2021/11/19 16:28
 * @description : 订单实体类
 */
@Data
public class LoanOrderEntity {
    /**
     * 用户渠道id
     */
    public Integer userChannelId;
    /**
     * 订单id
     */
    private String id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 产品id
     */
    private String productId;
    /**
     * 收款银行卡id
     */
    private String bankCardId;
    /**
     * 是否复贷
     */
    private Integer reloan;
    /**
     * 订单风控组
     */
    private String orderModelGroup;
    /**
     * 订单汇款组
     */
    private String remittanceDistributionGroup;

    /**
     * 订单还款组
     */
    private String repaymentDistributionGroup;

    /**
     * 用户类型(客群)
     */
    private Integer userType;

    /**
     * 订单期数
     */
    private Integer stages;

    /**
     * 分期天数
     */
    private Integer stagesDay;

    /**
     * 手续费
     */
    private Double processingFeeProportion;

    /**
     * 利息
     */
    private Double interest;

    /**
     * 罚息
     */
    private Double penaltyInterest;


    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单类型
     */
    private Integer type;

    /**
     * 批准金额
     */
    private Double approvalAmount;

    /**
     * 实际到账金额
     */
    private Double actualAmount;

    /**
     * 附带扣除费用
     */
    private Double incidentalAmount;

    /**
     * 预计还款金额(总)
     */
    private Double estimatedRepaymentAmount;

    /**
     * 实际还款金额(总)
     */
    private Double actualRepaymentAmount;

    /**
     * 名称
     */
    private String appName;

    /**
     * 版本
     */
    private String appVersion;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 审核通过时间
     */
    private Date examinePassTime;

    /**
     * 放款时间
     */
    private Date loanTime;

    /**
     * 到账时间
     */
    private Date arrivalTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

}
