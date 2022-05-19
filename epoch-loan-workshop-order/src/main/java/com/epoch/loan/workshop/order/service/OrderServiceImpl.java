package com.epoch.loan.workshop.order.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.ApplyParams;
import com.epoch.loan.workshop.common.params.params.request.BindRemittanceAccountParams;
import com.epoch.loan.workshop.common.params.params.request.ConfirmMergePushApplyParams;
import com.epoch.loan.workshop.common.params.params.request.OrderDetailParams;
import com.epoch.loan.workshop.common.params.params.result.ConfirmMergePushApplyResult;
import com.epoch.loan.workshop.common.params.params.result.OrderDetailResult;
import com.epoch.loan.workshop.common.params.params.result.OrderListResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.model.LoanRepaymentRecordResult;
import com.epoch.loan.workshop.common.params.params.result.model.OrderInfoResult;
import com.epoch.loan.workshop.common.service.OrderService;
import com.epoch.loan.workshop.common.util.*;
import com.epoch.loan.workshop.common.zookeeper.lock.UserApplyDetailLock;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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

        // 进行绑定放款账户
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
    public Result apply(ApplyParams applyParams) throws Exception {
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
                LogUtil.sysInfo("loanOrderEntity: {}", loanOrderEntity);

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

                LogUtil.sysInfo("loanOrderEntity: {}", loanOrderEntity);
                // 修改订单状态
                int updateOrderStatus = loanOrderDao.updateOrderStatus(orderId, OrderStatus.EXAMINE_WAIT, new Date());
                LogUtil.sysInfo("updateOrderStatus: {}", updateOrderStatus);
                if (updateOrderStatus != 0) {
                    // 更新申请时间
                    loanOrderDao.updateOrderApplyTime(orderId, new Date(), new Date());

                    // 查询审核模型列表
                    List<String> orderModelList = orderModelDao.findNamesByGroup(orderModelGroup);
                    ;
                    LogUtil.sysInfo("orderModelList: {}", updateOrderStatus);

                    // 发送订单审核队列
                    OrderParams orderParams = new OrderParams();
                    orderParams.setOrderId(orderId);
                    orderParams.setGroupName(orderModelGroup);
                    orderParams.setModelList(orderModelList);
                    orderMQManager.sendMessage(orderParams, orderModelList.get(0));
                    LogUtil.sysInfo("SUCCESS: {}", "SUCCESS");

                    // 成功
                    return "SUCCESS";
                }

                // 失败
                return "FAIL";
            }
        });

        // 判断是否提交失败
        if (status.equals("FAIL")) {
            // 返回结果集
            result.setReturnCode(ResultEnum.ORDER_ERROR.code());
            result.setMessage(ResultEnum.ORDER_ERROR.message());
            return result;
        }

        // 返回结果集
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 全部订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    @Override
    public Result<OrderListResult> listAll(BaseParams params) {
        // 封装结果集
        Result<OrderListResult> result = new Result<>();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());

        // 全部订单列表响应集合
        List<OrderInfoResult> orderInfoList = new ArrayList<>();

        // 查询用户所有状态的订单
        String userId = params.getUser().getId();
        Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.EXAMINE_FAIL, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE, OrderStatus.ABANDONED};
        List<LoanOrderEntity> orderAllList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.APPLY_TIME, OrderByField.DESC);
        if (CollectionUtils.isEmpty(orderAllList)) {
            result.setData(new OrderListResult());
            return result;
        }

        // 转换响应参数
        orderAllList.forEach(loanOrderEntity -> {
            OrderInfoResult orderInfoResult = new OrderInfoResult();
            BeanUtils.copyProperties(loanOrderEntity, orderInfoResult);
            orderInfoResult.setOrderNo(loanOrderEntity.getId());
            orderInfoResult.setOrderStatus(loanOrderEntity.getStatus());
            orderInfoResult.setOrderStatusStr(OrderUtils.button(loanOrderEntity.getStatus()));

            // 申请时间--- 取订单创建时间
            orderInfoResult.setApplyTime(loanOrderEntity.getCreateTime());

            // 未还款订单添加 预计还款时间、剩余应还金额
            if (loanOrderEntity.getStatus() >= OrderStatus.WAY && loanOrderEntity.getStatus() != OrderStatus.ABANDONED) {
                LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(loanOrderEntity.getId());
                orderInfoResult.setRepaymentTime(lastOrderBill.getRepaymentTime());

                // 实际还款金额
                Double actualRepaymentAmount = loanOrderEntity.getActualRepaymentAmount();
                if (ObjectUtils.isEmpty(actualRepaymentAmount)) {
                    actualRepaymentAmount = 0.0;
                }

                // 预估还款金额
                Double estimatedRepaymentAmount = loanOrderEntity.getEstimatedRepaymentAmount();
                if (ObjectUtils.isEmpty(estimatedRepaymentAmount)) {
                    estimatedRepaymentAmount = 0.0;
                }

                // 剩余还款金额 = 预估还款金额-实际还款金额
                Double repaymentAmount = new BigDecimal(estimatedRepaymentAmount).subtract(new BigDecimal(actualRepaymentAmount)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                if (loanOrderEntity.getStatus() == OrderStatus.COMPLETE || loanOrderEntity.getStatus() == OrderStatus.DUE_COMPLETE) {
                    orderInfoResult.setRepaymentAmount(estimatedRepaymentAmount);
                } else {
                    orderInfoResult.setRepaymentAmount(repaymentAmount);
                }

            }

            // 添加产品名称 和 产品图片
            LoanProductEntity product = loanProductDao.findProduct(loanOrderEntity.getProductId());
            orderInfoResult.setProductIconImageUrl(product.getIcon());
            orderInfoResult.setProductName(product.getProductName());

            // 贷款金额
            if (loanOrderEntity.getStatus() <= OrderStatus.EXAMINE_WAIT || loanOrderEntity.getStatus() == OrderStatus.EXAMINE_FAIL) {

                // 未审核和审核中的订单 贷款金额取 产品申请金额的最小值; 且新老用户申请金额范围不同
                String amountRange = product.getAmountRange();
                if (UserType.NEW == loanOrderEntity.getUserType()) {
                    orderInfoResult.setApprovalAmount(getMinAmountRange(amountRange, "newConfig"));
                } else {
                    orderInfoResult.setApprovalAmount(getMinAmountRange(amountRange, "oldConfig"));
                }
            } else {

                // 审核通过的订单 取订单的贷款金额
                orderInfoResult.setApprovalAmount(loanOrderEntity.getApprovalAmount() + "");
            }
            orderInfoList.add(orderInfoResult);
        });
        result.setData(new OrderListResult(orderInfoList));
        return result;
    }

    /**
     * 待完成订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    @Override
    public Result<OrderListResult> unfinishedOrderList(BaseParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        String userId = params.getUser().getId();

        // 待完成订单响应集合
        List<OrderInfoResult> orderInfoList = new ArrayList<>();

        // 待完成订单查询集合
        List<LoanOrderEntity> orderEntityList = new ArrayList<>();

        // 查询用户审核通过的订单
        Integer[] status = new Integer[]{OrderStatus.EXAMINE_PASS};
        List<LoanOrderEntity> passOrderEntityList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.EXAMINE_PASS_TIME, OrderByField.ASC);
        if (CollectionUtils.isNotEmpty(passOrderEntityList)) {
            orderEntityList.addAll(passOrderEntityList);
        }

        // 查询卡审和待放款的订单
        status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.WAIT_PAY};
        List<LoanOrderEntity> otherOrderEntityList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.UPDATE_TIME, OrderByField.DESC);
        if (CollectionUtils.isNotEmpty(otherOrderEntityList)) {
            orderEntityList.addAll(otherOrderEntityList);
        }

        // 用户无 待完成的订单，响应为空
        if (CollectionUtils.isEmpty(orderEntityList)) {
            result.setData(new OrderListResult());
            return result;
        }

        // 转换响应参数
        orderEntityList.forEach(loanOrderEntity -> {
            OrderInfoResult orderInfoResult = new OrderInfoResult();
            BeanUtils.copyProperties(loanOrderEntity, orderInfoResult);
            orderInfoResult.setOrderNo(loanOrderEntity.getId());
            orderInfoResult.setOrderStatus(loanOrderEntity.getStatus());
            orderInfoResult.setOrderStatusStr(OrderUtils.button(loanOrderEntity.getStatus()));

            // 申请时间--- 取订单创建时间
            orderInfoResult.setApplyTime(loanOrderEntity.getCreateTime());

            // 添加产品名称 和 产品图片
            LoanProductEntity product = loanProductDao.findProduct(loanOrderEntity.getProductId());
            orderInfoResult.setProductIconImageUrl(product.getIcon());
            orderInfoResult.setProductName(product.getProductName());

            // 贷款金额
            if (loanOrderEntity.getStatus() <= OrderStatus.EXAMINE_WAIT) {

                // 审核中的订单 贷款金额取 产品申请金额的最小值; 且新老用户申请金额范围不同
                String amountRange = product.getAmountRange();
                if (UserType.NEW == loanOrderEntity.getUserType()) {
                    orderInfoResult.setApprovalAmount(getMinAmountRange(amountRange, "newConfig"));
                } else {
                    orderInfoResult.setApprovalAmount(getMinAmountRange(amountRange, "oldConfig"));
                }
            } else {

                // 审核通过的订单 取订单的贷款金额
                orderInfoResult.setApprovalAmount(loanOrderEntity.getApprovalAmount() + "");
            }
            orderInfoList.add(orderInfoResult);
        });
        result.setData(new OrderListResult(orderInfoList));
        return result;
    }

    /**
     * 未还款订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    @Override
    public Result<OrderListResult> unRepaymentOrderList(BaseParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        String userId = params.getUser().getId();

        // 未还款订单查询集合
        List<OrderInfoResult> orderInfoList = new ArrayList<>();

        // 查询用户待还款的订单
        Integer[] status = new Integer[]{OrderStatus.WAY, OrderStatus.DUE};
        List<LoanOrderEntity> orderEntityList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.CREATE_TIME, OrderByField.ASC);
        if (CollectionUtils.isEmpty(orderEntityList)) {
            result.setData(new OrderListResult());
            return result;
        }

        // 转换响应参数
        orderEntityList.forEach(loanOrderEntity -> {
            OrderInfoResult orderInfoResult = new OrderInfoResult();
            BeanUtils.copyProperties(loanOrderEntity, orderInfoResult);
            orderInfoResult.setOrderNo(loanOrderEntity.getId());
            orderInfoResult.setOrderStatus(loanOrderEntity.getStatus());
            orderInfoResult.setOrderStatusStr(OrderUtils.button(loanOrderEntity.getStatus()));

            // 申请时间--- 取订单创建时间
            orderInfoResult.setApplyTime(loanOrderEntity.getCreateTime());

            // 应还款时间
            LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(loanOrderEntity.getId());
            orderInfoResult.setRepaymentTime(lastOrderBill.getRepaymentTime());

            // 实际还款金额
            Double actualRepaymentAmount = loanOrderEntity.getActualRepaymentAmount();
            if (ObjectUtils.isEmpty(actualRepaymentAmount)) {
                actualRepaymentAmount = 0.0;
            }

            // 预估还款金额
            Double estimatedRepaymentAmount = loanOrderEntity.getEstimatedRepaymentAmount();
            if (ObjectUtils.isEmpty(estimatedRepaymentAmount)) {
                estimatedRepaymentAmount = 0.0;
            }

            // 剩余还款金额 = 预估还款金额-实际还款金额
            Double repaymentAmount = new BigDecimal(estimatedRepaymentAmount).subtract(new BigDecimal(actualRepaymentAmount)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            orderInfoResult.setRepaymentAmount(repaymentAmount);

            // 添加产品名称 和 产品图片
            LoanProductEntity product = loanProductDao.findProduct(loanOrderEntity.getProductId());
            orderInfoResult.setProductIconImageUrl(product.getIcon());
            orderInfoResult.setProductName(product.getProductName());

            // 贷款金额
            if (loanOrderEntity.getStatus() <= OrderStatus.EXAMINE_WAIT) {
                String amountRange = product.getAmountRange();

                // 未审核和审核中的订单 贷款金额取 产品申请金额的最小值; 且新老用户申请金额范围不同
                if (UserType.NEW == loanOrderEntity.getUserType()) {
                    orderInfoResult.setApprovalAmount(getMinAmountRange(amountRange, "newConfig"));
                } else {
                    orderInfoResult.setApprovalAmount(getMinAmountRange(amountRange, "oldConfig"));
                }
            } else {

                // 审核通过的订单 取订单的贷款金额
                orderInfoResult.setApprovalAmount(loanOrderEntity.getApprovalAmount() + "");
            }

            orderInfoList.add(orderInfoResult);
        });
        result.setData(new OrderListResult(orderInfoList));
        return result;
    }

    /**
     * 订单详情
     *
     * @param params 请求参数
     * @return Result 订单详情
     */
    @Override
    public Result<OrderDetailResult> detail(OrderDetailParams params) {
        // 结果集
        Result<OrderDetailResult> result = new Result<>();

        // 用户id
        String userId = params.getUser().getId();

        // 订单id
        String orderId = params.getOrderId();
        LoanOrderEntity orderEntity = loanOrderDao.findOrder(orderId);
        if (ObjectUtils.isEmpty(orderEntity)) {
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }
        if (!orderEntity.getUserId().equals(userId)) {
            result.setReturnCode(ResultEnum.ORDER_ERROR.code());
            result.setMessage(ResultEnum.ORDER_ERROR.message());
            return result;
        }

        // 封装参数
        OrderDetailResult detailResult = new OrderDetailResult();
        detailResult.setOrderNo(orderEntity.getId());
        detailResult.setProductId(orderEntity.getProductId());
        LoanProductEntity product = loanProductDao.findProduct(orderEntity.getProductId());
        detailResult.setProductName(product.getProductName());
        detailResult.setOrderStatus(orderEntity.getStatus());

        // 服务费
        Double incidentalAmount = orderEntity.getIncidentalAmount();
        if (ObjectUtils.isNotEmpty(incidentalAmount) && new BigDecimal(incidentalAmount).compareTo(BigDecimal.ZERO) != 1) {
            detailResult.setIncidentalAmount(incidentalAmount + "");
        } else {
            String serviceFeeRange = product.getServiceFeeRange();
            if (StringUtils.isNotBlank(serviceFeeRange) && serviceFeeRange.contains("-") && serviceFeeRange.split("-").length == 2) {
                detailResult.setIncidentalAmount(serviceFeeRange.split("-")[0]);
            }
        }

        // 总利息
        Double interestAmount = loanOrderBillDao.sumOrderInterestAmount(orderEntity.getId());
        if (ObjectUtils.isNotEmpty(interestAmount)) {
            detailResult.setInterest(interestAmount + "");
        } else {
            String interestRange = product.getInterestRange();
            if (StringUtils.isNotBlank(interestRange) && interestRange.contains("-") && interestRange.split("-").length == 2) {
                detailResult.setInterest(interestRange.split("-")[0]);
            }
        }

        // 应还金额
        LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderId);
        Double estimatedRepaymentAmount = orderEntity.getEstimatedRepaymentAmount();

        // 账单id
        if (ObjectUtils.isNotEmpty(lastOrderBill)) {
            detailResult.setOrderBillId(lastOrderBill.getId());
        }

        // 预计还款金额、实际到账金额、申请金额
        if (orderEntity.getStatus() <= OrderStatus.EXAMINE_WAIT) {
            // 未审核通过时，预计还款金额、实际到账金额、申请金额  都去产品额度范围的最小值; 且新老用户申请金额范围不同
            String amountRange = product.getAmountRange();
            String repaymentRange = product.getRepaymentRange();
            String arrivalRange = product.getArrivalRange();
            LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);

            // 申请额度
            if (UserType.NEW == loanOrderEntity.getUserType()) {
                detailResult.setApprovalAmount(getMinAmountRange(amountRange, "newConfig"));
                detailResult.setEstimatedRepaymentAmount(getMinAmountRange(repaymentRange, "newConfig"));
                detailResult.setActualAmount(getMinAmountRange(arrivalRange, "newConfig"));
            } else {
                detailResult.setApprovalAmount(getMinAmountRange(amountRange, "oldConfig"));
                detailResult.setEstimatedRepaymentAmount(getMinAmountRange(repaymentRange, "oldConfig"));
                detailResult.setActualAmount(getMinAmountRange(arrivalRange, "oldConfig"));
            }
        } else {
            detailResult.setApprovalAmount(orderEntity.getApprovalAmount() + "");
            detailResult.setEstimatedRepaymentAmount(estimatedRepaymentAmount + "");
            detailResult.setActualAmount(orderEntity.getActualAmount() + "");
        }

        // 申请时间
        if (ObjectUtils.isNotEmpty(orderEntity.getApplyTime())) {
            detailResult.setApplyTime(orderEntity.getApplyTime());
        } else {
            detailResult.setApplyTime(new Date());
        }

        // 预计还款时间
        if (ObjectUtils.isNotEmpty(lastOrderBill) &&
                ObjectUtils.isNotEmpty(lastOrderBill.getRepaymentTime())) {
            detailResult.setExpectedRepaymentTime(lastOrderBill.getRepaymentTime());
        } else {
            Date repaymentTimeStr = DateUtil.addDay(new Date(), 6 * orderEntity.getStages());
            Date repaymentTime = DateUtil.StringToDate(DateUtil.DateToString(repaymentTimeStr, "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            detailResult.setExpectedRepaymentTime(repaymentTime);
        }

        // 添加银行卡信息
        String bankCardId = orderEntity.getBankCardId();
        if (StringUtils.isNotBlank(bankCardId)) {
            LoanRemittanceAccountEntity accountEntity = loanRemittanceAccountDao.findRemittanceAccount(bankCardId);
            if (ObjectUtils.isNotEmpty(accountEntity)) {
                detailResult.setBankCardName(accountEntity.getBank());
                detailResult.setBankCardNo(accountEntity.getAccountNumber());
                detailResult.setReceiveWay(accountEntity.getType());
            }
        }

        if (orderEntity.getStatus() <= OrderStatus.EXAMINE_FAIL) {
            // 封装结果
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(detailResult);
            return result;
        }

        // 废弃订单
        if (orderEntity.getStatus() == OrderStatus.ABANDONED) {
            // 封装结果
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(detailResult);
            return result;
        }
        detailResult.setLoanTime(orderEntity.getLoanTime());

        // 总罚息
        Double punishmentAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        if (ObjectUtils.isNotEmpty(punishmentAmount)) {
            detailResult.setPenaltyInterest(punishmentAmount);
        } else {
            detailResult.setPenaltyInterest(0.0);
        }


        // 实际已还金额
        Double actualRepaymentAmount = orderEntity.getActualRepaymentAmount();
        detailResult.setActualRepaymentAmount(actualRepaymentAmount);

        // 剩余还款金额
        Double remainingRepaymentAmount = new BigDecimal(estimatedRepaymentAmount).subtract(new BigDecimal(actualRepaymentAmount)).doubleValue();
        detailResult.setRemainingRepaymentAmount(remainingRepaymentAmount);

        // 已还款成功记录
        List<LoanRepaymentRecordResult> recordDTOList = new ArrayList<>();
        List<LoanRepaymentPaymentRecordEntity> paymentRecordList = loanRepaymentPaymentRecordDao.findListRecordDTOByOrderIdAndStatus(orderId, LoanRepaymentPaymentRecordStatus.SUCCESS);
        paymentRecordList.forEach(paymentRecord -> {
            LoanRepaymentRecordResult recordDTO = new LoanRepaymentRecordResult();
            recordDTO.setRepaymentAmount(paymentRecord.getAmount());
            recordDTO.setTotalAmount(paymentRecord.getActualAmount());

            // 手续费
            double charge = new BigDecimal(paymentRecord.getAmount()).subtract(new BigDecimal(paymentRecord.getActualAmount())).doubleValue();
            recordDTO.setCharge(charge);
            recordDTO.setSuccessTime(paymentRecord.getUpdateTime());
            recordDTO.setSuccessDay(paymentRecord.getUpdateTime());
            recordDTO.setRepayWay(paymentRecord.getType());
            recordDTOList.add(recordDTO);
        });
        if (CollectionUtils.isNotEmpty(recordDTOList)) {
            detailResult.setRepayRecord(recordDTOList);
        }

        // 已还款
        if (orderEntity.getStatus() == OrderStatus.COMPLETE || orderEntity.getStatus() == OrderStatus.DUE_COMPLETE) {
            detailResult.setActualRepaymentTime(lastOrderBill.getActualRepaymentTime());
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(detailResult);
        return result;
    }


    /**
     * 申请确认页
     *
     * @param params 请求参数
     * @return Result 订单详情
     */
    @Override
    public Result<OrderDetailResult> applyConfirmation(OrderDetailParams params) {
        // 结果集
        Result<OrderDetailResult> result = new Result<>();

        // 用户id
        String userId = params.getUser().getId();

        // 订单id
        String orderId = params.getOrderId();
        LoanOrderEntity orderEntity = loanOrderDao.findOrder(orderId);
        if (ObjectUtils.isEmpty(orderEntity)) {
            result.setReturnCode(ResultEnum.NO_EXITS.code());
            result.setMessage(ResultEnum.NO_EXITS.message());
            return result;
        }
        if (!orderEntity.getUserId().equals(userId)) {
            result.setReturnCode(ResultEnum.ORDER_ERROR.code());
            result.setMessage(ResultEnum.ORDER_ERROR.message());
            return result;
        }

        // 封装参数
        OrderDetailResult detailResult = new OrderDetailResult();
        detailResult.setOrderNo(orderEntity.getId());
        detailResult.setProductId(orderEntity.getProductId());
        LoanProductEntity product = loanProductDao.findProduct(orderEntity.getProductId());
        detailResult.setProductName(product.getProductName());
        detailResult.setOrderStatus(orderEntity.getStatus());
        detailResult.setIncidentalAmount(orderEntity.getIncidentalAmount() + "");

        // 总利息
        Double interestAmount = loanOrderBillDao.sumOrderInterestAmount(orderEntity.getId());
        detailResult.setInterest(interestAmount + "");

        // 区分是否放款填充 预计还款时间、预计还款金额、申请时间、申请金额
        LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderId);
        Double estimatedRepaymentAmount = orderEntity.getEstimatedRepaymentAmount();
        if (orderEntity.getStatus() <= OrderStatus.EXAMINE_WAIT) {
            String amountRange = product.getAmountRange();
            String repaymentRange = product.getRepaymentRange();
            String arrivalRange = product.getArrivalRange();

            // 用户客群
            Integer userType = userType(params.getUser().getId(), product.getId());

            // 申请额度
            if (UserType.NEW == userType) {
                detailResult.setApprovalAmount(getAmountRange(amountRange, "newConfig"));
                detailResult.setEstimatedRepaymentAmount(getAmountRange(repaymentRange, "newConfig"));
                detailResult.setActualAmount(getAmountRange(arrivalRange, "newConfig"));
            } else {
                detailResult.setApprovalAmount(getAmountRange(amountRange, "oldConfig"));
                detailResult.setEstimatedRepaymentAmount(getAmountRange(repaymentRange, "oldConfig"));
                detailResult.setActualAmount(getAmountRange(arrivalRange, "oldConfig"));
            }
        } else {
            detailResult.setApprovalAmount(orderEntity.getApprovalAmount() + "");
            detailResult.setEstimatedRepaymentAmount(estimatedRepaymentAmount + "");
            detailResult.setActualAmount(orderEntity.getActualAmount() + "");
        }

        // 申请时间
        if (ObjectUtils.isNotEmpty(orderEntity.getApplyTime())) {
            detailResult.setApplyTime(orderEntity.getApplyTime());
        } else {
            detailResult.setApplyTime(new Date());
        }

        // 预计还款时间
        if (ObjectUtils.isNotEmpty(lastOrderBill) &&
                ObjectUtils.isNotEmpty(lastOrderBill.getRepaymentTime())) {
            detailResult.setExpectedRepaymentTime(lastOrderBill.getRepaymentTime());
        } else {
            Date repaymentTimeStr = DateUtil.addDay(new Date(), 6 * orderEntity.getStages());
            Date repaymentTime = DateUtil.StringToDate(DateUtil.DateToString(repaymentTimeStr, "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            detailResult.setExpectedRepaymentTime(repaymentTime);
        }

        // 添加银行卡信息
        String bankCardId = orderEntity.getBankCardId();
        LoanRemittanceAccountEntity accountEntity = loanRemittanceAccountDao.findRemittanceAccount(bankCardId);
        detailResult.setBankCardName(accountEntity.getBank());
        detailResult.setBankCardNo(accountEntity.getAccountNumber());
        detailResult.setReceiveWay(accountEntity.getType());

        if (orderEntity.getStatus() <= OrderStatus.EXAMINE_FAIL) {
            // 封装结果
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(detailResult);
            return result;
        }

        // 废弃订单
        if (orderEntity.getStatus() == OrderStatus.ABANDONED) {
            // 封装结果
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(detailResult);
            return result;
        }
        detailResult.setLoanTime(orderEntity.getLoanTime());

        // 总罚息
        Double punishmentAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        if (ObjectUtils.isNotEmpty(punishmentAmount)) {
            detailResult.setPenaltyInterest(punishmentAmount);
        } else {
            detailResult.setPenaltyInterest(0.0);
        }

        Double actualRepaymentAmount = orderEntity.getActualRepaymentAmount();
        detailResult.setActualRepaymentAmount(actualRepaymentAmount);

        // 剩余还款金额
        Double remainingRepaymentAmount = new BigDecimal(estimatedRepaymentAmount).subtract(new BigDecimal(actualRepaymentAmount)).doubleValue();
        detailResult.setRemainingRepaymentAmount(remainingRepaymentAmount);

        // 已还款成功记录
        List<LoanRepaymentRecordResult> recordDTOList = new ArrayList<>();
        List<LoanRepaymentPaymentRecordEntity> paymentRecordList = loanRepaymentPaymentRecordDao.findListRecordDTOByOrderIdAndStatus(orderId, LoanRepaymentPaymentRecordStatus.SUCCESS);
        paymentRecordList.forEach(paymentRecord -> {
            LoanRepaymentRecordResult recordDTO = new LoanRepaymentRecordResult();
            recordDTO.setRepaymentAmount(paymentRecord.getAmount());
            recordDTO.setTotalAmount(paymentRecord.getActualAmount());

            // 手续费
            double charge = new BigDecimal(paymentRecord.getAmount()).subtract(new BigDecimal(paymentRecord.getActualAmount())).doubleValue();
            recordDTO.setCharge(charge);
            recordDTO.setSuccessTime(paymentRecord.getUpdateTime());
            recordDTO.setSuccessDay(paymentRecord.getUpdateTime());
            recordDTO.setRepayWay(paymentRecord.getType());
            recordDTOList.add(recordDTO);
        });
        if (CollectionUtils.isNotEmpty(recordDTOList)) {
            detailResult.setRepayRecord(recordDTOList);
        }

        // 已还款
        if (orderEntity.getStatus() == OrderStatus.COMPLETE || orderEntity.getStatus() == OrderStatus.DUE_COMPLETE) {
            detailResult.setActualRepaymentTime(lastOrderBill.getActualRepaymentTime());
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(detailResult);
        return result;
    }

    /**
     * 多推--申请确认
     *
     * @param params 请求参数
     * @return Result
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


    /**
     * 计算用户客群
     *
     * @param userId 用户id
     * @param productId 产品id
     * @return 用户客群
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
     * 获取给定JSON字符串的指定key的最小值
     *
     * @param jsonRange JSON字符串
     * @param key 指定key
     * @return 最小的值
     */
    private String getMinAmountRange(String jsonRange, String key) {
        String range = getAmountRange(jsonRange, key);
        if (StringUtils.isNotBlank(range) && range.contains("-") && range.split("-").length == 2) {
            return range.split("-")[0];
        }
        return null;
    }

    /**
     * 获取给定JSON字符串的指定key的值
     *
     * @param jsonRange JSON字符串
     * @param key 指定key
     * @return 最小的值
     */
    private String getAmountRange(String jsonRange, String key) {
        JSONObject jsonObject = JSONObject.parseObject(jsonRange, JSONObject.class);
        return jsonObject.getString(key);
    }
}
