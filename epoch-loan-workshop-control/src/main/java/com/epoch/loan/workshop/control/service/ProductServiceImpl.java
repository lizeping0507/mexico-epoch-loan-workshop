package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.lock.UserProductDetailLock;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.params.params.result.model.PayH5Result;
import com.epoch.loan.workshop.common.params.params.result.model.ProductList;
import com.epoch.loan.workshop.common.service.ProductService;
import com.epoch.loan.workshop.common.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.*;

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

        // 产品id
        String productId = params.getProductId();

        // app版本
        String appVersion = params.getAppVersion();

        // app名称
        String appName = params.getAppName();

        // 用户id
        String userId = params.getUser().getId();

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

        // 更新GPS信息(userInfo实时)
        updateUserGpsMsg(userId, params.getGps(), params.getGpsAddress());
        
        // 初始化订单
        LoanOrderEntity loanOrderEntity = initOrder(params.getUser(), OrderType.LOAN, appVersion, appName, orderModelGroup, loanProductEntity);

        // 订单是否创建成功
        if (ObjectUtils.isEmpty(loanOrderEntity)) {
            // 封装结果
            result.setReturnCode(ResultEnum.SYNCHRONIZATION_ERROR.code());
            result.setMessage(ResultEnum.SYNCHRONIZATION_ERROR.message());
            return result;
        }

        // 订单状态
        Integer orderStatus = loanOrderEntity.getStatus();
        if (orderStatus == OrderStatus.CREATE) {
            // 如果订单状态处于创建状态，进行多投判断
            boolean rejectionRule = rejectionRule(loanOrderEntity, params.getUser());

            // 多投被拒返回
            if (!rejectionRule) {
                // 封装结果
                result.setReturnCode(ResultEnum.DELIVERY_REJECTED_ERROR.code());
                result.setMessage(ResultEnum.DELIVERY_REJECTED_ERROR.message());
                return result;
            }
        }

        // 封装结果集
        ProductDetailResult resData = new ProductDetailResult();
        resData.setArrivalRange(loanProductEntity.getArrivalRange());
        resData.setInterestRange(loanProductEntity.getInterestRange());
        resData.setRepaymentRange(loanProductEntity.getRepaymentRange());
        resData.setServiceFeeRange(loanProductEntity.getServiceFeeRange());
        resData.setAmount(loanProductEntity.getAmountRange());
        resData.setIdFlag(params.getUser().isIdentityAuth() ? 1 : 0);
        resData.setBaseInfoFlag(params.getUser().isBasicInfoAuth() ? 1 : 0);
        resData.setAddInfoFlag(params.getUser().isAddInfoAuth() ? 1 : 0);
        resData.setOrderId(loanOrderEntity.getId());
        result.setData(resData);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 获取用户app模式
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<AppMaskModelResult> appMaskModel(AppMaskModelParams params) throws Exception {
        // 结果集
        Result<AppMaskModelResult> result = new Result<>();
        AppMaskModelResult appMaskModelResult = new AppMaskModelResult();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());

        // 用户id
        String userId = params.getUser().getId();

        // app名称
        String appName = params.getUser().getAppName();

        // App版本
        String appVersion = params.getAppVersion();

        // 更新GPS信息(userInfo实时)
        updateUserGpsMsg(params.getUser().getUserInfoId(), params.getGps(), params.getGpsAddress());
        tokenManager.updateUserCache(userId);

        // 初始化金额
        appMaskModelResult.setAmount("10000");

        /*查询用户认证情况*/
        // 身份认证
        appMaskModelResult.setIdentityAuth(0);
        if (params.getUser().isIdentityAuth()) {
            appMaskModelResult.setIdentityAuth(1);
        }

        // 基本信息认证
        appMaskModelResult.setBasicInfoAuth(0);
        if (params.getUser().isBasicInfoAuth()) {
            appMaskModelResult.setBasicInfoAuth(1);
        }

        // 补充信息认证
        appMaskModelResult.setAddInfoAuth(0);
        if (params.getUser().isAddInfoAuth()) {
            appMaskModelResult.setAddInfoAuth(1);
        }

        // 查询用户有没有添加过卡
        appMaskModelResult.setRemittanceAccountAuth(0);
        if (params.getUser().isRemittanceAccountAuth()) {
            appMaskModelResult.setRemittanceAccountAuth(1);
        }

        // 查询四项认证是否都通过
        if (!params.getUser().isIdentityAuth() || !params.getUser().isBasicInfoAuth() || !params.getUser().isAddInfoAuth()) {
            // 没有通过 返回结果
            appMaskModelResult.setMaskModel(3);
            appMaskModelResult.setButton(OrderUtils.button(OrderStatus.CREATE));
            result.setData(appMaskModelResult);
            return result;
        }

        // 查询用户有没有贷超模式订单
        Integer orderCount = loanOrderDao.findOrderCountByUserIdAndType(userId, OrderType.LOAN);

        // 查询用户指定状态下的订单
        if (orderCount > 0) {
            // 贷超模式已经有订单
            appMaskModelResult.setMaskModel(1);
            result.setData(appMaskModelResult);
            return result;
        }

        // 没有指定状态的订单就生成一个订单
        Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
        List<LoanOrderEntity> loanOrderEntityList = loanOrderDao.findOrderByUserIdAndStatus(userId, status);
        if (CollectionUtils.isEmpty(loanOrderEntityList)) {
            /*查询订单是否全部被拒*/
            status = new Integer[]{OrderStatus.EXAMINE_FAIL};
            loanOrderEntityList = loanOrderDao.findOrderByUserIdAndStatus(userId, status);
            if (CollectionUtils.isNotEmpty(loanOrderEntityList)) {
                // 更新时间
                Date updateTime = loanOrderEntityList.get(0).getUpdateTime();

                // 格式化时间判断是否是当天的订单
                String updateTimeStr = DateUtil.DateToString(updateTime, "yyyy-MM-dd");
                if (updateTimeStr.equals(DateUtil.getDefault())) {
                    // 当天订单不允许再次进行申请
                    appMaskModelResult.setMaskModel(2);
                    appMaskModelResult.setButton(OrderUtils.button(OrderStatus.EXAMINE_FAIL));
                    result.setData(appMaskModelResult);
                    return result;
                }
            }

            /*生成新的订单*/
            // 查询A阈值的承接盘
            LoanMaskEntity loanMaskEntity = loanMaskDao.findLoanMaskByAppNameAndLevel(appName, "A");

            // 查询承接盘详细信息
            LoanProductEntity loanProductEntity = loanProductDao.findProduct(loanMaskEntity.getProductId());

            // 生成订单
            LoanOrderEntity loanOrderEntity = initOrder(params.getUser(), OrderType.MASK, appVersion, appName, "MASK", loanProductEntity);

            // 订单是否创建成功
            if (ObjectUtils.isEmpty(loanOrderEntity)) {
                // 封装结果
                result.setReturnCode(ResultEnum.SYNCHRONIZATION_ERROR.code());
                result.setMessage(ResultEnum.SYNCHRONIZATION_ERROR.message());
                return result;
            }

            // 订单状态
            Integer orderStatus = loanOrderEntity.getStatus();

            // 返回结果集
            appMaskModelResult.setMaskModel(0);
            appMaskModelResult.setButton(OrderUtils.button(orderStatus));
            appMaskModelResult.setOrderNo(loanOrderEntity.getId());
            appMaskModelResult.setOrderStatus(orderStatus);
            result.setData(appMaskModelResult);
            return result;
        }

        // 获取订单
        LoanOrderEntity loanOrderEntity = loanOrderEntityList.get(0);

        // 订单编号
        String orderNo = loanOrderEntity.getId();

        // 订单状态
        Integer orderStatus = loanOrderEntity.getStatus();

        // 判断订单状态是否已经结清
        if (orderStatus == OrderStatus.DUE_COMPLETE || orderStatus == OrderStatus.COMPLETE) {
            // 返回结果
            appMaskModelResult.setMaskModel(1);
            result.setData(appMaskModelResult);
            return result;
        }

        // 查询承接盘
        String productId = loanOrderEntity.getProductId();
        LoanMaskEntity loanMaskEntity = loanMaskDao.findLoanMaskByAppNameAndProductId(appName, productId);

        // 如果阈值为A在途订单就进入贷超模式
        if (loanMaskEntity.getLevel().equals("A") && orderStatus >= OrderStatus.WAY) {
            // 返回结果
            appMaskModelResult.setMaskModel(1);
            appMaskModelResult.setButton(OrderUtils.button(orderStatus));
            appMaskModelResult.setOrderNo(orderNo);
            appMaskModelResult.setOrderStatus(orderStatus);
            result.setData(appMaskModelResult);
            return result;
        }

        // 如果订单处于在途或者在途之后的状态那么金额就为预计还款金额
        if (orderStatus >= OrderStatus.WAY) {
            appMaskModelResult.setAmount(String.valueOf(loanOrderEntity.getEstimatedRepaymentAmount()));
        }

        // 返回结果
        appMaskModelResult.setMaskModel(0);
        appMaskModelResult.setButton(OrderUtils.button(orderStatus));
        appMaskModelResult.setOrderNo(orderNo);
        appMaskModelResult.setOrderStatus(orderStatus);
        result.setData(appMaskModelResult);
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
        Result<ProductListResult> result = new Result<>();

        // 产品列表
        List<ProductList> list = new ArrayList<>();

        // 待还款订单产品
        List<ProductList> waitRepaymentOrderProductList = new ArrayList<>();

        // 可续贷且开量产品
        List<ProductList> reloanOrderProductList = new ArrayList<>();

        // 审核中或放款中 且开量产品
        List<ProductList> examineWaitOrWaitPayOrderProductList = new ArrayList<>();

        // 被拒产品
        List<ProductList> examineFailOrderProductList = new ArrayList<>();

        // 新贷且开量产品
        List<ProductList> newLoanAndOpenProductList = new ArrayList<>();

        // 关量产品
        List<ProductList> closeProductList = new ArrayList<>();


        String userId = params.getUser().getId();

        // 查询所有产品
        List<LoanProductEntity> products = loanProductDao.findAll();
        Map<String, LoanProductEntity> productMap = new HashMap<>();
        products.forEach(product -> {
            productMap.put(product.getId(), product);
        });

        // 查询其他包承接盘
        List<LoanMaskEntity> otherLoanMaskList = loanMaskDao.findLoanMaskByAppNameIsNot(params.getAppName());

        // 过滤它包承接盘产品
        otherLoanMaskList.parallelStream().forEach(loanMask -> {
            productMap.remove(loanMask.getProductId());
        });

        /*所有待还款订单*/
        int[] status = {OrderStatus.WAY, OrderStatus.DUE};
        List<LoanOrderEntity> waitRepaymentOrderList = loanOrderDao.findOrderByUserAndStatusIn(userId, status);
        for (LoanOrderEntity loanOrderEntity : waitRepaymentOrderList) {
            String productId = loanOrderEntity.getProductId();
            LoanProductEntity loanProductEntity = productMap.get(productId);

            // TODO
            waitRepaymentOrderProductList.add(convertToProductList(loanProductEntity, loanOrderEntity));

            // 删除
            productMap.remove(productId);
        }

        /*可续贷且开量产品*/
        status = new int[]{OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
        List<LoanOrderEntity> reloanOrderList = loanOrderDao.findOrderByUserAndStatusIn(userId, status);
        for (LoanOrderEntity loanOrderEntity : reloanOrderList) {
            String productId = loanOrderEntity.getProductId();
            LoanProductEntity loanProductEntity = productMap.get(productId);

            if (loanProductEntity.getIsOpen() == 1) {
                // TODO
                reloanOrderProductList.add(convertToProductList(loanProductEntity, loanOrderEntity));
                // 删除
                productMap.remove(productId);
            }
        }

        /*审核通过的产品*/
        status = new int[]{OrderStatus.EXAMINE_PASS};
        List<LoanOrderEntity> examinePassOrderList = loanOrderDao.findOrderByUserAndStatusIn(userId, status);
        for (LoanOrderEntity loanOrderEntity : examinePassOrderList) {
            String productId = loanOrderEntity.getProductId();
            LoanProductEntity loanProductEntity = productMap.get(productId);

            if (loanProductEntity.getIsOpen() == 1) {
                // TODO
                list.add(convertToProductList(loanProductEntity, loanOrderEntity));
                // 删除
                productMap.remove(productId);
            }
        }

        /*审核中或放款中且开量产品*/
        status = new int[]{OrderStatus.EXAMINE_WAIT, OrderStatus.WAIT_PAY};
        List<LoanOrderEntity> examineWaitOrWaitPayOrderList = loanOrderDao.findOrderByUserAndStatusIn(userId, status);
        for (LoanOrderEntity loanOrderEntity : examineWaitOrWaitPayOrderList) {
            String productId = loanOrderEntity.getProductId();
            LoanProductEntity loanProductEntity = productMap.get(productId);

            if (loanProductEntity.getIsOpen() == 1) {
                // TODO
                examineWaitOrWaitPayOrderProductList.add(convertToProductList(loanProductEntity, loanOrderEntity));
                // 删除
                productMap.remove(productId);
            }
        }

        /*被拒且开量产品*/
        status = new int[]{OrderStatus.EXAMINE_FAIL};
        List<LoanOrderEntity> examineFailOrderList = loanOrderDao.findOrderByUserAndStatusIn(userId, status);
        for (LoanOrderEntity loanOrderEntity : examineFailOrderList) {
            String productId = loanOrderEntity.getProductId();
            LoanProductEntity loanProductEntity = productMap.get(productId);

            if (loanProductEntity.getIsOpen() == 1) {
                // TODO
                examineFailOrderProductList.add(convertToProductList(loanProductEntity, loanOrderEntity));
                // 删除
                productMap.remove(productId);
            }
        }

        /*挑选关量产品*/
        for (Map.Entry<String, LoanProductEntity> entry : productMap.entrySet()) {
            LoanProductEntity value = entry.getValue();
            if (value.getIsOpen() == 0){
                // TODO
                closeProductList.add(convertToProductList(value, null));

                // 删除
                productMap.remove(entry.getKey());
            }
        }

        /*挑选 新贷&开量产品*/
        for (Map.Entry<String, LoanProductEntity> entry : productMap.entrySet()) {
            LoanProductEntity value = entry.getValue();
            if (value.getIsOpen() == 1){
                // TODO
                newLoanAndOpenProductList.add(convertToProductList(value, null));

                // 删除
                productMap.remove(entry.getKey());
            }
        }

        list.addAll(waitRepaymentOrderProductList);
        list.addAll(reloanOrderProductList);
        list.addAll(examineWaitOrWaitPayOrderProductList);
        list.addAll(examineFailOrderProductList);
        list.addAll(newLoanAndOpenProductList);
        list.addAll(closeProductList);

        // 封装结果
        ProductListResult productListResult = new ProductListResult();
        productListResult.setList(list);
        result.setData(productListResult);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
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
        int singleQuantity = loanOrderDao.countProcessOrderNo(user.getAppName(), userId);
        int allQuantity = loanOrderDao.countProcessOrderNo(null, user.getId());
        bizData.put(Field.SINGLE_QUANTITY, singleQuantity);
        bizData.put(Field.ALL_QUANTITY, allQuantity);
        LoanOrderBillEntity fistRepayOrder = loanOrderBillDao.findFistRepayOrder(userId, user.getAppName());
        if (null != fistRepayOrder) {
            Date actualRepaymentTime = fistRepayOrder.getActualRepaymentTime();
            int intervalDays = DateUtil.getIntervalDays(new Date(), actualRepaymentTime);
            bizData.put(Field.REPAYMENT_TIME, intervalDays);
        }
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
        LogUtil.sysInfo("requestParams: {} result:{}", requestParams, result);

//        if (StringUtils.isEmpty(result)) {
//            return false;
//        }

        // 更新节点响应数据
//        loanOrderExamineDao.updateOrderExamineResponse(loanOrderEntity.getId(), subExpression, result, new Date());

        // 转换为JSON
//        JSONObject resultJson = JSONObject.parseObject(result);
//
//        // 返回码
//        Integer code = resultJson.getInteger(Field.ERROR);
//
//        if (code != 200) {
//            return false;
//        }
//
//        // 成功
//        JSONObject data = resultJson.getJSONObject(Field.DATA);
//
//        // 是否通过
//        int pass = data.getInteger(Field.PASS);
//
//        // 未通过
//        if (pass == 0) {
//            // 更新审核状态
//            loanOrderExamineDao.updateOrderExamineStatus(orderId, subExpression, OrderExamineStatus.REFUSE, new Date());
//            return false;
//        }

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
                // 产品id
                String productId = productEntity.getId();

                // 查询用户是否有已经创建且未完结的订单
                Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE};
                List<LoanOrderEntity> loanOrderEntityList = loanOrderDao.findOrderByUserAndProductIdAndStatus(userId, productId, status);
                if (CollectionUtils.isNotEmpty(loanOrderEntityList)) {
                    return loanOrderEntityList.get(0).getId();
                }


                // 查询用户指定状态订单
                status = new Integer[]{OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
                loanOrderEntityList = loanOrderDao.findOrderByUserAndProductIdAndStatus(userId, productId, status);


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

                // 判断是否新增成功
                if (insertOrder == 0) {
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
            }
        });

        // 判断订单号是否为空
        if (StringUtils.isEmpty(orderId)) {
            return null;
        }

        // 查询订单
        LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
        if (ObjectUtils.isEmpty(loanOrderEntity)) {
            return null;
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
        if (count == 0) {
            return 2;
        }

        // 本包本产品是否有还款
        count = loanOrderDao.countUserOrderByProductAndAppInStatus(userId, productId, appName, status);
        // 无:1客群
        if (count == 0) {
            return 1;
        }

        return 0;
    }
}
