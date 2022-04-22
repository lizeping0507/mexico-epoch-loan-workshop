package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.SendRegisterMessageParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.service.ShortMessageService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.control.service
 * @className : ShortMessageServiceImpl
 * @createTime : 2022/3/23 11:04
 * @description : 手机短信
 */
@DubboService(timeout = 5000)
public class ShortMessageServiceImpl extends BaseService implements ShortMessageService {

    /**
     * 发送注册短信
     *
     * @param params 入参
     * @return 发送结果
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> sendRegisterMessage(SendRegisterMessageParams params) throws Exception {
        // 结果结果集
        Result<Object> result = new Result<>();

        // 发送验证码
        String smsCode = smsManager.sendVerificationCode(params.getPhoneNumber());

        // 缓存
        String key = String.format(RedisKeyField.SMS_CODE_TEMPLATE, params.getAppName(), params.getPhoneNumber());
        redisClient.set(key, smsCode, 60 * 5);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

}
