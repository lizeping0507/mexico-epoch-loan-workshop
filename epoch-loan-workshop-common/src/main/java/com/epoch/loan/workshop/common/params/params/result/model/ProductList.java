package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductList
 * @createTime : 2022/3/24 18:23
 * @description : 列表产品
 */
@Data
public class ProductList implements Serializable {

    /**
     * 产品Id
     */
    private String id;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 金额
     */
    private String amountRange;

    /**
     * 利率
     */
    private String interest;

    /**
     * 产品Icon图标
     */
    private String icon;

    /**
     * 按钮文案
     */
    private String button;

    /**
     * 高通过率标识
     */
    private String passRate;

    /**
     * 产品标签
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer orderStatus;
}
