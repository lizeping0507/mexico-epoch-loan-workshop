package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.result.*;
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
     * 获取订单合同参数
     *
     * @param contractParams 订单合同请求封装类
     * @return 合同参数
     */
    @PostMapping(URL.CONTRACT)
    public Result<OrderContractResult> contract(ContractParams contractParams) {
        // 结果集
        Result<OrderContractResult> result = new Result<OrderContractResult>();

        try {
            // 判断手机号是否已经注册过
            return orderService.contract(contractParams);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController contract]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

    }

    /**
     * 订单列表
     *
     * @param params 请求参数
     * @return Result
     */
    @PostMapping(URL.ORDER_LIST)
    public Result<OrderListResult> list(OrderListParams params) {
        LogUtil.sysInfo(URL.ORDER + URL.ORDER_LIST + " params : {}", JSONObject.toJSONString(params));

        // 结果集
        Result<OrderListResult> result = new Result<OrderListResult>();

        try {
            // 订单列表
            return orderService.list(params);
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
     * 申请借款
     *
     * @param params 请求参数
     * @return Result
     */
    @PostMapping(URL.APPLYLOAN)
    public Result<ApplyLoanResult> applyLoan(ApplyLoanParams params) {
        // 结果集
        Result<ApplyLoanResult> result = new Result<ApplyLoanResult>();

        try {
            // 申请借款
            return orderService.applyLoan(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController applyLoan]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 申请确认
     *
     * @param params 请求参数
     * @return Result
     */
    @PostMapping(URL.COMFIRM_APPLY)
    public Result<ComfirmApplyResult> comfirmApply(ComfirmApplyParams params) {
        // 结果集
        Result<ComfirmApplyResult> result = new Result<ComfirmApplyResult>();

        try {
            // 订单详情
            return orderService.comfirmApply(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController comfirmApply]", e);

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
    @PostMapping(URL.ORDER_DETAIL)
    public Result<OrderDetailResult> detail(OrderDetailParams params) {
        // 结果集
        Result<OrderDetailResult> result = new Result<OrderDetailResult>();

        try {
            // 获取订单合同参数
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
     * 还款详情
     *
     * @param params 请求参数
     * @return Result
     */
    @PostMapping(URL.REPAY_DETAIL)
    public Result<RepayDetailResult> repayDetail(RepayDetailParams params) {
        // 结果集
        Result<RepayDetailResult> result = new Result<>();

        try {
            // 获取订单合同参数
            return orderService.repayDetail(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController repayDetail]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     *多推--申请确认
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
