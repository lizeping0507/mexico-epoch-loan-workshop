package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.AddRemittanceAccountParams;
import com.epoch.loan.workshop.common.params.params.result.AddRemittanceAccountResult;
import com.epoch.loan.workshop.common.params.params.result.RemittanceAccountListResult;
import com.epoch.loan.workshop.common.params.params.result.RemittanceBankListResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.controller
 * @className : RemittanceController
 * @createTime : 2022/4/21 14:38
 * @description : 放款
 */
@RestController
@RequestMapping(URL.REMITTANCE)
public class RemittanceController extends BaseController {

    /**
     * 放款账户列表
     *
     * @param baseParams
     * @return
     */
    @Authentication
    @PostMapping(URL.REMITTANCE_ACCOUNT_LIST)
    public Result<RemittanceAccountListResult> remittanceAccountList(BaseParams baseParams) {
        // 结果集
        Result<RemittanceAccountListResult> result = new Result<>();

        try {
            // 放款账户列表
            return remittanceService.remittanceAccountList(baseParams);
        } catch (Exception e) {
            LogUtil.sysError("[RemittanceController remittanceAccountList]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 新增放款账户
     *
     * @param addRemittanceAccountParams
     * @return
     */
    @Authentication
    @PostMapping(URL.ADD_REMITTANCE_ACCOUNT)
    public Result<AddRemittanceAccountResult> addRemittanceAccount(AddRemittanceAccountParams addRemittanceAccountParams) {
        // 结果集
        Result<AddRemittanceAccountResult> result = new Result();

        try {
            // 验证请求参数是否合法
            if (!addRemittanceAccountParams.isAccountNumberLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":accountNumber");
                return result;
            }

            if (!addRemittanceAccountParams.isBankLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":bank");
                return result;
            }

            if (!addRemittanceAccountParams.isTypeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":type");
                return result;
            }

            if (!addRemittanceAccountParams.isNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":name");
                return result;
            }

            // 新增放款账户
            return remittanceService.addRemittanceAccount(addRemittanceAccountParams);
        } catch (Exception e) {
            LogUtil.sysError("[RemittanceController addRemittanceAccount]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 放款银行列表
     *
     * @param baseParams
     * @return
     */
    @Authentication
    @PostMapping(URL.REMITTANCE_BANK_LIST)
    public Result<RemittanceBankListResult> remittanceBankList(BaseParams baseParams) {
        // 结果集
        Result<RemittanceBankListResult> result = new Result<>();

        try {
            // 放款银行列表
            return remittanceService.remittanceBankList(baseParams);
        } catch (Exception e) {
            LogUtil.sysError("[RemittanceController remittanceBankList]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }


}
