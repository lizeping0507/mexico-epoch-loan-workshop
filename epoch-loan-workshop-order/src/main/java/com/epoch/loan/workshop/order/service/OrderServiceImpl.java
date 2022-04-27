package com.epoch.loan.workshop.order.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.lock.UserApplyDetailLock;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.params.params.result.model.LoanRepaymentRecordDTO;
import com.epoch.loan.workshop.common.params.params.result.model.OrderDTO;
import com.epoch.loan.workshop.common.service.OrderService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.OrderUtils;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
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
                    loanOrderDao.updateOrderApplyTime(orderId, new Date(), new Date());

                    // 查询审核模型列表
                    List<String> orderModelList = orderModelDao.findNamesByGroup(orderModelGroup);

                    // 发送订单审核队列
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
    public Result<OrderListResult> listAll(OrderListParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());

        // 查询订单列表
        String userId = params.getUser().getId();
        List<OrderDTO> orderDTOList = new ArrayList<>();
        Integer[] status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.EXAMINE_FAIL, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE, OrderStatus.ABANDONED};
        List<LoanOrderEntity> orderAllList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.APPLY_TIME, OrderByField.DESC);
        if (CollectionUtils.isEmpty(orderAllList)) {
            result.setData(new OrderListResult());
            return result;
        }

        // 转换响应参数
        orderAllList.stream().forEach(loanOrderEntity -> {
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(loanOrderEntity, orderDTO);
            orderDTO.setOrderNo(loanOrderEntity.getId());
            orderDTO.setOrderStatus(loanOrderEntity.getStatus());
            orderDTO.setOrderStatusStr(OrderUtils.button(loanOrderEntity.getStatus()));
            if (loanOrderEntity.getStatus() >= OrderStatus.WAY && loanOrderEntity.getStatus() != OrderStatus.ABANDONED) {
                LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(loanOrderEntity.getId());
                orderDTO.setRepaymentTime(lastOrderBill.getRepaymentTime());
            }
            orderDTOList.add(orderDTO);
        });
        result.setData(new OrderListResult(orderDTOList));
        return result;
    }

    /**
     * 待完成订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    @Override
    public Result<OrderListResult> unfinishedOrderList(OrderListParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        String userId = params.getUser().getId();
        List<OrderDTO> orderDTOList = new ArrayList<>();
        List<LoanOrderEntity> orderEntityList = new ArrayList<>();

        // 查询用户审核通过的订单
        Integer[] status = new Integer[]{OrderStatus.EXAMINE_PASS};
        List<LoanOrderEntity> passOrderEntityList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.EXAMINE_PASS_TIME, OrderByField.ASC);
        if (CollectionUtils.isNotEmpty(passOrderEntityList)) {
            orderEntityList.addAll(passOrderEntityList);
        }

        // 查询其他状态的订单
        status = new Integer[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.WAIT_PAY};
        List<LoanOrderEntity> otherOrderEntityList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.UPDATE_TIME, OrderByField.DESC);
        if (CollectionUtils.isNotEmpty(otherOrderEntityList)) {
            orderEntityList.addAll(otherOrderEntityList);
        }
        if (CollectionUtils.isEmpty(orderEntityList)) {
            result.setData(new OrderListResult());
            return result;
        }

        // 转换响应参数
        orderEntityList.stream().forEach(loanOrderEntity -> {
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(loanOrderEntity, orderDTO);
            orderDTO.setOrderNo(loanOrderEntity.getId());
            orderDTO.setOrderStatus(loanOrderEntity.getStatus());
            orderDTO.setOrderStatusStr(OrderUtils.button(loanOrderEntity.getStatus()));
            orderDTOList.add(orderDTO);
        });
        result.setData(new OrderListResult(orderDTOList));
        return result;
    }

    /**
     * 未还款订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    @Override
    public Result<OrderListResult> unRepaymentOrderList(OrderListParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        String userId = params.getUser().getId();
        List<OrderDTO> orderDTOList = new ArrayList<>();

        // 查询用户待还款的订单
        Integer[] status = new Integer[]{OrderStatus.WAY, OrderStatus.DUE};
        List<LoanOrderEntity> orderEntityList = loanOrderDao.findOrderListByUserIdAndStatusAndOrderByField(userId, status, OrderByField.CREATE_TIME, OrderByField.ASC);
        if (CollectionUtils.isEmpty(orderEntityList)) {
            result.setData(new OrderListResult());
            return result;
        }

        // 转换响应参数
        orderEntityList.stream().forEach(loanOrderEntity -> {
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(loanOrderEntity, orderDTO);
            orderDTO.setOrderNo(loanOrderEntity.getId());
            orderDTO.setOrderStatus(loanOrderEntity.getStatus());
            orderDTO.setOrderStatusStr(OrderUtils.button(loanOrderEntity.getStatus()));
            LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(loanOrderEntity.getId());
            orderDTO.setRepaymentTime(lastOrderBill.getRepaymentTime());
            orderDTOList.add(orderDTO);
        });
        result.setData(new OrderListResult(orderDTOList));
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
        detailResult.setOrderStatus(orderEntity.getStatus());
        detailResult.setApprovalAmount(orderEntity.getApprovalAmount());
        detailResult.setIncidentalAmount(orderEntity.getIncidentalAmount());
        detailResult.setActualAmount(orderEntity.getActualAmount());

        // 总利息
        Double interestAmount = loanOrderBillDao.sumOrderInterestAmount(orderEntity.getId());
        detailResult.setInterest(interestAmount);
        Double estimatedRepaymentAmount = orderEntity.getEstimatedRepaymentAmount();
        detailResult.setEstimatedRepaymentAmount(estimatedRepaymentAmount);
        detailResult.setApplyTime(orderEntity.getApplyTime());
        if (orderEntity.getStatus() <= OrderStatus.EXAMINE_FAIL) {
            // 封装结果
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(detailResult);
            return result;
        }

        // 添加银行卡信息
        String bankCardId = orderEntity.getBankCardId();
        LoanRemittanceAccountEntity accountEntity = loanRemittanceAccountDao.findRemittanceAccount(bankCardId);
        detailResult.setBankCardName(accountEntity.getName());
        detailResult.setBankCardNo(accountEntity.getAccountNumber());
        detailResult.setReceiveWay(accountEntity.getType());

        // 废弃订单
        if (orderEntity.getStatus() == OrderStatus.ABANDONED) {
            // 封装结果
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(detailResult);
            return result;
        }
        detailResult.setLoanTime(orderEntity.getLoanTime());
        LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderId);

        // 已还款
        if (orderEntity.getStatus() == OrderStatus.COMPLETE
                || orderEntity.getStatus() == OrderStatus.DUE_COMPLETE) {
            detailResult.setActualRepaymentTime(lastOrderBill.getActualRepaymentTime());

            // 封装结果
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(detailResult);
            return result;
        }

        // 预计还款时间
        detailResult.setExpectedRepaymentTime(lastOrderBill.getRepaymentTime());

        // 总罚息
        Double punishmentAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        detailResult.setPenaltyInterest(punishmentAmount);
        Double actualRepaymentAmount = orderEntity.getActualRepaymentAmount();
        detailResult.setActualRepaymentAmount(actualRepaymentAmount);

        // 剩余还款金额
        Double remainingRepaymentAmount = new BigDecimal(estimatedRepaymentAmount).subtract(new BigDecimal(actualRepaymentAmount)).doubleValue();
        detailResult.setRemainingRepaymentAmount(remainingRepaymentAmount);

        // 已还款成功记录
        List<LoanRepaymentRecordDTO> recordDTOList = new ArrayList<>();
        List<LoanRepaymentPaymentRecordEntity> paymentRecordList = loanRepaymentPaymentRecordDao.findListRecordDTOByOrderIdAndStatus(orderId, LoanRepaymentPaymentRecordStatus.SUCCESS);
        paymentRecordList.stream().forEach(paymentRecord -> {
            LoanRepaymentRecordDTO recordDTO = new LoanRepaymentRecordDTO();
            recordDTO.setRepaymentAmount(paymentRecord.getAmount());
            recordDTO.setTotalAmount(paymentRecord.getActualAmount());

            // 手续费
            double charge = new BigDecimal(paymentRecord.getAmount()).subtract(new BigDecimal(paymentRecord.getActualAmount())).doubleValue();
            recordDTO.setCharge(charge);
            recordDTO.setSuccessTime(paymentRecord.getUpdateTime());
            recordDTO.setRepayWay(paymentRecord.getType());
            recordDTOList.add(recordDTO);
        });
        if (CollectionUtils.isNotEmpty(recordDTOList)) {
            detailResult.setRepayRecord(recordDTOList);
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
}
