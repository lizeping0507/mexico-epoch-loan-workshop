package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
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
     * 获取App模式
     *
     * @param params 入参
     * @return 首页信息
     */
    @PostMapping(URL.APP_MODEL)
    public Result<AppModelResult> appModel(AppModelParams params) {
        // 结果集
        Result<AppModelResult> result = new Result<>();

        try {
            // 获取App模式
            return productService.getAppModel(params);
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
     * 多推-贷超模式-首页
     *
     * @param params 入参
     * @return 首页信息
     */
    @PostMapping(URL.MERGE_PUSH_HOME)
    public Result<MergePushHomeResult> mergePushHome(MergePushHomeParams params) {
        // 结果集
        Result<MergePushHomeResult> result = new Result<>();

        try {
            // 多推-贷超模式-首页
            return productService.mergePushHome(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController mergePushHome]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 多推-贷超模式-产品列表
     *
     * @param params 入参
     * @return 产品列表
     */
    @PostMapping(URL.MERGE_PUSH_LIST)
    public Result<MergePushListResult> mergePushList(MergePushListParams params) {
        // 结果集
        Result<MergePushListResult> result = new Result<>();

        try {
            // 多推-贷超模式-产品列表
            return productService.mergePushList(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController mergePushList]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 多推-变身贷超模式
     *
     * @param params 入参
     * @return 变身成功与否
     */
    @PostMapping(URL.TURN_INTO_LOAN)
    public Result<Object> turnIntoLoan(MergePushHomeParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            // 多推-变身贷超模式
            return productService.turnIntoLoan(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController turnIntoLoan]", e);

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
     * 产品详情
     *
     * @param params 入参
     * @return 产品信息
     */
    @PostMapping(URL.DETAIL)
    public Result<ProductDetailResult> detail(ProductDetailParams params) {
        // 结果集
        Result<ProductDetailResult> result = new Result<>();

        try {
            // 产品详情
            return productService.detail(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController detail]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 产品详情页
     *
     * @param params 入参
     * @return 产品信息
     */
    @PostMapping(URL.VIEW_DETAIL)
    public Result<ProductViewDetailResult> viewDetail(ProductViewDetailParams params) {
        // 结果集
        Result<ProductViewDetailResult> result = new Result<>();

        try {
            // 产品详情
            return productService.viewDetail(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController viewDetail]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 产品是否续贷开量
     *
     * @param params 入参
     * @return 是否续贷开量
     */
    @PostMapping(URL.ISRELOAN)
    public Result<ProductIsReloanResult> isReloan(ProductIsReloanParams params) {
        // 结果集
        Result<ProductIsReloanResult> result = new Result<>();

        try {
            // 产品是否续贷开量
            return productService.isReloan(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController isReloan]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取产品支付渠道
     *
     * @param params 入参
     * @return 聚道信息
     */
    @PostMapping(URL.PAY_CHANNEL)
    public Result<ProductPayChannelResult> payChannel(ProductPayChannelParams params) {
        // 结果集
        Result<ProductPayChannelResult> result = new Result<>();

        try {
            // 获取产品支付渠道
            return productService.payChannel(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController payChannel]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取注册页banner
     *
     * @param params 基础参数
     * @return 链接地址
     */
    @PostMapping(URL.REGISTER_BANNER)
    public Result<UrlResult> findRegisterBanner(BaseParams params) {
        // 结果集
        Result<UrlResult> result = new Result<>();

        try {
            // 获取产品支付渠道
            return productService.findRegisterBanner(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController findRegisterBanner]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取产品 banner列表
     *
     * @param params 基础参数
     * @return 链接地址
     */
    @PostMapping(URL.PRODUCT_BANNER_LIST)
    public Result<BannerListResult> productBannerList(ProductDetailParams params) {
        // 结果集
        Result<BannerListResult> result = new Result<>();

        try {
            // 获取产品支付渠道
            return productService.productBannerList(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController productBannerList]", e);

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


    /**
     * uv统计
     *
     * @param params UV统计入参
     * @return 保存结果
     */
    @PostMapping(URL.UV)
    public Result<Object> countView(CountViewParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            // 获取产品推荐列表
            return productService.countView(params);
        } catch (Exception e) {
            LogUtil.sysError("[ProductController countView]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
