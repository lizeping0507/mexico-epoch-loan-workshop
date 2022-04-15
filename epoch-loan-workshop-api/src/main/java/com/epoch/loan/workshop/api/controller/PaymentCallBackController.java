package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.result.Result;
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
     * YeahPay 支付回调
     *
     * @param params yeahPay支付回调参数
     * @return String
     */
    @PostMapping(URL.YEAHPAY)
    public Object yeahPay(YeahPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.yeahPay(params);
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
     * InPay 支付回调
     *
     * @param params inPay支付回调参数
     * @return String
     */
    @PostMapping(URL.INPAY)
    public Object inPay(InPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.inPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController inPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * sunFlowerPay支付回调
     *
     * @return
     */
    @PostMapping(URL.SUNFLOWERPAY)
    public Object sunFlowerPay(SunFlowerPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.sunFlowerPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController sunFlowerPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * OceanPay 支付回调
     *
     * @param params oceanPay支付回调参数
     * @return String
     */
    @PostMapping(URL.OCEANPAY)
    public Object oceanPay(OceanPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.oceanPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController oceanPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * AcPay 支付回调
     *
     * @param params acPay支付回调参数
     * @return String
     */
    @PostMapping(URL.ACPAY)
    public Object acPay(AcPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.acPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController acPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * IncashPay 支付回调
     *
     * @param params incashPay支付回调参数
     * @return String
     */
    @PostMapping(URL.INCASHPAY)
    public Object incashPay(IncashPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.incashPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController incashPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * IncashXjdPay 支付回调
     *
     * @param params incashXjdPay支付回调参数
     * @return String
     */
    @PostMapping(URL.INCASHXJDPAY)
    public Object incashXjdPay(IncashPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.incashXjdPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController incashXjdPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }


    /**
     * TrustPay 支付回调
     *
     * @param params trustPay支付回调参数
     * @return String
     */
    @PostMapping(URL.TRUSTPAY)
    public Object trustPay(TrustPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.trustPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController trustPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * QePay 支付回调
     *
     * @param params qePay支付回调参数
     * @return String
     */
    @PostMapping(URL.QEPAY)
    public Object qePay(QePayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.qePay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController qePay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * hrPay 支付回调
     *
     * @param params hrPay支付回调参数
     * @return String
     */
    @PostMapping(URL.HRPAY)
    public Object hrPay(HrPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.hrPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController hrPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * globPay 支付回调
     *
     * @param params globPay支付回调参数
     * @return String
     */
    @PostMapping(URL.GLOBPAY)
    public Object globPay(GlobPayCallBackParams params) {
        // 结果集
        Result<?> result = new Result<>();

        try {
            return paymentCallbackService.globPay(params);
        } catch (Exception e) {
            LogUtil.sysError("[PaymentCallBackController globPay]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
