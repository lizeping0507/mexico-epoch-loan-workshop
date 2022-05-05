package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.ApplyParams;
import com.epoch.loan.workshop.common.params.params.request.UserTypeParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.UserTypeResult;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部服务接口
 */
@RestController
@RequestMapping(URL.INTERNAL)
public class InternalController extends BaseController {
    /**
     * 获取用户客群
     *
     * @param params
     * @return
     */
    @PostMapping(URL.USER_TYPE)
    public Result<UserTypeResult> userType(UserTypeParams params) {
        // 结果集
        Result result = new Result();

        try {

            // 获取用户客群
            return productService.getUserType(params);
        } catch (Exception e) {
            LogUtil.sysError("[OrderController apply]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
