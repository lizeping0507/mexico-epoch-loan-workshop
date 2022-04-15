package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : ProductViewDetailParams
 * @createTime : 2022/3/25 12:18
 * @description : 产品详情页接口请求参数封装
 */
@Data
public class ProductViewDetailParams extends BaseParams {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 产品Id
     */
    private Long productId;
}
