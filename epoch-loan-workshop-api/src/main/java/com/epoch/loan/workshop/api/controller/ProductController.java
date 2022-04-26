package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.AppMaskModelParams;
import com.epoch.loan.workshop.common.params.params.request.ProductDetailParams;
import com.epoch.loan.workshop.common.params.params.request.ProductListParams;
import com.epoch.loan.workshop.common.params.params.request.ProductRecommendListParams;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller;
 * @className : ProductController
 * @createTime : 2022/3/22 14:30
 * @description : 产品相关接口
 */
@RestController
@RequestMapping(URL.PRODUCT)
public class ProductController extends BaseController {

    /**
     * 产品详情
     *
     * @param params
     * @return
     */
    @Authentication
    @PostMapping(URL.PRODUCT_DETAIL)
    public Result<ProductDetailResult> productDetail(ProductDetailParams params) {
        // 结果集
        Result<ProductDetailResult> result = new Result<>();

        try {

            return productService.productDetail(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController productDetail]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }


    /**
     * 获取App模式
     *
     * @param params 入参
     * @return 首页信息
     */
    @Authentication
    @PostMapping(URL.APP_MASK_MODEL)
    public Result<AppMaskModelResult> appMaskModel(AppMaskModelParams params) {
        // 结果集
        Result<AppMaskModelResult> result = new Result<>();

        try {
            // 获取App模式
            return productService.appMaskModel(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController appModel]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }


    /**
     * 产品列表
     *
     * @param params 入参
     * @return 产品列表
     */
    @Authentication
    @PostMapping(URL.LIST)
    public Result<ProductListResult> list(ProductListParams params) {
        // 结果集
        Result<ProductListResult> result = new Result<>();

        try {
            // 产品列表
            return productService.list(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController list]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取产品推荐列表
     *
     * @param params 基础参数
     * @return 推荐列表
     */
    @Authentication
    @PostMapping(URL.PRODUCT_RECOMMEND_LIST)
    public Result<ProductRecommendResult> recommendList(ProductRecommendListParams params) {
        // 结果集
        Result<ProductRecommendResult> result = new Result<>();

        try {
            // 获取产品推荐列表
            return productService.recommendList(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController recommendList]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
