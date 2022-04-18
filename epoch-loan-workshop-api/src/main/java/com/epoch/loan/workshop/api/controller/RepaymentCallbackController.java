package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.YeahPayRepamentCallbackParam;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller;
 * @className : RepaymentCallbackController
 * @createTime : 2022/3/9 17:17
 * @description : 还款回调
 */
@RestController
@RequestMapping(URL.REPAYMENT_CALL_BACK)
public class RepaymentCallbackController extends BaseController {


    @RequestMapping(URL.YEAHPAY)
    public Object yeahPay(YeahPayRepamentCallbackParam params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return repaymentService.yeahPayCallback(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController yeahPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
