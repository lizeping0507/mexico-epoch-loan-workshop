package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result.model
 * @className : LoanRepaymentRecordDTO
 * @createTime : 2022/04/24 15:26
 * @Description: 历史还款成功的记录
 */
@Data
public class LoanRepaymentRecordDTO implements Serializable {

    /**
     * 还款总额
     */
    private Double totalAmount;

    /**
     * 应还金额
     */
    private Double repaymentAmount;

    /**
     * 手续费
     */
    private Double charge;

    /**
     * 还款时间
     */
    private String successTime;

    /**
     * 还款方式
     */
    private Integer repayWay;
}
