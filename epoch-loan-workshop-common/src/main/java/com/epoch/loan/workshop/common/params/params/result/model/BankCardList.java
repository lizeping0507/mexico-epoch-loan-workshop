package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : BankCardList
 * @createTime : 22/3/28 18:11
 * @description : 银行卡列表
 */
@Data
@NoArgsConstructor
public class BankCardList implements Serializable {

    /**
     * 银行账户号
     */
    private String bankCard;

    /**
     * 银行卡名称
     */
    private String bankName;

    /**
     * 掩码银行卡号
     */
    private String safeBankCard;

    /**
     * 是否在机构绑卡
     */
    private Integer bindCardSrc;

    /**
     * 是否为还款卡
     */
    private Integer isRepayCard;

    /**
     * 开户行ifscCode
     */
    private String openBank;

    /**
     * 持卡人姓名
     */
    private String holderName;
}
