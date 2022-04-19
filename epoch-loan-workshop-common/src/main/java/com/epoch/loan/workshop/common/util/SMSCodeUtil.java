package com.epoch.loan.workshop.common.util;

import com.epoch.loan.workshop.common.constant.RedisKeyField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.util
 * @className : SMSCodeUtil
 * @createTime : 2021/11/3 16:03
 * @description : 验证码工具类
 */
@Component
public class SMSCodeUtil {

    /**
     * Redis工具类
     */
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 更新注册用验证码
     *
     * @param phoneNumber 手机号
     * @param appName     App名称
     * @param smsCode     验证码
     * @param time        有效时间
     */
    public void updateRegisterCode(String phoneNumber, String appName, String smsCode, long time) {
        redisUtil.set(RedisKeyField.REGISTER_SMS_CODE + appName + RedisKeyField.SPLIT + phoneNumber, smsCode,time);
    }

    /**
     * 获取注册用验证码
     *
     * @param phoneNumber 手机号
     * @param appName     App名称
     * @return 验证码
     */
    public String getRegisterCode(String phoneNumber, String appName) {
        Object smsCodeObject = redisUtil.get(RedisKeyField.REGISTER_SMS_CODE + appName + RedisKeyField.SPLIT + phoneNumber);

        if (ObjectUtils.isEmpty(smsCodeObject)) {
            return null;
        }

        // 用户ID
        String smsCode = String.valueOf(smsCodeObject);
        if (StringUtils.isEmpty(smsCode)){
            return null;
        }
        return smsCode;
    }
}
