package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.PandaPayH5Params;
import com.epoch.loan.workshop.common.params.params.request.UtrParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.api.controller
 * @className : RepaymentController
 * @createTime : 2022/03/29 19:19
 * @Description: 还款相关
 */
@RestController
@RequestMapping(URL.REPAYMENT)
public class RepaymentController extends BaseController {

    /**
     * 调支付提供的UTR接口
     *
     * @param params UTR入参
     * @return String
     */
    @PostMapping(URL.REPAY_UTR)
    public Result<Object> repayUtr(UtrParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            return repaymentService.repayUtr(params);
        } catch (Exception e) {
            LogUtil.sysError("[RepaymentController repayUtr]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            result.setEx(e.getMessage());
            return result;
        }
    }

    /**
     * pandaPay OXXO方式H5回调
     *
     * @param params UTR入参
     * @return String
     */
    @PostMapping(URL.PANDAPAY_OXXO_H5)
    public Result<Object> pandaPayOxxoH5(PandaPayH5Params params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            return repaymentService.pandaPayOxxoH5(params);
        } catch (Exception e) {
            LogUtil.sysError("[RepaymentController pandaPayOxxoH5]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            result.setEx(e.getMessage());
            return result;
        }
    }

}
