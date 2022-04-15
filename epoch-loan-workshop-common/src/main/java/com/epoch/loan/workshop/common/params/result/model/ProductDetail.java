package com.epoch.loan.workshop.common.params.result.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result.model;
 * @className : ProductDetail
 * @createTime : 2022/3/24 18:23
 * @description : 订单详细信息封装
 */
@Data
public class ProductDetail implements Serializable {

    /**
     * 申请额度描述
     */
    private String loanLimitDesc;

    /**
     * 商户id
     */
    private List<String> loanMessage;

    /**
     * 产品名称
     */
    private List<String> tags;

    /**
     * 申请金额
     */
    private Integer approvalRate;
}
