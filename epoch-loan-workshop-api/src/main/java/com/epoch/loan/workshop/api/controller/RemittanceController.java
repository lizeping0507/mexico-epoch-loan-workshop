package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.result.RemittanceAccountListResult;
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
    @PostMapping(URL.REMITTANCE_ACCOUNT_LIST)
    public Result<RemittanceAccountListResult> remittanceAccountList(BaseParams baseParams) {
        // 结果集
        Result<RemittanceAccountListResult> result = new Result<>();

        try {
            // 获取App模式
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

}
