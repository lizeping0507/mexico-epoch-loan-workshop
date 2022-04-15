package com.epoch.loan.workshop.common.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductIsReloanResult
 * @createTime : 2022/3/25 12:17
 * @description : 产品是否续贷开量接口响应参数封装
 */
@Data
public class ProductIsReloanResult implements Serializable {

    /**
     * 0：复贷不开量  1：复贷开量
     */
    private Integer type;
}
