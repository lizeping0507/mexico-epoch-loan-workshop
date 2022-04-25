package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service
 * @className : OrderService
 * @createTime : 2022/3/19 15:07
 * @description : 订单
 */
public interface OrderService {
    /**
     * 订单绑定放款账户
     *
     * @param bindRemittanceAccountParams
     * @return
     */
    Result bindRemittanceAccount(BindRemittanceAccountParams bindRemittanceAccountParams);

    /**
     * 申请
     *
     * @param applyParams
     * @return
     */
    Result apply(ApplyParams applyParams);

    /**
     * 全部订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    Result<OrderListResult> listAll(OrderListParams params);

    /**
     * 未完成订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    Result<OrderListResult> unfinishedOrderList(OrderListParams params);

    /**
     * 未还款订单列表
     *
     * @param params 请求参数
     * @return Result 订单列表
     */
    public Result<OrderListResult> unRepaymentOrderList(OrderListParams params);

    /**
     * 订单详情
     *
     * @param params 请求参数
     * @return Result
     */
    Result<OrderDetailResult> detail(OrderDetailParams params);

    /**
     * 多推--申请确认
     *
     * @param params 请求参数
     * @return Result
     */
    Result<ConfirmMergePushApplyResult> confirmMergePushApply(ConfirmMergePushApplyParams params) throws Exception;

}
