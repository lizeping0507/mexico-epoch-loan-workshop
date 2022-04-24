package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.params.params.result.model.PayH5Result;
import com.epoch.loan.workshop.common.service.ProductService;
import com.epoch.loan.workshop.common.util.*;
import com.epoch.loan.workshop.common.lock.UserProductDetailLock;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Date;
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
     * @throws Exception
     */
    @Override
    public Result<ProductDetailResult> productDetail(ProductDetailParams params) throws Exception {
        // 结果集
        Result<ProductDetailResult> result = new Result<>();
        ProductDetailResult resData = new ProductDetailResult();
        result.setData(resData);

        // 产品id
        String productId = params.getProductId();
        LogUtil.sysInfo("productDetail productId:{}",productId);

        // app版本
        String appVersion = params.getAppVersion();

        // app名称
        String appName = params.getAppName();

        // 查询产品详情
        LoanProductEntity loanProductEntity = loanProductDao.findProduct(productId);
        if (ObjectUtils.isEmpty(loanProductEntity)) {
            // 封装结果
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }

        // 订单审核模型
        String orderModelGroup = loanProductEntity.getOrderModelGroup();
        LogUtil.sysInfo("productDetail orderModelGroup:{}",orderModelGroup);

        // 更新GPS信息(userInfo实时) TODO 更新到表里，然后更新缓存

        // 初始化订单
        LogUtil.sysInfo("productDetail params.getUser():{}", params.getUser());
        LoanOrderEntity loanOrderEntity = initOrder(params.getUser(), OrderType.LOAN, appVersion, appName, orderModelGroup, loanProductEntity);

        // 订单状态
        Integer orderStatus = loanOrderEntity.getStatus();
        if (orderStatus == OrderStatus.CREATE) {
            // 如果订单状态处于创建状态，进行多投判断
            boolean rejectionRule = rejectionRule(loanOrderEntity, params.getUser());

            // 多投被拒返回
            if (!rejectionRule) {
                // 封装结果 TODO 看看V1是返回啥
                return result;
            }

        }

        // 认证状态
        PlatformUserAuthEntity userAuth = platformUserAuthDao.findUserAuth(params.getUser().getId());

        // 封装认证状态
        resData.setIdFlag("30".equals(userAuth.getIdCardState()) ? 1 : 0);
        resData.setBaseInfoFlag("30".equals(userAuth.getBaseInfoState()) ? 1 : 0);
        resData.setAddInfoFlag("30".equals(userAuth.getOtherInfoState()) ? 1 : 0);
        resData.setOrderNo(loanOrderEntity.getId());

        return result;
    }

    /**
     * 多投限制
     *
     * @param loanOrderEntity
     * @param user
     * @return
     * @throws Exception
     */
    protected boolean rejectionRule(LoanOrderEntity loanOrderEntity, User user) throws Exception {
        // 模型名称
        String subExpression = "rejectionRule";

        // 用户id
        String userId = loanOrderEntity.getUserId();

        // 注册地址
        String registerAddress = user.getRegisterAddress();

        // 订单id
        String orderId = loanOrderEntity.getId();

        // 用户客群
        Integer userType = loanOrderEntity.getUserType();

        // 手机哈
        String mobile = user.getMobile();

        // app名称
        String appName = loanOrderEntity.getAppName();

        // 渠道ID
        Integer userChannelId = loanOrderEntity.getUserChannelId();

        // 查询渠道信息
        PlatformChannelEntity platformChannelEntity = platformChannelDao.findChannel(userChannelId);

        // 渠道名称
        String channelName = platformChannelEntity.getChannelName();

        // 封装请求参数
        Map<String, String> params = new HashMap<>();
        params.put(Field.METHOD, "riskmanagement.mexico.rejection.rule");
        params.put(Field.APP_ID, riskConfig.getAppId());
        params.put(Field.VERSION, "1.0");
        params.put(Field.SIGN_TYPE, "RSA");
        params.put(Field.FORMAT, "json");
        params.put(Field.TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
        JSONObject bizData = new JSONObject();
        bizData.put(Field.TRANSACTION_ID, userId);
        bizData.put(Field.BORROW_ID, orderId);
        bizData.put(Field.PROGRESS, 0);
        bizData.put(Field.REGISTER_ADDR, registerAddress);
        bizData.put(Field.CHANNEL_NAME, channelName);
        int singleQuantity = loanOrderDao.countProcessOrderNo(user.getAppName(),userId);
        int allQuantity = loanOrderDao.countProcessOrderNo(null, user.getId());
        bizData.put(Field.SINGLE_QUANTITY, singleQuantity);
        bizData.put(Field.ALL_QUANTITY, allQuantity);
        LoanOrderBillEntity fistRepayOrder = loanOrderBillDao.findFistRepayOrder(userId, user.getAppName());
        Date actualRepaymentTime = fistRepayOrder.getActualRepaymentTime();
        int intervalDays = DateUtil.getIntervalDays(new Date(), actualRepaymentTime);
        bizData.put(Field.REPAYMENT_TIME, intervalDays);
        bizData.put(Field.USER_TYPE, userType);
        bizData.put(Field.PHONE, mobile);
        bizData.put(Field.APP_NAME, appName);
        params.put(Field.BIZ_DATA, bizData.toJSONString());

        // 生成签名
        String paramsStr = RSAUtils.getSortParams(params);
        String sign = RSAUtils.addSign(riskConfig.getPrivateKey(), paramsStr);
        params.put(Field.SIGN, sign);

        // 请求参数
        String requestParams = JSONObject.toJSONString(params);

        // 更新节点请求数据
        loanOrderExamineDao.updateOrderExamineRequest(loanOrderEntity.getId(), subExpression, requestParams, new Date());

        // 发送请求
        String result = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
        if (StringUtils.isEmpty(result)) {
            return false;
        }

        // 更新节点响应数据
        loanOrderExamineDao.updateOrderExamineResponse(loanOrderEntity.getId(), subExpression, result, new Date());

        // 转换为JSON
        JSONObject resultJson = JSONObject.parseObject(result);

        // 返回码
        Integer code = resultJson.getInteger(Field.ERROR);
        if (code != 200) {
            return false;
        }

        // 成功
        JSONObject data = resultJson.getJSONObject(Field.DATA);

        // 是否通过
        int pass = data.getInteger(Field.PASS);

        // 未通过
        if (pass == 0) {
            // 更新审核状态
            loanOrderExamineDao.updateOrderExamineStatus(orderId, subExpression, OrderExamineStatus.REFUSE, new Date());
            return false;
        }

        // 通过
        loanOrderExamineDao.updateOrderExamineStatus(orderId, subExpression, OrderExamineStatus.PASS, new Date());
        return true;
    }

    /**
     * 初始化订单
     *
     * @param user
     * @param type
     * @param appVersion
     * @param appName
     * @param orderModelGroup
     * @param productEntity
     * @return
     * @throws Exception
     */
    protected LoanOrderEntity initOrder(User user, Integer type, String appVersion, String appName, String orderModelGroup, LoanProductEntity productEntity) throws Exception {
        // 用户id
        String userId = user.getId();

        // 使用分布式锁，防止同时创建多条订单
        String orderId = zookeeperClient.lock(new UserProductDetailLock<String>(userId) {
            @Override
            public String execute() {
                try {
                    // 产品id
                    String productId = productEntity.getId();
                    LogUtil.sysInfo("productDetail productId:{}", productId);

                    // 查询用户是否有已经创建且未完结的订单
                    Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE};
                    List<LoanOrderEntity> loanOrderEntityList = loanOrderDao.findOrderByUserAndProductIdAndStatus(userId, productId, status);
                    if (CollectionUtils.isNotEmpty(loanOrderEntityList)) {
                        return loanOrderEntityList.get(0).getId();
                    }


                    // 查询用户指定状态订单
                    status = new Integer[]{OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
                    loanOrderEntityList = loanOrderDao.findOrderByUserAndProductIdAndStatus(userId, productId, status);
                    LogUtil.sysInfo("productDetail loanOrderEntityList:{}", loanOrderEntityList);

                    // 是否复贷
                    Integer reloan = 0;
                    if (CollectionUtils.isNotEmpty(loanOrderEntityList)) {
                        reloan = 1;
                    }

                    // 用户客群
                    Integer userType = userType(userId, productId, appName);

                    // 订单id
                    String orderId = ObjectIdUtil.getObjectId();

                    // 新增订单
                    LoanOrderEntity loanOrderEntity = new LoanOrderEntity();
                    loanOrderEntity = new LoanOrderEntity();
                    loanOrderEntity.setId(orderId);
                    loanOrderEntity.setUserId(userId);
                    loanOrderEntity.setProductId(productId);
                    loanOrderEntity.setUserChannelId(user.getChannelId());
                    loanOrderEntity.setBankCardId("");
                    loanOrderEntity.setReloan(reloan);
                    loanOrderEntity.setOrderModelGroup(orderModelGroup);
                    loanOrderEntity.setRemittanceDistributionGroup(productEntity.getRemittanceDistributionGroup());
                    loanOrderEntity.setRepaymentDistributionGroup("H6");
                    loanOrderEntity.setUserType(userType);
                    loanOrderEntity.setStages(productEntity.getStages());
                    loanOrderEntity.setStagesDay(productEntity.getStagesDay());
                    loanOrderEntity.setProcessingFeeProportion(productEntity.getProcessingFeeProportion());
                    loanOrderEntity.setInterest(productEntity.getInterest());
                    loanOrderEntity.setPenaltyInterest(productEntity.getPenaltyInterest());
                    loanOrderEntity.setStatus(OrderStatus.CREATE);
                    loanOrderEntity.setType(type);
                    loanOrderEntity.setApprovalAmount(0.0);
                    loanOrderEntity.setActualAmount(0.0);
                    loanOrderEntity.setIncidentalAmount(0.0);
                    loanOrderEntity.setEstimatedRepaymentAmount(0.0);
                    loanOrderEntity.setActualAmount(0.0);
                    loanOrderEntity.setActualRepaymentAmount(0.0);
                    loanOrderEntity.setAppName(appName);
                    loanOrderEntity.setAppVersion(appVersion);
                    loanOrderEntity.setApplyTime(null);
                    loanOrderEntity.setLoanTime(null);
                    loanOrderEntity.setApplyTime(null);
                    loanOrderEntity.setUpdateTime(new Date());
                    loanOrderEntity.setCreateTime(new Date());
                    Integer insertOrder = loanOrderDao.insertOrder(loanOrderEntity);
                    LogUtil.sysInfo("productDetail insertOrder:{}", insertOrder);

                    // 判断是否新增成功
                    if (insertOrder > 0) {
                        return null;
                    }

                    // 订单审核模型
                    List<String> modelList = loanOrderModelDao.findNamesByGroup(orderModelGroup);
                    modelList.parallelStream().forEach(model -> {
                        LoanOrderExamineEntity loanOrderExamineEntity = new LoanOrderExamineEntity();
                        loanOrderExamineEntity.setOrderId(orderId);
                        loanOrderExamineEntity.setId(ObjectIdUtil.getObjectId());
                        loanOrderExamineEntity.setStatus(OrderExamineStatus.CREATE);
                        loanOrderExamineEntity.setModelName(model);
                        loanOrderExamineEntity.setUpdateTime(new Date());
                        loanOrderExamineEntity.setCreateTime(new Date());
                        loanOrderExamineDao.insertOrderExamine(loanOrderExamineEntity);
                    });
                    LogUtil.sysInfo("productDetail 订单审核模型");

                    // 判断当前初始化订单是什么类型
                    if (OrderType.LOAN == type) {
                        LoanOrderExamineEntity loanOrderExamineEntity = new LoanOrderExamineEntity();
                        loanOrderExamineEntity.setOrderId(orderId);
                        loanOrderExamineEntity.setId(ObjectIdUtil.getObjectId());
                        loanOrderExamineEntity.setStatus(OrderExamineStatus.CREATE);
                        loanOrderExamineEntity.setModelName("rejectionRule");
                        loanOrderExamineEntity.setUpdateTime(new Date());
                        loanOrderExamineEntity.setCreateTime(new Date());
                        loanOrderExamineDao.insertOrderExamine(loanOrderExamineEntity);
                    }

                    return orderId;

                }catch (Exception e){
                    LogUtil.sysError("[ProductServiceImpl initOrder]",e);
                    return null;
                }
            }
        });

        // 判断订单号是否为空
        if (StringUtils.isEmpty(orderId)) {
            throw new Exception();
        }

        // 查询订单
        LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
        if (ObjectUtils.isEmpty(loanOrderEntity)) {
            throw new Exception();
        }

        return loanOrderEntity;

    }

    /**
     * 计算用户客群
     *
     * @param userId
     * @param productId
     * @param appName
     * @return
     */
    protected Integer userType(String userId, String productId, String appName) {
        // 用户在本包是否有还款
        int[] status = {OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
        Integer count = loanOrderDao.countUserOrderByAppInStatus(userId, appName, status);
        // 无:2客群
        if (count == 0){
            return 2;
        }

        // 本包本产品是否有还款
        count = loanOrderDao.countUserOrderByProductAndAppInStatus(userId, productId, appName, status);
        // 无:1客群
        if (count == 0){
            return 1;
        }

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
