package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductViewDetailParamsResult
 * @createTime : 2022/3/25 12:16
 * @description : 贷款流程
 */
@Data
public class ProductProcess implements Serializable {

    /**
     * 描述
     */
    private String description;

    /**
     * 贷款进程
     */
    private String process;

}
