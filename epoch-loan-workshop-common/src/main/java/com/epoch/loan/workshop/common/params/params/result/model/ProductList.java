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
    private Long id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 金额
     */
    private String amount;

    /**
     * 利率
     */
    private String rate;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 产品标签集合
     */
    private List<String> tagList;

    /**
     * 按钮文案
     */
    private String button;

    /**
     * 产品Icon图标
     */
    private String icon;

    /**
     * 产品标签
     */
    private String tag;
}
