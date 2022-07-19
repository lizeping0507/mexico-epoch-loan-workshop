package com.epoch.loan.workshop.control.service;

import cn.hutool.core.collection.CollectionUtil;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.LoanAppConfigEntity;
import com.epoch.loan.workshop.common.params.params.request.SmsCodeParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.service.ShortMessageService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Result<Object> sendRegisterMessage(SmsCodeParams params) throws Exception {
        // 结果结果集
        Result<Object> result = new Result<>();

        // 发送验证码
        String smsCode = smsManager.sendVerificationCode(params.getMobile());

        if (StringUtils.isBlank(smsCode)) {
            result.setReturnCode(ResultEnum.SMS_CODE_SEND_FAILED.code());
            result.setMessage(ResultEnum.SMS_CODE_SEND_FAILED.message());
            return result;
        }

        // 缓存
        String key = String.format(RedisKeyField.SMS_CODE_TEMPLATE, params.getAppName(), params.getMobile());
        redisClient.set(key, smsCode, 60 * 5);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }


    /**
     * 加载 app 相关配置
     *
     * @param appName    包名
     * @param configName 配置名称
     * @return
     */
    public String getAppConfig(String appName, String configName) {
        Object appConfig = redisClient.hget(RedisKeyField.APP_CONFIG + appName, configName);
        if (ObjectUtils.isNotEmpty(appConfig)) {
            return appConfig.toString();
        }

        // 获取app全部配置信息,添加进入redis中
        List<LoanAppConfigEntity> appConfigList = loanAppConfigDao.findByAppName(appName);
        if (CollectionUtil.isNotEmpty(appConfigList)) {
            Map<String, Object> collect = appConfigList.stream().
                    filter(appConfigEntity -> StringUtils.isBlank(appConfigEntity.getConfigKey()) || StringUtils.isBlank(appConfigEntity.getConfigValue()))
                    .collect(Collectors.toMap(appConfigEntity -> appConfigEntity.getConfigKey(), appConfigEntity -> appConfigEntity.getConfigValue()));
            redisClient.hmset(RedisKeyField.APP_CONFIG + appName, collect,60 * 60 );
        }

        return redisClient.hget(RedisKeyField.APP_CONFIG + appName, configName).toString();
    }
}
