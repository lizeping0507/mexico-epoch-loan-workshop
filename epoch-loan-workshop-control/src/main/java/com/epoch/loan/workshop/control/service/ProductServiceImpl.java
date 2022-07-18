package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.ProductDetailParams;
import com.epoch.loan.workshop.common.params.params.request.ProductRecommendListParams;
import com.epoch.loan.workshop.common.params.params.request.UserTypeParams;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.params.params.result.model.ProductList;
import com.epoch.loan.workshop.common.service.ProductService;
import com.epoch.loan.workshop.common.util.*;
import com.epoch.loan.workshop.common.lock.UserProductDetailLock;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.*;
import java.util.stream.Collectors;

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

        // 产品id
        String productId = params.getProductId();

        // 查询产品详情
        LoanProductEntity loanProductEntity = loanProductDao.findProduct(productId);
        if (ObjectUtils.isEmpty(loanProductEntity)) {
            // 封装结果
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }

        // 产品信息
        resData.setInterestRange(loanProductEntity.getInterestRange());
        resData.setServiceFeeRange(loanProductEntity.getServiceFeeRange());
        resData.setArrivalRange(parseProductConfig(loanProductEntity.getArrivalRange(),1));
        resData.setRepaymentRange(parseProductConfig(loanProductEntity.getRepaymentRange(),1));
        resData.setAmount(parseProductConfig(loanProductEntity.getAmountRange(),1));

        // 用户认证状态
        resData.setIdFlag(params.getUser().isIdentityAuth() ? 1 : 0);
        resData.setBaseInfoFlag(params.getUser().isBasicInfoAuth() ? 1 : 0);
        resData.setAddInfoFlag(params.getUser().isAddInfoAuth() ? 1 : 0);

        // 判断用户认证是否通过
        if (!params.getUser().isIdentityAuth() || !params.getUser().isBasicInfoAuth() || !params.getUser().isAddInfoAuth()) {
            resData.setOrderId("");
            result.setData(resData);
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            return result;
        }

        // app版本
        String appVersion = params.getAppVersion();

        // app名称
        String appName = params.getAppName();

        // 用户id
        String userId = params.getUser().getId();

        // 查询指定状态的订单
        Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.EXAMINE_FAIL, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
        LoanOrderEntity loanOrderEntity = loanOrderDao.findLatelyOrderByUserIdAndProductIdAndStatus(userId, productId, status);

        /* 判断最后一条订单是否不处于结清和被拒（排除这三个状态的订单后，说明这条订单是在途状态） */
        if (ObjectUtils.isNotEmpty(loanOrderEntity) && loanOrderEntity.getStatus() != OrderStatus.DUE_COMPLETE && loanOrderEntity.getStatus() != OrderStatus.COMPLETE && loanOrderEntity.getStatus() != OrderStatus.EXAMINE_FAIL) {
            // 封装结果集
            resData.setOrderId(loanOrderEntity.getId());
            result.setData(resData);
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            return result;
        }

        /*判断最后一条订单是否是被拒订单*/
        if (ObjectUtils.isNotEmpty(loanOrderEntity) && loanOrderEntity.getStatus() == OrderStatus.EXAMINE_FAIL) {
            // 更新时间
            Date updateTime = loanOrderEntity.getUpdateTime();

            // 产品冷却期
            int cdDays = loanProductEntity.getCdDays();

            // 判断是否过了冷却期
            if (!OrderUtils.isCdWithTime(cdDays, updateTime)) {
                // 封装结果
                resData.setOrderId(loanOrderEntity.getId());
                result.setData(resData);
                result.setReturnCode(ResultEnum.COOLING_PERIOD.code());
                result.setMessage(ResultEnum.COOLING_PERIOD.message());
                return result;
            }
        }

        /*多投校验*/
        // 如果订单状态处于创建状态，进行多投判断
        boolean rejectionRule = rejectionRule(productId, params.getUser());

        // 多投被拒返回
        if (!rejectionRule) {
            // 封装结果
            resData.setOrderId("");
            result.setData(resData);
            result.setReturnCode(ResultEnum.DELIVERY_REJECTED_ERROR.code());
            result.setMessage(ResultEnum.DELIVERY_REJECTED_ERROR.message());
            return result;
        }

        /*生成订单*/
        // 订单审核模型
        String orderModelGroup = loanProductEntity.getOrderModelGroup();

        // 初始化订单
        loanOrderEntity = initOrder(params.getUser(), OrderType.LOAN, appVersion, appName, orderModelGroup, loanProductEntity);

        // 订单是否创建成功
        if (ObjectUtils.isEmpty(loanOrderEntity)) {
            // 封装结果
            result.setReturnCode(ResultEnum.SYNCHRONIZATION_ERROR.code());
            result.setMessage(ResultEnum.SYNCHRONIZATION_ERROR.message());
            return result;
        }

        // 封装结果集
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
    public Result<AppMaskModelResult> appMaskModel(BaseParams params) throws Exception {

        // 多推包判断
        if(ObjectUtils.isNotEmpty(params.getAppType()) && params.getAppType().equals(NumberField.NUM_ONE)){
            return mergePushMask(params);
        }

        // 结果集
        Result<AppMaskModelResult> result = new Result<>();
        AppMaskModelResult appMaskModelResult = new AppMaskModelResult();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());

        // 用户id
        String userId = params.getUser().getId();

        // 更新地址
        String gps = params.getGps();
        String gpsAddress = params.getGpsAddress();
        if (StringUtils.isNotEmpty(gps) || StringUtils.isNotEmpty(gpsAddress)){
            loanUserInfoDao.updateUserGpsMsg(userId, gps, gpsAddress, new Date());
            tokenManager.updateUserCache(userId);
        }

        // app名称
        String appName = params.getUser().getAppName();

        // App版本
        String appVersion = params.getAppVersion();

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

        // 查询三项认证是否都通过
        if (!params.getUser().isIdentityAuth() ) {
            // 没有通过 返回结果
            appMaskModelResult.setMaskModel(3);
            appMaskModelResult.setButton(OrderUtils.button(0));
            appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(OrderStatus.CREATE));
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

        // 指定状态的订单最后生成一个订单
        Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.EXAMINE_FAIL, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
        LoanOrderEntity loanOrderEntity = loanOrderDao.findLatelyOrderByUserIdAndStatus(userId, status);
        if (ObjectUtils.isEmpty(loanOrderEntity)) {
            // 查询A阈值的承接盘
            LoanMaskEntity loanMaskEntity = loanMaskDao.findLoanMaskByAppNameAndLevel(appName, "A");

            // 产品id
            String maskProductId = loanMaskEntity.getProductId();

            // 查询承接盘详细信息
            LoanProductEntity maskLoanProductEntity = loanProductDao.findProduct(maskProductId);

            // 生成订单
            loanOrderEntity = initOrder(params.getUser(), OrderType.MASK, appVersion, appName, "MASK", maskLoanProductEntity);

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
            appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(orderStatus));
            appMaskModelResult.setOrderId(loanOrderEntity.getId());
            appMaskModelResult.setOrderStatus(orderStatus);
            result.setData(appMaskModelResult);
            return result;
        }

        // 订单编号
        String orderId = loanOrderEntity.getId();

        // 订单状态
        Integer orderStatus = loanOrderEntity.getStatus();

        // 产品id
        String productId = loanOrderEntity.getProductId();

        // 已申请放款的金额设置申请时间
        if (orderStatus >= OrderStatus.EXAMINE_WAIT){
            appMaskModelResult.setApplyTime(loanOrderEntity.getApplyTime());
        }

        /* 判断订单状态是否已经结清*/
        if (orderStatus == OrderStatus.DUE_COMPLETE || orderStatus == OrderStatus.COMPLETE) {
            // 返回结果
            appMaskModelResult.setMaskModel(1);
            result.setData(appMaskModelResult);
            return result;
        }

        /*判断最后一条订单是否是被拒订单*/
        if (orderStatus == OrderStatus.EXAMINE_FAIL) {
            // 更新时间
            Date updateTime = loanOrderEntity.getUpdateTime();

            // 产品冷却期
            int cdDays = 1;

            // 判断是否过了冷却期
            if (!OrderUtils.isCdWithTime(cdDays, updateTime)) {
                // 未过冷却期订单不允许再次进行申请
                appMaskModelResult.setMaskModel(2);
                appMaskModelResult.setButton(OrderUtils.button(OrderStatus.EXAMINE_FAIL));
                appMaskModelResult.setOrderId(loanOrderEntity.getId());
                appMaskModelResult.setOrderStatus(loanOrderEntity.getStatus());
                appMaskModelResult.setApplyTime(loanOrderEntity.getApplyTime());
                appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(OrderStatus.EXAMINE_FAIL));
                result.setData(appMaskModelResult);
                return result;
            }

            /*冷却期结束生成新的订单*/
            // 查询A阈值的承接盘
            LoanMaskEntity loanMaskEntity = loanMaskDao.findLoanMaskByAppNameAndLevel(appName, "A");

            // 产品id
            String maskProductId = loanMaskEntity.getProductId();

            // 查询承接盘详细信息
            LoanProductEntity maskLoanProductEntity = loanProductDao.findProduct(maskProductId);

            // 生成订单
            loanOrderEntity = initOrder(params.getUser(), OrderType.MASK, appVersion, appName, "MASK", maskLoanProductEntity);

            // 订单是否创建成功
            if (ObjectUtils.isEmpty(loanOrderEntity)) {
                // 封装结果
                result.setReturnCode(ResultEnum.SYNCHRONIZATION_ERROR.code());
                result.setMessage(ResultEnum.SYNCHRONIZATION_ERROR.message());
                return result;
            }

            // 订单状态
            orderStatus = loanOrderEntity.getStatus();

            // 返回结果集
            appMaskModelResult.setMaskModel(0);
            appMaskModelResult.setButton(OrderUtils.button(orderStatus));
            appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(orderStatus));
            appMaskModelResult.setOrderId(loanOrderEntity.getId());
            appMaskModelResult.setOrderStatus(orderStatus);
            result.setData(appMaskModelResult);
            return result;
        }

        /*已经有在途订单*/
        // 查询承接盘
        LoanMaskEntity loanMaskEntity = loanMaskDao.findLoanMaskByAppNameAndProductId(appName, productId);

        // 如果阈值为A在途订单就进入贷超模式
        if (loanMaskEntity.getLevel().equals("A") && orderStatus >= OrderStatus.WAY) {
            // 返回结果
            appMaskModelResult.setMaskModel(1);
            appMaskModelResult.setButton(OrderUtils.button(orderStatus));
            appMaskModelResult.setOrderId(orderId);
            appMaskModelResult.setOrderStatus(orderStatus);
            result.setData(appMaskModelResult);
            return result;
        }

        // 如果订单处于在途或者在途之后的状态那么金额就为预计还款金额
        if (orderStatus >= OrderStatus.WAY) {
            // 查询最早一期没还款的账单
            Integer[] statusArray = {OrderBillStatus.WAY, OrderBillStatus.DUE};
            LoanOrderBillEntity loanOrderBillEntity = loanOrderBillDao.findOrderBillFastStagesByStatusAndOrderId(orderId, statusArray);

            // 还款时间
            appMaskModelResult.setRepaymentTime(loanOrderBillEntity.getRepaymentTime());

            // 应还金额
            appMaskModelResult.setAmount(String.valueOf(loanOrderBillEntity.getRepaymentAmount() - loanOrderBillEntity.getReductionAmount()));
        }else if (orderStatus > OrderStatus.CREATE){
            // 还款时间
            appMaskModelResult.setRepaymentTime(DateUtil.addDay(new Date() , 7));
        }

        // 返回结果
        appMaskModelResult.setMaskModel(0);
        appMaskModelResult.setButton(OrderUtils.button(orderStatus));
        appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(orderStatus));
        appMaskModelResult.setOrderId(orderId);
        appMaskModelResult.setOrderStatus(orderStatus);
        result.setData(appMaskModelResult);
        return result;
    }

    /**
     * 获取用户app模式-多推包
     *
     * @param params 入参
     * @return Result
     * @throws Exception 请求异常
     */
    public Result<AppMaskModelResult> mergePushMask(BaseParams params) throws Exception{
        // 结果集
        Result<AppMaskModelResult> result = new Result<>();
        AppMaskModelResult appMaskModelResult = new AppMaskModelResult();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());

        // 用户id
        String userId = params.getUser().getId();

        // 更新地址
        String gps = params.getGps();
        String gpsAddress = params.getGpsAddress();
        if (StringUtils.isNotEmpty(gps) || StringUtils.isNotEmpty(gpsAddress)){
            loanUserInfoDao.updateUserGpsMsg(userId, gps, gpsAddress, new Date());
            tokenManager.updateUserCache(userId);
        }

        // app名称
        String appName = params.getUser().getAppName();

        // App版本
        String appVersion = params.getAppVersion();

        // 初始化金额
        appMaskModelResult.setAmount("10000");

        // 多推用户模式标识 0-现金贷 1-贷超
        Integer userModelType = 0;

        // 初始额度赋值
        appMaskModelResult.setAvailableCredit(NumberField.NUM_L_TEN_THOUSAND);
        appMaskModelResult.setTotalCredit(NumberField.NUM_L_TEN_THOUSAND);
        appMaskModelResult.setUsedCredit(NumberField.NUM_L_ZERO);
        // 默认无锁定标识
        appMaskModelResult.setLocked(NumberField.NUM_ZERO);
        // 默认不展示引导标识
        appMaskModelResult.setGuideType(NumberField.NUM_ZERO);

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

        // 查询三项认证是否都通过
        if (!params.getUser().isIdentityAuth() ) {
            // 没有通过 返回结果
            appMaskModelResult.setMaskModel(3);
            appMaskModelResult.setButton(OrderUtils.button(0));
            appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(OrderStatus.CREATE));
            result.setData(appMaskModelResult);
            return result;
        }

        // 多推模式，判断用户当前是永久现金贷，还是已转贷超
        Integer userModel = loanUserModelDao.findByUserId(params.getUser().getId());
        userModelType = (userModel == null || ObjectUtils.isEmpty(userModel))? 0 : userModel;

        if (userModelType.equals(NumberField.NUM_ONE)) {
            // 贷超模式已经有订单
            appMaskModelResult.setMaskModel(1);
            result.setData(appMaskModelResult);
            return result;
        }

        // 指定状态的订单最后生成一个订单
        Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.EXAMINE_FAIL, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
        LoanOrderEntity loanOrderEntity = loanOrderDao.findLatelyOrderByUserIdAndStatus(userId, status);
        if (ObjectUtils.isEmpty(loanOrderEntity)) {
            // 查询A阈值的承接盘
            LoanMaskEntity loanMaskEntity = loanMaskDao.findLoanMaskByAppNameAndLevel(appName, "A");

            // 产品id
            String maskProductId = loanMaskEntity.getProductId();

            // 查询承接盘详细信息
            LoanProductEntity maskLoanProductEntity = loanProductDao.findProduct(maskProductId);

            // 生成订单
            loanOrderEntity = initOrder(params.getUser(), OrderType.MASK, appVersion, appName, "MASK", maskLoanProductEntity);

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
            appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(orderStatus));
            appMaskModelResult.setOrderId(loanOrderEntity.getId());
            appMaskModelResult.setOrderStatus(orderStatus);
            result.setData(appMaskModelResult);
            return result;
        }

        // 订单编号
        String orderId = loanOrderEntity.getId();

        // 订单状态
        Integer orderStatus = loanOrderEntity.getStatus();

        // 产品id
        String productId = loanOrderEntity.getProductId();

        // 已申请放款的金额设置申请时间
        if (orderStatus >= OrderStatus.EXAMINE_WAIT){
            appMaskModelResult.setApplyTime(loanOrderEntity.getApplyTime());
        }

        /* 判断订单状态是否已经结清*/
        if (orderStatus == OrderStatus.DUE_COMPLETE || orderStatus == OrderStatus.COMPLETE) {
            // 返回结果
            appMaskModelResult.setMaskModel(1);
            result.setData(appMaskModelResult);
            return result;
        }

        /*判断最后一条订单是否是被拒订单*/
        if (orderStatus == OrderStatus.EXAMINE_FAIL) {
            // 更新时间
            Date updateTime = loanOrderEntity.getUpdateTime();

            // 产品冷却期
            int cdDays = 1;

            // 判断是否过了冷却期
            if (!OrderUtils.isCdWithTime(cdDays, updateTime)) {
                // 未过冷却期订单不允许再次进行申请
                appMaskModelResult.setMaskModel(2);
                appMaskModelResult.setButton(OrderUtils.button(OrderStatus.EXAMINE_FAIL));
                appMaskModelResult.setOrderId(loanOrderEntity.getId());
                appMaskModelResult.setOrderStatus(loanOrderEntity.getStatus());
                appMaskModelResult.setApplyTime(loanOrderEntity.getApplyTime());
                appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(OrderStatus.EXAMINE_FAIL));
                result.setData(appMaskModelResult);
                return result;
            }

            /*冷却期结束生成新的订单*/
            // 查询A阈值的承接盘
            LoanMaskEntity loanMaskEntity = loanMaskDao.findLoanMaskByAppNameAndLevel(appName, "A");

            // 产品id
            String maskProductId = loanMaskEntity.getProductId();

            // 查询承接盘详细信息
            LoanProductEntity maskLoanProductEntity = loanProductDao.findProduct(maskProductId);

            // 生成订单
            loanOrderEntity = initOrder(params.getUser(), OrderType.MASK, appVersion, appName, "MASK", maskLoanProductEntity);

            // 订单是否创建成功
            if (ObjectUtils.isEmpty(loanOrderEntity)) {
                // 封装结果
                result.setReturnCode(ResultEnum.SYNCHRONIZATION_ERROR.code());
                result.setMessage(ResultEnum.SYNCHRONIZATION_ERROR.message());
                return result;
            }

            // 订单状态
            orderStatus = loanOrderEntity.getStatus();

            // 返回结果集
            appMaskModelResult.setMaskModel(0);
            appMaskModelResult.setButton(OrderUtils.button(orderStatus));
            appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(orderStatus));
            appMaskModelResult.setOrderId(loanOrderEntity.getId());
            appMaskModelResult.setOrderStatus(orderStatus);
            result.setData(appMaskModelResult);
            return result;
        }

        /*已经有在途订单*/

        // 查看缓存，判断订单号是否已生成
        Object  json = redisClient.get("ydplatform:app-api:mergePush:templateOrder:" + params.getUser().getId());

        // 如果上一笔已还清，则生成一笔虚拟单
        if(ObjectUtils.isEmpty(json) && orderStatus.equals(OrderStatus.COMPLETE) && userModelType.equals(NumberField.NUM_ZERO)){

            // 生成订单号
            String virtulOrderNo = ObjectIdUtil.getObjectId();;

            // 存储
            redisClient.set("ydplatform:app-api:mergePush:templateOrder:" + params.getUser().getId(),virtulOrderNo);

            // 赋值虚拟单号、状态
            orderId = virtulOrderNo;
            orderStatus = OrderStatus.CREATE;

        }

        if(ObjectUtils.isNotEmpty(json)){
            orderId = (String) json;
            orderStatus = OrderStatus.CREATE;
        }


        // 如果订单处于在途或者在途之后的状态那么金额就为预计还款金额
        if (orderStatus >= OrderStatus.WAY) {
            // 查询最早一期没还款的账单
            Integer[] statusArray = {OrderBillStatus.WAY, OrderBillStatus.DUE};
            LoanOrderBillEntity loanOrderBillEntity = loanOrderBillDao.findOrderBillFastStagesByStatusAndOrderId(orderId, statusArray);

            // 还款时间
            appMaskModelResult.setRepaymentTime(loanOrderBillEntity.getRepaymentTime());

            // 应还金额
            appMaskModelResult.setAmount(String.valueOf(loanOrderBillEntity.getRepaymentAmount() - loanOrderBillEntity.getReductionAmount()));
        }else if (orderStatus > OrderStatus.CREATE){
            // 还款时间
            appMaskModelResult.setRepaymentTime(DateUtil.addDay(new Date() , 7));

            // 状态赋值
            if(orderStatus.equals(OrderStatus.EXAMINE_WAIT)){
                appMaskModelResult.setLocked(NumberField.NUM_ONE);
                appMaskModelResult.setTotalCredit(loanOrderEntity.getApprovalAmount().longValue());
                appMaskModelResult.setAvailableCredit(loanOrderEntity.getApprovalAmount().longValue());
            }

        }

        // 返回结果
        appMaskModelResult.setMaskModel(0);
        appMaskModelResult.setButton(OrderUtils.button(orderStatus));
        appMaskModelResult.setStatusDescription(OrderUtils.statusDescription(orderStatus));
        appMaskModelResult.setOrderId(orderId);
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
    public Result<ProductListResult> productList(BaseParams params) throws Exception {
        Result<ProductListResult> result = new Result<>();

        // 产品列表
        List<ProductList> list = new ArrayList<>();

        // 待还款订单产品
        List<ProductList> waitRepaymentOrderProductList = new ArrayList<>();

        // 可续贷且开量产品
        List<ProductList> reloanOrderProductList = new ArrayList<>();

        // 审核中或放款中 且开量产品
        List<ProductList> examineWaitOrWaitPayOrderProductList = new ArrayList<>();

        // 审核通过 开量产品
        List<ProductList> newCreateProductList = new ArrayList<>();

        // 审核通过 开量产品
        List<ProductList> examinePassOrderProductList = new ArrayList<>();

        // 被拒产品
        List<ProductList> examineFailOrderProductList = new ArrayList<>();

        // 新贷且开量产品
        List<ProductList> newLoanAndOpenProductList = new ArrayList<>();

        // 关量产品
        List<ProductList> closeProductList = new ArrayList<>();

        // 用户id
        String userId = params.getUser().getId();

        // 是否有过放款成功
        int[] statues = {OrderStatus.WAY,OrderStatus.DUE,OrderStatus.COMPLETE,OrderStatus.DUE_COMPLETE};
        Integer integer = loanOrderDao.countUserOrderByStatusIn(userId, statues);
        boolean hasPaymentOrder = integer > 0;

        // 查询所有产品
        List<LoanProductEntity> products = loanProductDao.findAll();
        Map<String, LoanProductEntity> productMap = new HashMap<>();
        products.forEach(product -> {
            if (product.getStatus() == 1){
                productMap.put(product.getId(), product);
            }
        });

        // 查询其他包承接盘
        List<LoanMaskEntity> otherLoanMaskList = loanMaskDao.findLoanMaskByAppNameIsNot(params.getAppName());

        // 过滤它包承接盘产品
        otherLoanMaskList.parallelStream().forEach(loanMask -> productMap.remove(loanMask.getProductId()));

        Integer[] processStatus = {
                OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS,
                OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.DUE
        };
        List<LoanOrderEntity> allProcessOrderList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, processStatus, OrderByField.CREATE_TIME, OrderByField.ASC);
        for (LoanOrderEntity loanOrderEntity : allProcessOrderList) {
            Integer orderStatus = loanOrderEntity.getStatus();
            String productId = loanOrderEntity.getProductId();
            LoanProductEntity loanProductEntity = productMap.get(productId);

            // 跳过不存在产品 以及未开量产品
            if (ObjectUtils.isEmpty(loanProductEntity) || loanProductEntity.getIsOpen() == 0) {
                continue;
            }

            // 封装
            ProductList productList = new ProductList();
            BeansUtil.copyProperties(loanProductEntity,productList);
            productList.setAmountRange(parseProductConfig(loanProductEntity.getAmountRange(),1));
            productList.setInterest(loanProductEntity.getInterest() + "%/Dia");
            productList.setPassRate("");
            productList.setButton(OrderUtils.button(loanOrderEntity.getStatus()));
            productList.setOrderStatus(loanOrderEntity.getStatus());
            productList.setOrderNo(loanOrderEntity.getId());

            // 通过订单状态加入对应列表
            switch (orderStatus){
                case OrderStatus.WAY:
                case OrderStatus.DUE:
                    // 待还款
                    waitRepaymentOrderProductList.add(productList);
                    break;
                case OrderStatus.EXAMINE_PASS:
                    // 审核通过的产品
                    examinePassOrderProductList.add(productList);
                    break;
                case OrderStatus.EXAMINE_WAIT:
                case OrderStatus.WAIT_PAY:
                    // 审核中或放款中且开量产品
                    examineWaitOrWaitPayOrderProductList.add(productList);
                    break;
                case OrderStatus.CREATE:
                    // 新建且开量产品
                    // 如果用户在本包没有放款成功记录 不展示通过率
                    if(hasPaymentOrder){
                        productList.setPassRate(loanProductEntity.getPassRate().toString());
                    }
                    newCreateProductList.add(productList);
                    break;
                default:
                    break;
            }

            // 移除
            productMap.remove(productId);
        }

        LogUtil.sysInfo("productMap : {}", JSONObject.toJSONString(productMap));

        // 查询无订单产品
        List<LoanProductEntity> withoutUserOrderProductList = loanProductDao.findProductWithoutUserOrder(userId);
        LogUtil.sysInfo("withoutUserOrderProductList : {}", JSONObject.toJSONString(withoutUserOrderProductList));
        for (LoanProductEntity loanProduct : withoutUserOrderProductList) {
            String productId = loanProduct.getId();
            LoanProductEntity loanProductEntity = productMap.get(productId);
            if (ObjectUtils.isEmpty(loanProductEntity)){
                continue;
            }

            // 封装
            ProductList productList = new ProductList();
            BeansUtil.copyProperties(loanProductEntity,productList);
            productList.setAmountRange(parseProductConfig(loanProductEntity.getAmountRange(),1));
            productList.setInterest(loanProduct.getInterest() + "%/Dia");


            // 新贷开量产品
            if (loanProductEntity.getIsOpen() == 1){
                // 如果用户在本包没有放款成功记录 不展示通过率
                if(!hasPaymentOrder){
                    productList.setPassRate("");
                }
                productList.setButton(OrderUtils.button(0));
                newLoanAndOpenProductList.add(productList);
                // 移除
                productMap.remove(productId);
                continue;
            }

            // 关量产品
            if (loanProductEntity.getIsOpen() == 0){
                productList.setPassRate("");
                productList.setInterest(loanProductEntity.getInterest() + "%/Dia");
                productList.setButton("Full");
                productList.setOrderStatus(OrderStatus.CREATE);
                closeProductList.add(productList);
                // 移除
                productMap.remove(productId);
                continue;
            }

        }
        LogUtil.sysInfo("productMap : {}", JSONObject.toJSONString(productMap));

        // 查询剩余产品 最后一笔订单
        for (Map.Entry<String, LoanProductEntity> entry : productMap.entrySet()) {
            LoanProductEntity productEntity = entry.getValue();
            String productId = productEntity.getId();
            LogUtil.sysInfo("productEntity : {}", JSONObject.toJSONString(productEntity));

            // 查询用户在该产品最后一笔订单
            LoanOrderEntity lastOrder = loanOrderDao.findUserLastOrderWithProduct(userId, productId);
            Integer status = lastOrder.getStatus();

            // 封装
            ProductList productList = new ProductList();
            BeansUtil.copyProperties(productEntity, productList);
            productList.setAmountRange(parseProductConfig(productEntity.getAmountRange(),1));
            productList.setInterest(productEntity.getInterest() + "%/Dia");

            // 续贷 必定展示通过率
            if (status == OrderStatus.COMPLETE || status == OrderStatus.DUE_COMPLETE) {
                productList.setButton("Aplicar de nuevo");
                reloanOrderProductList.add(productList);
                continue;
            }

            // 被拒
            if (status == OrderStatus.EXAMINE_FAIL) {
                Integer cdDays = productEntity.getCdDays();
                Date updateTime = lastOrder.getUpdateTime();

                // 已过冷却期
                if (OrderUtils.isCdWithTime(cdDays, updateTime)) {
                    // 如果用户在本包没有放款成功记录 不展示通过率
                    if(!hasPaymentOrder){
                        productList.setPassRate("");
                    }
                    productList.setButton(OrderUtils.button(0));
                    productList.setOrderStatus(OrderStatus.CREATE);
                    newCreateProductList.add(productList);
                    continue;
                }

                // 未过冷却期
                productList.setPassRate("");
                productList.setButton(OrderUtils.button(OrderStatus.EXAMINE_FAIL));
                productList.setOrderStatus(OrderStatus.EXAMINE_FAIL);
                examineFailOrderProductList.add(productList);
                continue;
            }

            // 剩余产品认为是关量产品
            productList.setPassRate("");
            productList.setButton("Full");
            productList.setOrderStatus(OrderStatus.CREATE);
            closeProductList.add(productList);
        }

        // 组装列表
        LogUtil.sysInfo("list 1:  {}", list);
        list.addAll(waitRepaymentOrderProductList);
        list.addAll(reloanOrderProductList);
        list.addAll(examinePassOrderProductList);
        list.addAll(newCreateProductList);
        list.addAll(newLoanAndOpenProductList);
        list.addAll(examineWaitOrWaitPayOrderProductList);
        list.addAll(closeProductList);
        list.addAll(examineFailOrderProductList);
        LogUtil.sysInfo("list 2:  {}", list);

        LogUtil.sysInfo("waitRepaymentOrderProductList:  {}", waitRepaymentOrderProductList);
        LogUtil.sysInfo("reloanOrderProductList:  {}", reloanOrderProductList);
        LogUtil.sysInfo("examinePassOrderProductList:  {}", examinePassOrderProductList);
        LogUtil.sysInfo("newCreateProductList:  {}", newCreateProductList);
        LogUtil.sysInfo("newLoanAndOpenProductList:  {}", newLoanAndOpenProductList);
        LogUtil.sysInfo("examineWaitOrWaitPayOrderProductList:  {}", examineWaitOrWaitPayOrderProductList);
        LogUtil.sysInfo("closeProductList:  {}", closeProductList);
        LogUtil.sysInfo("examineFailOrderProductList:  {}", examineFailOrderProductList);

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

        // 获取参数
        String userId = params.getUser().getId();
        String currentProductId = params.getProductId();

        // 可续贷且开量产品
        List<ProductList> reloanOrderProductList = new ArrayList<>();

        // 新贷且开量产品
        List<ProductList> newLoanAndOpenProductList = new ArrayList<>();

        // 查询所有产品
        List<LoanProductEntity> products = loanProductDao.findAll();

        // 排除下架产品/关量产品/当前产品
        Map<String, LoanProductEntity> productMap = new HashMap<>();
        products.forEach(product -> {
            if (product.getStatus() == 1 && product.getIsOpen() == 1 && !product.getId().equals(currentProductId)){
                productMap.put(product.getId(), product);
            }
        });

        // 排除其他包承接盘产品
        List<LoanMaskEntity> otherLoanMaskList = loanMaskDao.findLoanMaskByAppNameIsNot(params.getAppName());
        otherLoanMaskList.parallelStream().forEach(loanMask -> productMap.remove(loanMask.getProductId()));

        // 排除有进行中订单产品
        Integer[] status = {OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.DUE};
        List<LoanOrderEntity> allProcessOrderList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.CREATE_TIME, OrderByField.ASC);
        for (LoanOrderEntity loanOrderEntity : allProcessOrderList) {
            String productId = loanOrderEntity.getProductId();
            productMap.remove(productId);
        }

        // 查询无订单产品 (即 新贷产品) 并排除
        List<LoanProductEntity> withoutUserOrderProductList = loanProductDao.findProductWithoutUserOrder(userId);
        for (LoanProductEntity loanProduct : withoutUserOrderProductList) {
            String productId = loanProduct.getId();
            LoanProductEntity loanProductEntity = productMap.get(productId);
            if (ObjectUtils.isEmpty(loanProductEntity)) {
                continue;
            }

            // 封装
            ProductList productList = new ProductList();
            BeansUtil.copyProperties(loanProductEntity, productList);
            productList.setAmountRange(parseProductConfig(loanProductEntity.getAmountRange(),1));
            productList.setInterest(loanProductEntity.getInterest() + "%/Dia");
            productList.setButton(OrderUtils.button(0));
            newLoanAndOpenProductList.add(productList);
            // 移除
            productMap.remove(productId);
        }

        // 遍历剩余产品 判断续贷产品
        for (String productId : productMap.keySet()) {
            LoanProductEntity productEntity = productMap.get(productId);
            // 查询用户在该产品最后一笔订单
            LoanOrderEntity lastOrder = loanOrderDao.findUserLastOrderWithProduct(userId, productId);
            Integer orderStatus = lastOrder.getStatus();

            // 续贷
            if (orderStatus == OrderStatus.COMPLETE || orderStatus == OrderStatus.DUE_COMPLETE) {
                // 封装
                ProductList productList = new ProductList();
                BeansUtil.copyProperties(productEntity, productList);
                productList.setAmountRange(parseProductConfig(productEntity.getAmountRange(),1));
                productList.setInterest(productEntity.getInterest() + "%/Dia");
                productList.setButton("Aplicar de nuevo");
                reloanOrderProductList.add(productList);
                continue;
            }
        }

        // 组装完整列表
        List<ProductList> list = new ArrayList<>();
        list.addAll(reloanOrderProductList);
        list.addAll(newLoanAndOpenProductList);

        // 封装结果
        ProductRecommendResult data = new ProductRecommendResult();
        data.setList(list);
        result.setData(data);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 变身贷超
     *
     * @param params 入参
     * @return 产品集合
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> turnIntoLoan(BaseParams params) throws Exception{
        // 返回集
        Result<Object> response = new Result<>();

        // 保存用户标识
        LoanUserModelEntity userModel = new LoanUserModelEntity();
        userModel.setUserId(Long.getLong(params.getUser().getId()));
        userModel.setModleTag(NumberField.NUM_ONE);
        userModel.setCreateTime(new Date());
        userModel.setAppName(params.getAppName());

        Integer I = loanUserModelDao.addLoanUserModel(userModel);

        if(I.equals(NumberField.NUM_ONE)){
            response.setReturnCode(200);
            response.setMessage("success");
        }else {
            response.setReturnCode(400);
            response.setMessage("error");
        }

        return response;
    }

    /**
     * 贷超多推
     * @param params 入参
     * @return Result<MergePushLoanResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<MergePushLoanResult> mergePushLoan(BaseParams params) throws Exception{
        // 返回集
        Result<MergePushLoanResult> response = new Result<MergePushLoanResult>();
        MergePushLoanResult result = new MergePushLoanResult();

        // 用户id
        String userId = params.getUser().getId();

        // 更新地址
        String gps = params.getGps();
        String gpsAddress = params.getGpsAddress();
        if (StringUtils.isNotEmpty(gps) || StringUtils.isNotEmpty(gpsAddress)){
            loanUserInfoDao.updateUserGpsMsg(userId, gps, gpsAddress, new Date());
            tokenManager.updateUserCache(userId);
        }

        // app名称
        String appName = params.getUser().getAppName();

        // App版本
        String appVersion = params.getAppVersion();

        // 获取风控返回可借贷笔数及客群额度
        Integer canBorrowNum = mergeRejectionRule(params.getUser());


        // 查看缓存，判断订单号是否已生成
        Object  orderNo = redisClient.get("ydplatform:app-api:mergePush:shopTemplateOrder:" + userId);

        if(ObjectUtils.isEmpty(orderNo)){
            // 生成订单号
            orderNo = ObjectIdUtil.getObjectId();
            // 存储
            redisClient.set("ydplatform:app-api:mergePush:shopTemplateOrder:" + userId,orderNo);
        }

        // 产品列表
        List<ProductList> list = new ArrayList<>();

        // 待还款订单产品
        List<ProductList> waitRepaymentOrderProductList = new ArrayList<>();

        // 可续贷且开量产品
        List<ProductList> reloanOrderProductList = new ArrayList<>();

        // 审核中或放款中 且开量产品
        List<ProductList> examineWaitOrWaitPayOrderProductList = new ArrayList<>();

        // 审核通过 开量产品
        List<ProductList> newCreateProductList = new ArrayList<>();

        // 审核通过 开量产品
        List<ProductList> examinePassOrderProductList = new ArrayList<>();

        // 被拒产品
        List<ProductList> examineFailOrderProductList = new ArrayList<>();

        // 新贷且开量产品
        List<ProductList> newLoanAndOpenProductList = new ArrayList<>();

        // 关量产品
        List<ProductList> closeProductList = new ArrayList<>();

        // 是否有过放款成功
        int[] statues = {OrderStatus.WAY,OrderStatus.DUE,OrderStatus.COMPLETE,OrderStatus.DUE_COMPLETE};
        Integer integer = loanOrderDao.countUserOrderByStatusIn(userId, statues);
        boolean hasPaymentOrder = integer > 0;

        // 查询所有产品
        List<LoanProductEntity> products = loanProductDao.findAll();
        Map<String, LoanProductEntity> productMap = new HashMap<>();
        products.forEach(product -> {
            if (product.getStatus() == 1){
                productMap.put(product.getId(), product);
            }
        });

        // 查询其他包承接盘
        List<LoanMaskEntity> otherLoanMaskList = loanMaskDao.findLoanMaskByAppNameIsNot(params.getAppName());

        // 过滤它包承接盘产品
        otherLoanMaskList.parallelStream().forEach(loanMask -> productMap.remove(loanMask.getProductId()));

        Integer[] processStatus = {
                OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS,
                OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.DUE
        };
        List<LoanOrderEntity> allProcessOrderList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, processStatus, OrderByField.CREATE_TIME, OrderByField.ASC);
        for (LoanOrderEntity loanOrderEntity : allProcessOrderList) {
            Integer orderStatus = loanOrderEntity.getStatus();
            String productId = loanOrderEntity.getProductId();
            LoanProductEntity loanProductEntity = productMap.get(productId);

            // 跳过不存在产品 以及未开量产品
            if (ObjectUtils.isEmpty(loanProductEntity) || loanProductEntity.getIsOpen() == 0) {
                continue;
            }

            // 封装
            ProductList productList = new ProductList();
            BeansUtil.copyProperties(loanProductEntity,productList);
            productList.setAmountRange(parseProductConfig(loanProductEntity.getAmountRange(),1));
            productList.setInterest(loanProductEntity.getInterest() + "%/Dia");
            productList.setPassRate("");
            productList.setButton(OrderUtils.button(loanOrderEntity.getStatus()));
            productList.setOrderStatus(loanOrderEntity.getStatus());
            productList.setOrderNo(loanOrderEntity.getId());

            // 通过订单状态加入对应列表
            switch (orderStatus){
                case OrderStatus.WAY:
                case OrderStatus.DUE:
                    // 待还款
                    waitRepaymentOrderProductList.add(productList);
                    break;
                case OrderStatus.EXAMINE_PASS:
                    // 审核通过的产品
                    examinePassOrderProductList.add(productList);
                    break;
                case OrderStatus.EXAMINE_WAIT:
                case OrderStatus.WAIT_PAY:
                    // 审核中或放款中且开量产品
                    examineWaitOrWaitPayOrderProductList.add(productList);
                    break;
                case OrderStatus.CREATE:
                    // 新建且开量产品
                    // 如果用户在本包没有放款成功记录 不展示通过率
                    if(hasPaymentOrder){
                        productList.setPassRate(loanProductEntity.getPassRate().toString());
                    }
                    newCreateProductList.add(productList);
                    break;
                default:
                    break;
            }

            // 移除
            productMap.remove(productId);
        }

        LogUtil.sysInfo("productMap : {}", JSONObject.toJSONString(productMap));

        // 查询无订单产品
        List<LoanProductEntity> withoutUserOrderProductList = loanProductDao.findProductWithoutUserOrder(userId);
        LogUtil.sysInfo("withoutUserOrderProductList : {}", JSONObject.toJSONString(withoutUserOrderProductList));
        for (LoanProductEntity loanProduct : withoutUserOrderProductList) {
            String productId = loanProduct.getId();
            LoanProductEntity loanProductEntity = productMap.get(productId);
            if (ObjectUtils.isEmpty(loanProductEntity)){
                continue;
            }

            // 封装
            ProductList productList = new ProductList();
            BeansUtil.copyProperties(loanProductEntity,productList);
            productList.setAmountRange(parseProductConfig(loanProductEntity.getAmountRange(),1));
            productList.setInterest(loanProduct.getInterest() + "%/Dia");


            // 新贷开量产品
            if (loanProductEntity.getIsOpen() == 1){
                // 如果用户在本包没有放款成功记录 不展示通过率
                if(!hasPaymentOrder){
                    productList.setPassRate("");
                }
                productList.setButton(OrderUtils.button(0));
                newLoanAndOpenProductList.add(productList);
                // 移除
                productMap.remove(productId);
                continue;
            }

            // 关量产品
            if (loanProductEntity.getIsOpen() == 0){
                productList.setPassRate("");
                productList.setInterest(loanProductEntity.getInterest() + "%/Dia");
                productList.setButton("Full");
                productList.setOrderStatus(OrderStatus.CREATE);
                closeProductList.add(productList);
                // 移除
                productMap.remove(productId);
                continue;
            }

        }
        LogUtil.sysInfo("productMap : {}", JSONObject.toJSONString(productMap));

        // 查询剩余产品 最后一笔订单
        for (Map.Entry<String, LoanProductEntity> entry : productMap.entrySet()) {
            LoanProductEntity productEntity = entry.getValue();
            String productId = productEntity.getId();
            LogUtil.sysInfo("productEntity : {}", JSONObject.toJSONString(productEntity));

            // 查询用户在该产品最后一笔订单
            LoanOrderEntity lastOrder = loanOrderDao.findUserLastOrderWithProduct(userId, productId);
            Integer status = lastOrder.getStatus();

            // 封装
            ProductList productList = new ProductList();
            BeansUtil.copyProperties(productEntity, productList);
            productList.setAmountRange(parseProductConfig(productEntity.getAmountRange(),1));
            productList.setInterest(productEntity.getInterest() + "%/Dia");

            // 续贷 必定展示通过率
            if (status == OrderStatus.COMPLETE || status == OrderStatus.DUE_COMPLETE) {
                productList.setButton("Aplicar de nuevo");
                reloanOrderProductList.add(productList);
                continue;
            }

            // 被拒
            if (status == OrderStatus.EXAMINE_FAIL) {
                Integer cdDays = productEntity.getCdDays();
                Date updateTime = lastOrder.getUpdateTime();

                // 已过冷却期
                if (OrderUtils.isCdWithTime(cdDays, updateTime)) {
                    // 如果用户在本包没有放款成功记录 不展示通过率
                    if(!hasPaymentOrder){
                        productList.setPassRate("");
                    }
                    productList.setButton(OrderUtils.button(0));
                    productList.setOrderStatus(OrderStatus.CREATE);
                    newCreateProductList.add(productList);
                    continue;
                }

                // 未过冷却期
                productList.setPassRate("");
                productList.setButton(OrderUtils.button(OrderStatus.EXAMINE_FAIL));
                productList.setOrderStatus(OrderStatus.EXAMINE_FAIL);
                examineFailOrderProductList.add(productList);
                continue;
            }

            // 剩余产品认为是关量产品
            productList.setPassRate("");
            productList.setButton("Full");
            productList.setOrderStatus(OrderStatus.CREATE);
            closeProductList.add(productList);
        }


        // 产品数量
        Integer productAllNum = list.size();

        // 可续贷且开亮产品数量
        Integer reLoanNum = reloanOrderProductList.size();

        // 风控返回可借额度小于可复贷产品
        if(canBorrowNum <= reLoanNum){
            canBorrowNum = reLoanNum;
        }

        // 可用金额
        Long availableCredit = 0L;

        // 总申请额度 = 已用额度 + 审批额度
        Long totalCredit = 0L;

        // 已用额度：未还款订单本金总计
        Long usedCredit = 0L;

        // 审批金额：未放款已生成订单总计
        Long pendingCredit = 0L;

        // 额度锁定
        Integer locked = 0;

        // 按钮状态
        String buttonStatus = "Apply Immediately";

        // 按钮状态标识
        Integer buttonStatusIndentify = 0;

        // 查询用户审批中的订单
        Integer[] pendingStatus = new Integer[]{OrderStatus.EXAMINE_WAIT};
        List<LoanOrderEntity> orderPendingList = loanOrderDao.findOrderByUserIdAndStatus(userId,pendingStatus);

        // 查询用户放款订单
        Integer[] waitAndloanStatus = new Integer[]{OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE};
        List<LoanOrderEntity> orderLoanList = loanOrderDao.findOrderByUserIdAndStatus(userId,waitAndloanStatus);

        // 查询用户待放款单
        Integer[] waitLoanStatus = new Integer[]{OrderStatus.WAIT_PAY};
        List<LoanOrderEntity> pendingOrders = loanOrderDao.findOrderByUserIdAndStatus(userId,waitLoanStatus);

        // 查询已放款订单
        Integer[] loanStatus = new Integer[]{OrderStatus.WAY, OrderStatus.DUE};
        List<LoanOrderEntity> loandOrders = loanOrderDao.findOrderByUserIdAndStatus(userId,loanStatus);


        // 已用额度：未还款订单本金总计
        Double usedCreditBD = orderLoanList.stream().collect(Collectors.summingDouble(x -> ((Double) x.get("approvalAmount"))));
        usedCredit = usedCreditBD.longValue();

        // 审批金额：未放款已生成订单总计
        Double pendingCreditBD = orderPendingList.stream().collect(Collectors.summingDouble(x -> ((Double) x.get("approvalAmount"))));

        pendingCredit = pendingCreditBD.longValue();

        totalCredit = usedCredit + pendingCredit;

        // 可借贷产品list
//        List<MergePushProductListDTO> productCanLoanList = new ArrayList<>();
        List<ProductList> productCanLoanList = new ArrayList<>();


        if(canBorrowNum == 0){

            // 无可借产品，加锁，申请额度=总额度*1.2
            locked = 1;
            availableCredit = new Double(totalCredit * 1.2).longValue();

            // 无可借产品额度，有订单在审批中，首页展示：审批中Button
            if(!NumberField.NUM_ZERO.equals(orderPendingList.size())){
                buttonStatus = "Evaluating";
                buttonStatusIndentify = 1;
            }

            // 无可借产品额度，无已放款订单，有放款中订单，首页展示：待放款Button
            if(!NumberField.NUM_ZERO.equals(pendingOrders.size())){
                buttonStatus = "Disbursing";
                buttonStatusIndentify =3;
            }

            // 无可借产品额度，无审批中订单，有已放款单，首页展示：去还款Button
            if(!NumberField.NUM_ZERO.equals(loandOrders.size())){
                buttonStatus = "Repayment";
                buttonStatusIndentify = 2;
            }


        }else {

            // 有可借产品，结算可借额度

            for (int i = 0; i < canBorrowNum; i++) {

                if(i < productAllNum){
//                    productCanLoanList.add(productAllList.get(i));
                    productCanLoanList.add(list.get(i));
                }

            }

//            availableCredit = productCanLoanList.stream().mapToLong(MergePushProductListDTO::getApprovalAmount).sum();

            totalCredit = availableCredit + usedCredit.intValue();

        }

        // 最终可借贷笔数等于可展示产品数
        canBorrowNum = productCanLoanList.size();


        // 再次对可借贷产品数进行判断，以防用户有可借贷额度，但是无产品可借
        if(canBorrowNum == 0){

            // 无可借产品，加锁，申请额度=总额度*1.2
            locked = 1;
            availableCredit = new Double(totalCredit * 1.2).longValue();

            // 无可借产品额度，有订单在审批中，首页展示：审批中Button
            if(!NumberField.NUM_ZERO.equals(orderPendingList.size())){
                buttonStatus = "Evaluating";
                buttonStatusIndentify = 1;
            }

            // 无可借产品额度，无已放款订单，有放款中订单，首页展示：待放款Button
            if(!NumberField.NUM_ZERO.equals(pendingOrders.size())){
                buttonStatus = "Disbursing";
                buttonStatusIndentify =3;
            }

            // 无可借产品额度，无审批中订单，有已放款单，首页展示：去还款Button
            if(!NumberField.NUM_ZERO.equals(loandOrders.size())){
                buttonStatus = "Repayment";
                buttonStatusIndentify = 2;
            }

        }

        // 查询已放款订单(待还款)
        List<LoanOrderEntity> repaymentList = loanOrderDao.findOrderByUserIdAndStatus(userId,loanStatus);

        result.setUserId(params.getUser().getId());
        result.setAvailableCredit(availableCredit.longValue());
        result.setTotalCredit(new Long (totalCredit));
        result.setUsedCredit(usedCredit);
        result.setButtonStatus(buttonStatus);
        result.setButtonStatusIndentify(buttonStatusIndentify);
        result.setLocked(locked);
        result.setOrderNo((String) orderNo); // 虚拟订单号
        result.setCanLoanNum(canBorrowNum + "products");
        result.setProductList(productCanLoanList);
        result.setRepaymentNum(repaymentList.size());
        response.setData(result);

        response.setReturnCode(200);
        response.setMessage("success");
        response.setData(result);
        return response;
    }

    /**
     * 多投限制
     *
     * @param productId
     * @param user
     * @return
     * @throws Exception
     */
    protected boolean rejectionRule(String productId, User user) throws Exception {
        // 用户id
        String userId = user.getId();

        // 注册地址
        String registerAddress = user.getRegisterAddress();

        // app名称
        String appName = user.getAppName();

        // 手机哈
        String mobile = user.getMobile();

        // 渠道ID
        Integer userChannelId = user.getChannelId();

        // 查询渠道信息
        LoanChannelEntity platformChannelEntity = platformChannelDao.findChannel(userChannelId);

        // 渠道名称
        String channelName = platformChannelEntity.getChannelName();

        List<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        // 单包在贷笔数
        int singleQuantity = loanOrderDao.countProcessOrderNo(userIdList);

        // 多包在贷笔数
        userIdList = loanUserInfoDao.findUserIdByMobile(mobile);
        int allQuantity = loanOrderDao.countProcessOrderNo(userIdList);

        // 第一笔还款距今天数
        LoanOrderBillEntity fistRepayOrder = loanOrderBillDao.findFistRepayOrder(userId, user.getAppName());
        int intervalDays = 0;
        if (ObjectUtils.isNotEmpty(fistRepayOrder)) {
            Date actualRepaymentTime = fistRepayOrder.getActualRepaymentTime();
            intervalDays = DateUtil.getIntervalDays(new Date(), actualRepaymentTime);
        }

        // 创建审核记录
        String orderExamineId = ObjectIdUtil.getObjectId();
        LoanOrderExamineEntity loanOrderExamineEntity = new LoanOrderExamineEntity();
        loanOrderExamineEntity.setOrderId("");
        loanOrderExamineEntity.setUserId(userId);
        loanOrderExamineEntity.setId(orderExamineId);
        loanOrderExamineEntity.setStatus(OrderExamineStatus.CREATE);
        loanOrderExamineEntity.setModelName("RejectionRule");
        loanOrderExamineEntity.setUpdateTime(new Date());
        loanOrderExamineEntity.setCreateTime(new Date());
        loanOrderExamineDao.insertOrderExamine(loanOrderExamineEntity);

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
        bizData.put(Field.BORROW_ID, "");
        bizData.put(Field.PRODUCT_ID, productId);
        bizData.put(Field.PROGRESS, 0);
        bizData.put(Field.REGISTER_ADDR, registerAddress);
        bizData.put(Field.CHANNEL_NAME, channelName);
        bizData.put("channelCode", platformChannelEntity.getChannelCode());
        bizData.put("currentOrder", singleQuantity);
        bizData.put("allOrder", allQuantity);
        bizData.put("address", user.getRegisterAddress());
        bizData.put(Field.REPAYMENT_TIME, intervalDays);
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
        loanOrderExamineDao.updateOrderExamineRequestById(orderExamineId, requestParams, new Date());

        // 发送请求
        String result = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
        LogUtil.sysInfo("请求风控多投限制 : request:{}  response:{}", requestParams, result);
        if (StringUtils.isEmpty(result)) {
            return false;
        }

        // 更新节点响应数据
        loanOrderExamineDao.updateOrderExamineResponseById(orderExamineId, result, new Date());

        // 转换为JSON
        JSONObject resultJson = JSONObject.parseObject(result);

        // 返回码
        Integer code = resultJson.getInteger(Field.ERROR);
        if (code != 200) {
            // 更新审核状态
            loanOrderExamineDao.updateOrderExamineStatusById(orderExamineId, OrderExamineStatus.FAIL, new Date());
            return false;
        }

        // 成功
        JSONObject data = resultJson.getJSONObject(Field.DATA);

        // 是否通过
        int pass = data.getInteger(Field.PASS);

        // 未通过
        if (pass == 0) {
            // 更新审核状态
            loanOrderExamineDao.updateOrderExamineStatusById(orderExamineId, OrderExamineStatus.REFUSE, new Date());
            return false;
        }

        // 更新审核状态
        loanOrderExamineDao.updateOrderExamineStatusById(orderExamineId, OrderExamineStatus.PASS, new Date());
        return true;
    }

    /**
     * 多推借贷额度
     * @param user
     * @return
     * @throws Exception
     */
    protected Integer mergeRejectionRule(User user) throws Exception{
        // 用户id
        String userId = user.getId();

        // 注册地址
        String registerAddress = user.getRegisterAddress();

        // app名称
        String appName = user.getAppName();

        // 手机哈
        String mobile = user.getMobile();

        // 渠道ID
        Integer userChannelId = user.getChannelId();

        // 查询渠道信息
        LoanChannelEntity platformChannelEntity = platformChannelDao.findChannel(userChannelId);

        // 渠道名称
        String channelName = platformChannelEntity.getChannelName();

        List<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        // 单包在贷笔数
        int singleQuantity = loanOrderDao.countProcessOrderNo(userIdList);

        // 多包在贷笔数
        userIdList = loanUserInfoDao.findUserIdByMobile(mobile);
        int allQuantity = loanOrderDao.countProcessOrderNo(userIdList);

        // 第一笔还款距今天数
        LoanOrderBillEntity fistRepayOrder = loanOrderBillDao.findFistRepayOrder(userId, user.getAppName());
        int intervalDays = 0;
        if (ObjectUtils.isNotEmpty(fistRepayOrder)) {
            Date actualRepaymentTime = fistRepayOrder.getActualRepaymentTime();
            intervalDays = DateUtil.getIntervalDays(new Date(), actualRepaymentTime);
        }

        // 创建审核记录
        String orderExamineId = ObjectIdUtil.getObjectId();
        LoanOrderExamineEntity loanOrderExamineEntity = new LoanOrderExamineEntity();
        loanOrderExamineEntity.setOrderId("");
        loanOrderExamineEntity.setUserId(userId);
        loanOrderExamineEntity.setId(orderExamineId);
        loanOrderExamineEntity.setStatus(OrderExamineStatus.CREATE);
        loanOrderExamineEntity.setModelName("RejectionRule");
        loanOrderExamineEntity.setUpdateTime(new Date());
        loanOrderExamineEntity.setCreateTime(new Date());
        loanOrderExamineDao.insertOrderExamine(loanOrderExamineEntity);

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
        bizData.put(Field.BORROW_ID, "");
//        bizData.put(Field.PRODUCT_ID, productId); // 多推单，无产品id
        bizData.put(Field.PROGRESS, 0);
        bizData.put(Field.REGISTER_ADDR, registerAddress);
        bizData.put(Field.CHANNEL_NAME, channelName);
        bizData.put("channelCode", platformChannelEntity.getChannelCode());
        bizData.put("currentOrder", singleQuantity);
        bizData.put("allOrder", allQuantity);
        bizData.put("address", user.getRegisterAddress());
        bizData.put(Field.REPAYMENT_TIME, intervalDays);
        bizData.put(Field.PHONE, mobile);
        bizData.put(Field.APP_NAME, appName);
        params.put(Field.BIZ_DATA, bizData.toJSONString());

        // 生成签名
        String paramsStr = RSAUtils.getSortParams(params);
        String sign = RSAUtils.addSign(riskConfig.getPrivateKey(), paramsStr);
        params.put(Field.SIGN, sign);

        // 请求参数
        String requestParams = JSONObject.toJSONString(params);

        // 发送请求
        String result = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
        LogUtil.sysInfo("请求风控多投限制 : request:{}  response:{}", requestParams, result);
        if (StringUtils.isEmpty(result)) {
            return NumberField.NUM_ZERO;
        }

        // 转换为JSON
        JSONObject resultJson = JSONObject.parseObject(result);

        // 返回码
        Integer code = resultJson.getInteger(Field.ERROR);
        if (code != 200) {
            return NumberField.NUM_ZERO;
        }

        // 成功
        JSONObject data = resultJson.getJSONObject(Field.DATA);

        // 是否通过
        int canBorrowNums = data.getInteger(Field.CAN_BORROW_NUMS);

        if(ObjectUtils.isEmpty(canBorrowNums)){
            return 0;
        }

        return  canBorrowNums;
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
    @Override
    public LoanOrderEntity initOrder(User user, Integer type, String appVersion, String appName, String orderModelGroup, LoanProductEntity productEntity) throws Exception {
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
                LoanOrderEntity loanOrderEntity = loanOrderDao.findLatelyOrderByUserIdAndProductIdAndStatus(userId, productId, status);
                if (ObjectUtils.isNotEmpty(loanOrderEntity)) {
                    return loanOrderEntity.getId();
                }

                // 查询用户指定状态订单
                int[] statues = {OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
                int count = loanOrderDao.countUserOrderByProductAndStatusIn(userId, productId, statues);

                // 是否复贷
                Integer reloan = 0;
                if (count > 0) {
                    reloan = 1;
                }

                // 用户客群
                Integer userType = userType(userId, productId);

                // 订单id
                String orderId = ObjectIdUtil.getObjectId();

                // 新增订单
                loanOrderEntity = new LoanOrderEntity();
                loanOrderEntity.setId(orderId);
                loanOrderEntity.setUserId(userId);
                loanOrderEntity.setProductId(productId);
                loanOrderEntity.setUserChannelId(user.getChannelId());
                loanOrderEntity.setBankCardId("");
                loanOrderEntity.setReloan(reloan);
                loanOrderEntity.setOrderModelGroup(orderModelGroup);
                loanOrderEntity.setRemittanceDistributionGroup(productEntity.getRemittanceDistributionGroup());
                loanOrderEntity.setRepaymentDistributionGroup("");
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
                    loanOrderExamineEntity.setUserId(userId);
                    loanOrderExamineEntity.setId(ObjectIdUtil.getObjectId());
                    loanOrderExamineEntity.setStatus(OrderExamineStatus.CREATE);
                    loanOrderExamineEntity.setModelName(model);
                    loanOrderExamineEntity.setUpdateTime(new Date());
                    loanOrderExamineEntity.setCreateTime(new Date());
                    loanOrderExamineDao.insertOrderExamine(loanOrderExamineEntity);
                });

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
     * @return
     */
    protected Integer userType(String userId, String productId) {
        // 用户在本包是否有还款
        int[] status = {OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE};
        Integer count = loanOrderDao.countUserOrderByStatusIn(userId, status);
        // 无:2客群
        if (count == 0) {
            return 2;
        }

        // 本包本产品是否有还款
        count = loanOrderDao.countUserOrderByProductAndStatusIn(userId, productId, status);
        // 无:1客群
        if (count == 0) {
            return 1;
        }

        return 0;
    }

    /**
     * 获取用户客群
     * @param params
     * @return
     */
    @Override
    public Result<UserTypeResult> getUserType(UserTypeParams params){
        Result<UserTypeResult> result = new Result<>();

        Integer type = userType(params.getUserId(), params.getUserId());
        UserTypeResult userTypeResult = new UserTypeResult();
        userTypeResult.setUserType(type);

        result.setData(userTypeResult);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }
}
