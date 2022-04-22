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
    Result<AppModelResult> getAppModel(AppModelParams params) throws Exception;

    /**
     * 多推-贷超模式-首页
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<MergePushHomeResult> mergePushHome(MergePushHomeParams params) throws Exception;

    /**
     * 多推-贷超模式-产品列表
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<MergePushListResult> mergePushList(MergePushListParams params) throws Exception;

    /**
     * 多推-变身贷超模式
     *
     * @param params 入参
     * @return 变身成功与否
     * @throws Exception 请求异常
     */
    Result<Object> turnIntoLoan(MergePushHomeParams params) throws Exception;

    /**
     * 产品列表
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ProductListResult> list(ProductListParams params) throws Exception;

    /**
     * 产品详情页
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ProductViewDetailResult> viewDetail(ProductViewDetailParams params) throws Exception;

    /**
     * 产品是否续贷开量
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ProductIsReloanResult> isReloan(ProductIsReloanParams params) throws Exception;

    /**
     * 获取产品支付渠道
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ProductPayChannelResult> payChannel(ProductPayChannelParams params) throws Exception;

    /**
     * 获取注册页banner
     *
     * @param params 基础参数
     * @return 链接地址
     * @throws Exception 请求异常
     */
    Result<UrlResult> findRegisterBanner(BaseParams params) throws Exception;

    /**
     * 获取产品 banner列表
     *
     * @param params 入参
     * @return 产品集合
     * @throws Exception 请求异常
     */
    Result<BannerListResult> productBannerList(ProductDetailParams params) throws Exception;

    /**
     * 获取产品推荐列表
     *
     * @param params 入参
     * @return 产品集合
     * @throws Exception 请求异常
     */
    Result<ProductRecommendResult> recommendList(ProductRecommendListParams params) throws Exception;

    /**
     * UV统计
     *
     * @param params UV统计入参
     * @return 保存结果
     * @throws Exception 请求异常
     */
    Result<Object> countView(CountViewParams params) throws Exception;

}
