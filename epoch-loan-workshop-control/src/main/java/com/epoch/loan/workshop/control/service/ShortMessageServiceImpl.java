package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.SendRegisterMessageParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.service.ShortMessageService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.PlatformUtil;
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

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_SMSCODE;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("phoneNumber", params.getPhoneNumber());

        // 请求
        String responseStr = HttpUtils.POST(url, requestParam.toJSONString());

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, Object.class, responseJson)) {
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }
}
