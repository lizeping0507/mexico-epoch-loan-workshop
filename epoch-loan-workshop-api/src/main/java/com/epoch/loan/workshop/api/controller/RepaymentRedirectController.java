package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.RepaymentParams;
import com.epoch.loan.workshop.common.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller;
 * @className : RepaymentRedirectController
 * @createTime : 2022/3/5 11:28
 * @description : 还款订单发起
 */
@RestController
@RequestMapping(URL.REPAYMENT)
public class RepaymentRedirectController extends BaseController {
    /**
     * YeahPay 支付回调
     *
     * @param params yeahPay支付回调参数
     * @return String
     */
    @GetMapping(value = URL.PREPAY)
    public Object redirectPage(RepaymentParams params, HttpServletResponse response) {

        LogUtil.sysInfo(URL.REPAYMENT + URL.PREPAY + " params : {}", JSONObject.toJSONString(params));

        // 结果集
        Result<?> result = new Result<>();
        LogUtil.sysInfo("+++++++{}", params);
        try {
            String payUrl = repaymentService.repayment(params);
            LogUtil.sysInfo("payUrl: {}", payUrl);
            // 重定向至支付页面
            response.sendRedirect(payUrl);
            return null;
        } catch (Exception e) {
            LogUtil.sysError("[RepaymentRedirectController yeahPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
