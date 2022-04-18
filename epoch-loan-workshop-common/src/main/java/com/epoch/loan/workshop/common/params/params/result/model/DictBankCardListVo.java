package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result.model
 * @className : DictBankCardListVo
 * @createTime : 2022/04/01 20:59
 * @Description: 银行卡
 */
@Data
public class DictBankCardListVo implements Serializable {

    /**
     * 开户行ifscCode
     */
    private String openBank;

    /**
     * 开户行名称
     */
    private String openBankName;
}
