package com.epoch.loan.workshop.common.sms.channel;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.sms.channel
 * @className : SMSChannel
 * @createTime : 2022/4/20 14:31
 * @description : 短信渠道接口
 */
public interface SMSChannel {

    /**
     * 发送短信验证码
     *
     * @param code
     * @param mobile
     * @return
     * @throws Exception
     */
    Situation sendVerificationCode(String code, String mobile) throws Exception;
}
