package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service;
 * @className : ProductService
 * @createTime : 2022/3/22 16:36
 * @description : 产品业务接口
 */
public interface ProductService {
    /**
     * 产品详情
     *
     * @param params
     * @return
     * @throws Exception
     */
    Result<ProductDetailResult> productDetail(ProductDetailParams params) throws Exception;

    /**
     * 获取用户app模式
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<AppMaskModelResult> appMaskModel(BaseParams params) throws Exception;

    /**
     * 产品列表
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ProductListResult> productList(BaseParams params) throws Exception;

    /**
     * 获取产品推荐列表
     *
     * @param params 入参
     * @return 产品集合
     * @throws Exception 请求异常
     */
    Result<ProductRecommendResult> recommendList(ProductRecommendListParams params) throws Exception;

    /**
     * 获取用户客群
     * @param params
     * @return
     */
    Result<UserTypeResult> getUserType(UserTypeParams params);
}
