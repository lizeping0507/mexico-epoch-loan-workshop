package com.epoch.loan.workshop.common.params.params.result.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result.model
 * @className : LoanRepaymentRecordResult
 * @createTime : 2022/04/24 15:26
 * @Description: 历史还款成功的记录
 */
@Data
public class LoanRepaymentRecordResult implements Serializable {

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
    @JsonFormat(pattern = "d-M-yyyy")
    private Date successDay;

    /**
     * 还款时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private Date successTime;

    /**
     * 还款方式  0:银行卡 1.clabe
     */
    private Integer repayWay;
}
