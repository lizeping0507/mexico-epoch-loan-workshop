package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : ProductIsReloanParams
 * @createTime : 2022/3/25 12:19
 * @description : 产品是否续贷开量接口请求参数封装
 */
@Data
public class ProductIsReloanParams extends BaseParams {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 产品Id
     */
    private Long productId;
}
