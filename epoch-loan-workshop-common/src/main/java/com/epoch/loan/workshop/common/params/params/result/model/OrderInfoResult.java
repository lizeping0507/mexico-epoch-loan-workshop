package com.epoch.loan.workshop.common.params.params.result.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : OrderList
 * @createTime : 2022/3/24 18:23
 * @description : 订单列表信息类
 */
@Data
public class OrderInfoResult implements Serializable {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 账单id
     */
    private String orderBillId;

    /**
     * 产品Id
     */
    private String productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 贷款金额
     */
    private String approvalAmount;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "d-M-yyyy",timezone = "America/Mexico_City")
    private Date applyTime;

    /**
     * 审核通过时间
     */
    private Date examinePassTime;

    /**
     * 放款时间
     */
    @JsonFormat(pattern = "d-M-yyyy",timezone = "America/Mexico_City")
    private Date loanTime;

    /**
     * 到期时间
     */
    @JsonFormat(pattern = "d-M-yyyy",timezone = "America/Mexico_City")
    private Date repaymentTime;

    /**
     * 按钮文案 pagado-还款 rechazado-被拒 vencido-逾期 completar
     */
    private String orderStatusStr;

    /**
     * 剩余还款金额
     */
    public Double repaymentAmount;

    /**
     * 产品logo的url
     */
    public String productIconImageUrl;
}
