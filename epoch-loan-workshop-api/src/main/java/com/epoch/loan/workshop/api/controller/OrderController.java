package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.api.annotated.Authentication;
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
 * @className : OrderContractController
 * @createTime : 2022/3/19 15:10
 * @description : 订单业务接口
 */
@RestController
@RequestMapping(URL.ORDER)
public class OrderController extends BaseController {

    /**
     * 订单绑定放款账户
     *
     * @param bindRemittanceAccountParams
     * @return
     */
    @Authentication
    @PostMapping(URL.BIND_REMITTANCE_ACCOUNT)
    public Result bindRemittanceAccount(BindRemittanceAccountParams bindRemittanceAccountParams) {
        // 结果集
        Result result = new Result();

        try {
            // 验证请求参数是否合法
            if (!bindRemittanceAccountParams.isOrderIdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":orderId");
                return result;
            }

            if (!bindRemittanceAccountParams.isRemittanceAccountIdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":remittanceAccountId");
                return result;
            }

            // 订单绑定放款账户
            return orderService.bindRemittanceAccount(bindRemittanceAccountParams);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController bindRemittanceAccount]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 申请
     *
     * @param applyParams
     * @return
     */
    @Authentication
    @PostMapping(URL.APPLY)
    public Result apply(ApplyParams applyParams) {
        // 结果集
        Result result = new Result();

        try {
            // 验证请求参数是否合法
            if (!applyParams.isOrderIdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":orderNo");
                return result;
            }

            // 申请
            return orderService.apply(applyParams);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController apply]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }


    /**
     * 全部订单列表
     *
     * @param params 请求参数
     * @return Result
     */
    @Authentication
    @PostMapping(URL.ORDER_LIST_ALL)
    public Result<OrderListResult> listAll(BaseParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();

        try {

            // 订单列表
            return orderService.listAll(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController list]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 待完成订单列表
     *
     * @param params 请求参数
     * @return Result
     */
    @Authentication
    @PostMapping(URL.ORDER_UN_FINISHED_LIST)
    public Result<OrderListResult> unfinishedOrderList(BaseParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();

        try {

            // 订单列表
            return orderService.unfinishedOrderList(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController list]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 待还款订单列表
     *
     * @param params 请求参数
     * @return Result
     */
    @Authentication
    @PostMapping(URL.ORDER_UN_REPAYMENT_LIST)
    public Result<OrderListResult> unRepaymentOrderList(BaseParams params) {
        // 结果集
        Result<OrderListResult> result = new Result<>();

        try {

            // 订单列表
            return orderService.unRepaymentOrderList(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController list]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 订单详情
     *
     * @param params 请求参数
     * @return Result
     */
    @Authentication
    @PostMapping(URL.ORDER_DETAIL)
    public Result<OrderDetailResult> detail(OrderDetailParams params) {
        // 结果集
        Result<OrderDetailResult> result = new Result<>();

        try {
            // 验证请求参数是否合法
            if (!params.isOrderIdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":orderId");
                return result;
            }

            // 获取订单详情
            return orderService.detail(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController detail]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 多推--申请确认
     *
     * @param params 请求参数
     * @return Result
     */
    @PostMapping(URL.CONFIRM_MERGE_PUSH_APPLY)
    public Result<ConfirmMergePushApplyResult> confirmMergePushApply(ConfirmMergePushApplyParams params) {
        // 结果集
        Result<ConfirmMergePushApplyResult> result = new Result<>();

        try {
            // 订单详情
            return orderService.confirmMergePushApply(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController confirmMergePushApply]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
