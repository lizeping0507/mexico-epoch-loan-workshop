package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.params.params.result.model.PayH5Result;
import com.epoch.loan.workshop.common.service.ProductService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import com.epoch.loan.workshop.common.zookeeper.UserProductDetailLock;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.control.service;
 * @className : ProductService
 * @createTime : 2022/3/22 14:28
 * @description : 产品相关业务
 */
@DubboService(timeout = 5000)
public class ProductServiceImpl extends BaseService implements ProductService {

    /**
     * 产品详情
     *
     * @param params
     * @return
     */
    @Override
    public Result<ProductDetailResult> productDetail(ProductDetailParams params) {
        return null;
    }

    /**
     * 初始化订单
     *
     * @param userId
     * @param productId
     * @param appName
     * @return
     */
    protected String initOrder(String userId, String productId, String appName) {
        // 锁id
        String lockId = userId + ":" + productId;

        // 使用分布式锁，防止同时创建多条订单
        zookeeperClient.lock(new UserProductDetailLock<String>(lockId) {
            @Override
            public String execute() {
                // 查询用户是否有已经创建且未完结的订单
                Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE};
                List<LoanOrderEntity> loanOrderEntityList = loanOrderDao.findOrderByUserAndProductIdAndStatus(userId, productId, status);
                if (CollectionUtils.isNotEmpty(loanOrderEntityList)) {
                    return loanOrderEntityList.get(0).getId();
                }

                // 查询用户信息
                LoanUserEntity loanUserEntity = loanUserDao.findById(userId);

                // 查询用户指定状态订单
                status = new Integer[]{OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
                loanOrderEntityList = loanOrderDao.findOrderByUserAndProductIdAndStatus(userId, productId, status);

                // 是否复贷
                Integer reloan = 0;
                if (CollectionUtils.isNotEmpty(loanOrderEntityList)) {
                    reloan = 1;
                }

                // 订单id
                String orderId = ObjectIdUtil.getObjectId();

                // 新增订单
                LoanOrderEntity loanOrderEntity = new LoanOrderEntity();
                loanOrderEntity = new LoanOrderEntity();
                loanOrderEntity.setId(orderId);
                loanOrderEntity.setUserId(userId);
                loanOrderEntity.setProductId(productId);
                loanOrderEntity.setUserChannelId(loanUserEntity.getChannelId());
                loanOrderEntity.setBankCardId("");
                loanOrderEntity.setReloan(reloan);

                return orderId;
            }
        });
        return null;

    }

    protected Integer userType(String userId, String productId, String appName) {
        return 0;
    }


    /**
     * 获取用户app模式
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<AppModelResult> getAppModel(AppModelParams params) throws Exception {
        // 结果集
        Result<AppModelResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_GETUSERAPPMODEL;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("registerAddr", params.getGpsAddress());
        requestParam.put("gpsLocation", params.getGps());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        if (ObjectUtils.isNotEmpty(params.getAppType())) {
            requestParam.put("appType", params.getAppType());
        }

        requestParam.put("userId", params.getUserId());
        requestParam.put("productId", params.getProductId());
        requestParam.put("approvalGps", params.getApprovalGps());
        requestParam.put("approvalAddr", params.getApprovalAddr());
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("appsflyerId", params.getAppsflyerId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, AppModelResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        AppModelResult appModelResult = JSONObject.parseObject(data.toJSONString(), AppModelResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(appModelResult);
        return result;
    }

    /**
     * 多推-贷超模式-首页
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<MergePushHomeResult> mergePushHome(MergePushHomeParams params) throws Exception {
        // 结果集
        Result<MergePushHomeResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_MERGE_PUSH_HOME;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());
        if (StringUtils.isNotBlank(params.getRegisterAddr())) {
            requestParam.put("registerAddr", params.getRegisterAddr());
        }
        if (StringUtils.isNotBlank(params.getGpsLocation())) {
            requestParam.put("gpsLocation", params.getGpsLocation());
        }

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, MergePushHomeResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        MergePushHomeResult mergePushHomeResult = JSONObject.parseObject(data.toJSONString(), MergePushHomeResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(mergePushHomeResult);
        return result;
    }

    /**
     * 多推-贷超模式-产品列表
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<MergePushListResult> mergePushList(MergePushListParams params) throws Exception {
        // 结果集
        Result<MergePushListResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_MERGE_PUSH_LIST;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());
        requestParam.put("pageType", params.getPageType());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, MergePushListResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        MergePushListResult mergePushListResult = JSONObject.parseObject(data.toJSONString(), MergePushListResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(mergePushListResult);
        return result;
    }

    /**
     * 多推-变身贷超模式
     *
     * @param params 入参
     * @return 变身成功与否
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> turnIntoLoan(MergePushHomeParams params) throws Exception {
        // 结果集
        Result<Object> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_TURN_INFO_LOAN;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, Object.class, responseJson)) {
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 产品列表
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<ProductListResult> list(ProductListParams params) throws Exception {
        // 结果集
        Result<ProductListResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_LIST;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("gpsLocation", params.getGps());
        requestParam.put("registerAddr", params.getGpsAddress());
        requestParam.put("type", params.getType());


        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ProductListResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ProductListResult res = JSONObject.parseObject(data.toJSONString(), ProductListResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 产品详情页
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<ProductViewDetailResult> viewDetail(ProductViewDetailParams params) throws Exception {
        // 结果集
        Result<ProductViewDetailResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_VIEW_DETAIL;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("productId", params.getProductId());


        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ProductViewDetailResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ProductViewDetailResult res = JSONObject.parseObject(data.toJSONString(), ProductViewDetailResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 产品是否续贷开量
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<ProductIsReloanResult> isReloan(ProductIsReloanParams params) throws Exception {
        // 结果集
        Result<ProductIsReloanResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_ISRELOAN;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("productId", params.getProductId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ProductIsReloanResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ProductIsReloanResult res = JSONObject.parseObject(data.toJSONString(), ProductIsReloanResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 获取产品支付渠道
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<ProductPayChannelResult> payChannel(ProductPayChannelParams params) throws Exception {
        // 结果集
        Result<ProductPayChannelResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_PAYCHANNEL;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());
        requestParam.put("productId", params.getProductId());
        requestParam.put("approvalGps", params.getApprovalGps());
        requestParam.put("approvalAddr", params.getApprovalAddr());
        requestParam.put("orderNo", params.getOrderNo());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ProductPayChannelResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ProductPayChannelResult res = JSONObject.parseObject(data.toJSONString(), ProductPayChannelResult.class);

        if (ObjectUtils.isNotEmpty(res) && ObjectUtils.isNotEmpty(res.getPayUrl())) {
            String parUrl = res.getPayUrl().toString();
            PayH5Result payH5Result = JSON.parseObject(parUrl, PayH5Result.class);
            res.setPayUrl(payH5Result);
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 获取注册页banner
     *
     * @param params 基础参数
     * @return 链接地址
     * @throws Exception
     */
    @Override
    public Result<UrlResult> findRegisterBanner(BaseParams params) throws Exception {
        // 结果集
        Result<UrlResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_BANNER;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        // 请求
        String responseStr = HttpUtils.POST(url, requestParam.toJSONString());

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, UrlResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        UrlResult res = JSONObject.parseObject(data.toJSONString(), UrlResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 获取产品 banner列表
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<BannerListResult> productBannerList(ProductDetailParams params) throws Exception {
        // 结果集
        Result<BannerListResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_BANNER_LIST;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());


        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, BannerListResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        BannerListResult res = JSONObject.parseObject(data.toJSONString(), BannerListResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 获取产品推荐列表
     *
     * @param params 入参
     * @return 产品集合
     * @throws Exception 请求异常
     */
    @Override
    public Result<ProductRecommendResult> recommendList(ProductRecommendListParams params) throws Exception {
        // 结果集
        Result<ProductRecommendResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_RECOMMEND_LIST;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("userId", params.getUserId());
        requestParam.put("type", params.getType());
        requestParam.put("productId", params.getProductId());
        requestParam.put("tag", params.getTag());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ProductRecommendResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ProductRecommendResult res = JSONObject.parseObject(data.toJSONString(), ProductRecommendResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * UV统计
     *
     * @param params UV统计入参
     * @return 保存结果
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> countView(CountViewParams params) throws Exception {
        // 结果集
        Result<Object> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PRODUCT_UV;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("imei", params.getImei());
        requestParam.put("productId", params.getProductId());
        requestParam.put("channelCode", params.getChannelCode());
        requestParam.put("userId", params.getUserId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, Object.class, responseJson)) {
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }
}
