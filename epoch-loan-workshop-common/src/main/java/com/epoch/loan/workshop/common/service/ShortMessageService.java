package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.SendRegisterMessageParams;
import com.epoch.loan.workshop.common.params.result.Result;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.service
 * @className : ShortMessageService
 * @createTime : 2022/3/23 11:02
 * @description : 手机短信
 */
public interface ShortMessageService {

    /**
     * 发送注册短信
     *
     * @param sendRegisterMessageParams 入参
     * @return 发送结果
     * @throws Exception 请求异常
     */
    Result<Object> sendRegisterMessage(SendRegisterMessageParams sendRegisterMessageParams) throws Exception;
}
