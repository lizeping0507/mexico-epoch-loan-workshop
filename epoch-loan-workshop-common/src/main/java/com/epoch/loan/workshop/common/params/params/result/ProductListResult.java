package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.ProductList;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductListResult
 * @createTime : 2022/3/25 12:15
 * @description :  产品列表响应参数封装
 */
@Data
public class ProductListResult implements Serializable {

    /**
     * 产品列表
     */
    private List<ProductList> list;
}
