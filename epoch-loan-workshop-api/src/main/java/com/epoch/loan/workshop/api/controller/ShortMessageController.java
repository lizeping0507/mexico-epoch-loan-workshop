package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.SendRegisterMessageParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.controller
 * @className : ShortMessageController
 * @createTime : 2022/3/23 10:53
 * @description : 手机短息
 */
@RestController
@RequestMapping(URL.SHORT_MESSAGE)
public class ShortMessageController extends BaseController {

    /**
     * 发送注册短信
     *
     * @param sendRegisterMessageParams 入参
     * @return 发送结果
     */
    @PostMapping(URL.SEND_REGISTER_MESSAGE)
    public Result<Object> sendRegisterMessage(SendRegisterMessageParams sendRegisterMessageParams) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            // 发送注册短信
            return shortMessageService.sendRegisterMessage(sendRegisterMessageParams);
        } catch (Exception e) {
            LogUtil.sysError("[ShortMessageController sendRegisterMessage]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
