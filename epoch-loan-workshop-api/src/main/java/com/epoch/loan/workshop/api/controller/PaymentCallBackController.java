package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller;
 * @className : PaymentCallBack
 * @createTime : 2022/2/10 16:36
 * @description : 支付回调
 */
@RestController
@RequestMapping(URL.PAYMENT_CALL_BACK)
public class PaymentCallBackController extends BaseController {

    /**
     * pandaPay 支付回调
     *
     * @param params pandaPay支付回调参数
     * @return String
     */
    @PostMapping(URL.PANDA_PAY)
    public Object yeahPay(PandaPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.pandaPay(params);
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
