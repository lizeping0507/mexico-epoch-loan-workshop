package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.params.params.request.PandaRepaymentCallbackParam;
import com.epoch.loan.workshop.common.params.params.result.PandaRepaymentCallbackResult;
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
    public PandaRepaymentCallbackResult pandaPay(PandaRepaymentCallbackParam params) {
        // 结果集
        PandaRepaymentCallbackResult result = new PandaRepaymentCallbackResult();

        try {
            result.setPayable(true);
            result.setMax_amount(100000);
            result.setMin_amount(5000);
            return result;
        } catch (Exception e) {
            result.setPayable(false);
            return result;
        }
    }
}
