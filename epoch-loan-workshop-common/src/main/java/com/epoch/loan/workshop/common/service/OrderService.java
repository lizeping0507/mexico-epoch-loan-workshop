package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.result.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service
 * @className : OrderService
 * @createTime : 2022/3/19 15:07
 * @description : 订单
 */
public interface OrderService {

    /**
     * 获取订单合同参数
     *
     * @param contractParams 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    Result<OrderContractResult> contract(ContractParams contractParams) throws Exception;

    /**
     * 订单列表
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    Result<OrderListResult> list(OrderListParams params) throws Exception;

    /**
     * 申请借款
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ApplyLoanResult> applyLoan(ApplyLoanParams params) throws Exception;

    /**
     * 申请确认
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ComfirmApplyResult> comfirmApply(ComfirmApplyParams params) throws Exception;

    /**
     * 订单详情
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    Result<OrderDetailResult> detail(OrderDetailParams params) throws Exception;

    /**
     * 还款详情
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    Result<RepayDetailResult> repayDetail(RepayDetailParams params) throws Exception;

    /**
     * 多推--申请确认
     *
     * @param params 请求参数
     * @return Result
     * @throws Exception 请求异常
     */
    Result<ConfirmMergePushApplyResult> confirmMergePushApply(ConfirmMergePushApplyParams params) throws Exception;

}
