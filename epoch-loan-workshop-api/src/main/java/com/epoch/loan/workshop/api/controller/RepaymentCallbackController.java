package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.PandaAuthorizeParam;
import com.epoch.loan.workshop.common.params.params.request.PandaPayVerifyUserParam;
import com.epoch.loan.workshop.common.params.params.request.PandaRepaymentCallbackParam;
import com.epoch.loan.workshop.common.params.params.result.PandaPayAuthorizeResult;
import com.epoch.loan.workshop.common.params.params.result.PandaPayVerifyUserResult;
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


    @RequestMapping(URL.PANDA_PAY)
    public Object pandaPay(PandaRepaymentCallbackParam params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return repaymentService.pandaPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController yeahPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * pandaPay 用户验证
     * @param params
     * @return
     */
    @RequestMapping(URL.PANDA_PAY_VERIFY_USER)
    public PandaPayVerifyUserResult pandaPayVerifyUser(PandaPayVerifyUserParam params) {
        PandaPayVerifyUserResult pandaPayVerifyUserResult = new PandaPayVerifyUserResult();
        try {
            pandaPayVerifyUserResult.setPayable(true);
            pandaPayVerifyUserResult.setMin_amount(5000);
            pandaPayVerifyUserResult.setMax_amount(1000000);
            return pandaPayVerifyUserResult;
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController yeahPay]", e);

            return pandaPayVerifyUserResult;
        }
    }

    /**
     * pandaPay 授权
     * @param params
     * @return
     */
    @RequestMapping(URL.PANDA_PAY_AUTHORIZE)
    public PandaPayAuthorizeResult pandaPayAuthorize(PandaAuthorizeParam params) {
        PandaPayAuthorizeResult pandaPayAuthorizeResult = new PandaPayAuthorizeResult();
        try {
            pandaPayAuthorizeResult.setPayable(true);
            return pandaPayAuthorizeResult;
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController yeahPay]", e);
            return pandaPayAuthorizeResult;
        }
    }
}
