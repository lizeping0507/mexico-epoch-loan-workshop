package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductDetailResult
 * @createTime : 2022/3/25 12:16
 * @description : 产品详情接口响应参数封装
 */
@Data
public class PandaRepaymentCallbackResult implements Serializable {
    private boolean payable;
    private Integer min_amount;
    private Integer max_amount;
}
