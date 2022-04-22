package com.epoch.loan.workshop.order.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.lock.UserApplyDetailLock;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.service.OrderService;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.order.service;
 * @className : OrderServiceImpl
 * @createTime : 2022/3/19 15:07
 * @description : 订单业务
 */
@DubboService(timeout = 5000)
public class OrderServiceImpl extends BaseService implements OrderService {
    /**
     * 订单绑定放款账户
     *
     * @param bindRemittanceAccountParams
     * @return
     */
    @Override
    public Result bindRemittanceAccount(BindRemittanceAccountParams bindRemittanceAccountParams) {
        // 结果集
        Result result = new Result();

        // 订单id
        String orderId = bindRemittanceAccountParams.getOrderId();

        // 账户id
        String remittanceAccountId = bindRemittanceAccountParams.getRemittanceAccountId();

        // 查询订单是否存在
        LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
        if (ObjectUtils.isEmpty(loanOrderEntity)) {
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }

        // 判断订单状态是否在可以绑卡的阶段
        if (loanOrderEntity.getStatus() != OrderStatus.CREATE) {
            result.setReturnCode(ResultEnum.ORDER_ERROR.code());
            result.setMessage(ResultEnum.ORDER_ERROR.message());
            return result;
        }

        // 判断账户是否存在
        LoanRemittanceAccountEntity loanRemittanceAccountEntity = loanRemittanceAccountDao.findRemittanceAccount(remittanceAccountId);
        if (ObjectUtils.isEmpty(loanRemittanceAccountEntity)) {
            result.setReturnCode(ResultEnum.REMITTANCE_ACCOUNT_ERROR.code());
            result.setMessage(ResultEnum.REMITTANCE_ACCOUNT_ERROR.message());
            return result;
        }

        // 进行绑定放款账户 TODO 新老表
        platformOrderDao.updateCardId(orderId, remittanceAccountId, new Date());
        loanOrderDao.updateBankCardId(orderId, remittanceAccountId, new Date());

        // 返回结果集
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 申请
     *
     * @param applyParams
     * @return
     */
    @Override
    public Result apply(ApplyParams applyParams) {
        // 结果集
        Result result = new Result();

        // 用户id
        String userId = applyParams.getUser().getId();

        // 订单id
        String orderId = applyParams.getOrderId();

        // 使用分布式锁进行申请，避免重复申请
        String status = zookeeperClient.lock(new UserApplyDetailLock<String>(userId) {
            @Override
            public String execute() throws Exception {
                // 查询订单
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);

                // 订单审核队列
                String orderModelGroup = loanOrderEntity.getOrderModelGroup();

                // 获取支付账户id
                String bankCardId = loanOrderEntity.getBankCardId();

                // 判断订单是否存在
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    // 失败
                    return "FAIL";
                }

                // 判断订单是否处于创建状态
                if (loanOrderEntity.getStatus() != OrderStatus.CREATE) {
                    // 失败
                    return "FAIL";
                }

                // 判断是否没有绑卡
                if (StringUtils.isEmpty(bankCardId)) {
                    // 失败
                    return "FAIL";
                }

                // 修改订单状态
                int updateOrderStatus = loanOrderDao.updateOrderStatus(orderId, OrderStatus.EXAMINE_WAIT, new Date());
                if (updateOrderStatus != 0) {
                    // 更新申请时间
                    loanOrderDao.updateOrderArrivalTime(orderId, new Date(), new Date());

                    // 查询审核模型列表
                    List<String> orderModelList = orderModelDao.findNamesByGroup(orderModelGroup);

                    // 订单队列参数
                    OrderParams orderParams = new OrderParams();
                    orderParams.setOrderId(orderId);
                    orderParams.setGroupName(orderModelGroup);
                    orderParams.setModelList(orderModelList);
                    orderMQManager.sendMessage(orderParams, orderModelList.get(0));

                    // 成功
                    return "SUCCESS";
                }

                // 失败
                return "FAIL";
            }
        });

        // 返回结果集
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 获取订单合同参数
     *
     * @param contractParams 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<OrderContractResult> contract(ContractParams contractParams) throws Exception {
        // 结果集
        Result<OrderContractResult> result = new Result<OrderContractResult>();

        // 订单号
        String orderNo = contractParams.getOrderNo();

        // 查询订单ID(因订单在卡审核之前不进新表，改为查询老表)
        LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderNo);
        PlatformOrderEntity platformOrderEntity = platformOrderDao.findByOrderNo(orderNo);
        if (ObjectUtils.isEmpty(loanOrderEntity) && ObjectUtils.isEmpty(platformOrderEntity)) {
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }

        // 封装合同参数
        OrderContractResult contractResult = new OrderContractResult();

        // 查询用户
        PlatformUserEntity userEntity = null;

        // 查询机构和nbfc信息
        LoanProductEntity product = null;

        // 查询用户OCR信息
        PlatformUserOcrBasicInfoEntity userOcrBasicInfo = null;

        Date applyTime = null;
        Double approvalAmount = null;
        if (ObjectUtils.isNotEmpty(loanOrderEntity)) {
            userEntity = platformUserDao.findUser(loanOrderEntity.getUserId());
            product = loanProductDao.findProduct(loanOrderEntity.getProductId());
            userOcrBasicInfo = platformUserOcrBasicInfoDao.findUserOcrBasicInfo(loanOrderEntity.getUserId());

            contractResult.setDpn(loanOrderEntity.getId());
            contractResult.setRepayment(loanOrderEntity.getEstimatedRepaymentAmount());

            // 申请时间、合同截至时间
            applyTime = loanOrderEntity.getApplyTime();

            //审批金额
            approvalAmount = loanOrderEntity.getApprovalAmount();
        } else {
            contractResult.setDpn(platformOrderEntity.getOrderNo());
            userEntity = platformUserDao.findUser(platformOrderEntity.getUserId() + "");
            product = loanProductDao.findProduct(platformOrderEntity.getProductId() + "");
            userOcrBasicInfo = platformUserOcrBasicInfoDao.findUserOcrBasicInfo(platformOrderEntity.getUserId() + "");

            // 申请时间、合同截至时间
            applyTime = platformOrderEntity.getApprovalTime();

            //审批金额
            if (ObjectUtils.isNotEmpty(platformOrderEntity.getApprovalAmount())) {
                approvalAmount = platformOrderEntity.getApprovalAmount().doubleValue();
            }
        }

        if (ObjectUtils.isEmpty(userEntity) || ObjectUtils.isEmpty(product) || ObjectUtils.isEmpty(userOcrBasicInfo)) {
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }

        // 添加机构和nbfc 信息
        // 机构信息
        if (StringUtils.isNotBlank(product.getCompanyName())) {
            contractResult.setCompanyName(product.getCompanyName());
            contractResult.setCompanyNameAddr(product.getCompanyName() + " " + product.getCompanyAddr());
        }
        contractResult.setCompanyAddr(product.getCompanyAddr());
        contractResult.setCompanyEmail(product.getCompanyEmail());
        contractResult.setCompanyPhone(product.getCompanyPhone());
        contractResult.setCompanyLogo(product.getCompanyLogo());

        // nbfc信息
        if (StringUtils.isNotBlank(product.getCompanyName())) {
            contractResult.setNbfcName(product.getNbfcName());
            contractResult.setNbfcNameAddr(product.getNbfcName() + "" + product.getNbfcAddr());
        }
        contractResult.setNbfcAddr(product.getNbfcAddr());
        contractResult.setNbfcEmail(product.getNbfcEmail());
        contractResult.setNbfcPhone(product.getNbfcPhone());
        contractResult.setNbfcLogo(product.getNbfcLogo());
        contractResult.setProductName(product.getProductName());

        // 添加订单相关信息
        contractResult.setRepaymentCharges(0.0);

        // 逾期费率
        Double penaltyInterest = product.getPenaltyInterest();
        if (ObjectUtils.isNotEmpty(penaltyInterest)) {
            contractResult.setOverRate(BigDecimal.valueOf(penaltyInterest / 100).setScale(2, RoundingMode.UP).doubleValue());
        }

        Date expirationTime = DateUtil.addMonth(applyTime, 1);
        contractResult.setAcceptedTime(DateUtil.DateToString(applyTime, "dd/MM/yyyy"));
        contractResult.setExpirationTime(DateUtil.DateToString(expirationTime, "dd/MM/yyyy"));
        contractResult.setAmount(approvalAmount);

        // 利息
        Double interest = product.getInterest();
        if (ObjectUtils.isNotEmpty(approvalAmount) && ObjectUtils.isNotEmpty(interest)) {
            Double multiply = BigDecimal.valueOf(approvalAmount)
                    .multiply(BigDecimal.valueOf(interest / 100)).setScale(2, RoundingMode.UP).doubleValue();
            contractResult.setInterest(multiply);
        }

        // 订单借款天数
        Integer stages = product.getStages();
        Integer stagesDay = product.getStagesDay();
        if (ObjectUtils.isNotEmpty(stages) && ObjectUtils.isNotEmpty(stagesDay)) {
            int sumDay = stages * stagesDay;
            contractResult.setDays(sumDay);

            //日利率
            if (ObjectUtils.isNotEmpty(interest)) {
                double interestRate = BigDecimal.valueOf(interest / 100).
                        divide(BigDecimal.valueOf(sumDay), 2, RoundingMode.UP).doubleValue();
                contractResult.setRate(interestRate);
            }
        }

        // 服务费 + 方便费
        Double processingFeeProportion = product.getProcessingFeeProportion();
        if (ObjectUtils.isNotEmpty(processingFeeProportion)) {
            Double multiply = BigDecimal.valueOf(approvalAmount).multiply(BigDecimal.valueOf(processingFeeProportion / 100)).setScale(2, RoundingMode.UP).doubleValue();
            contractResult.setFees(multiply);
        }

        // 用户相关信息
        contractResult.setDeviceId("NA");
        contractResult.setDeviceName("NA");
        contractResult.setCustomerId(userEntity.getUuid());
        contractResult.setDeviceDetails(userEntity.getRegisterIp());
        contractResult.setCustomerAddr(userEntity.getRegisterAddr());
        contractResult.setBorrowerDetail(userOcrBasicInfo.getRealName());

        // 返回结果集
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(contractResult);
        return result;
    }

    /**
     * 订单列表
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<OrderListResult> list(OrderListParams params) throws Exception {
        // 结果集
        Result<OrderListResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_ORDER_LIST;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("orderQueryReq", params.getOrderQueryReq());
        requestParam.put("userId", params.getUserId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>(1);
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, OrderListResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        OrderListResult res = JSONObject.parseObject(data.toJSONString(), OrderListResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 申请确认
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<ComfirmApplyResult> comfirmApply(ComfirmApplyParams params) throws Exception {
        // 结果集
        Result<ComfirmApplyResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_COMFIRM_APPLY;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("orderNo", params.getOrderNo());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ComfirmApplyResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ComfirmApplyResult res = JSONObject.parseObject(data.toJSONString(), ComfirmApplyResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 订单详情
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<OrderDetailResult> detail(OrderDetailParams params) throws Exception {
        // 结果集
        Result<OrderDetailResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_ORDER_DETAIL;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("orderNo", params.getOrderNo());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, OrderDetailResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        OrderDetailResult res = JSONObject.parseObject(data.toJSONString(), OrderDetailResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 还款详情
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<RepayDetailResult> repayDetail(RepayDetailParams params) throws Exception {
        // 结果集
        Result<RepayDetailResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_REPAY_DETAIL;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("orderNo", params.getOrderNo());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, RepayDetailResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        RepayDetailResult res = JSONObject.parseObject(data.toJSONString(), RepayDetailResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 多推--申请确认
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    @Override
    public Result<ConfirmMergePushApplyResult> confirmMergePushApply(ConfirmMergePushApplyParams params) throws Exception {
        // 结果集
        Result<ConfirmMergePushApplyResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_CONFIRM_MERGE_PUSH_APPLY;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("productList", params.getProductList());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ConfirmMergePushApplyResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ConfirmMergePushApplyResult res = JSONObject.parseObject(data.toJSONString(), ConfirmMergePushApplyResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }
}
