package com.epoch.loan.workshop.common.util;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.result.Result;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.util;
 * @className : PlatformUtil
 * @createTime : 2022/3/28 21:25
 * @description : 贷超相关工具类
 */
public class PlatformUtil {

    /**
     * 校验响应码
     *
     * @param result       预定义结果集
     * @param responseJson 响应Json
     * @return boolean
     */
    public static <T> boolean checkResponseCode(Result<T> result, Class<T> clazz, JSONObject responseJson) {
        Integer code = responseJson.getInteger("code");
        switch (code) {
            case 200:
                // 成功
                return true;
            case 400:
                // 自定异常
                result.setReturnCode(ResultEnum.SERVICE_ERROR.code());
                result.setMessage(getMsg(responseJson, ResultEnum.SERVICE_ERROR));
                result.setData(responseJson.getObject("data", clazz));
                return false;
            case 4002:
            case 405:
                // 登录状态异常
                result.setReturnCode(ResultEnum.NO_LOGIN.code());
                result.setMessage(getMsg(responseJson, ResultEnum.NO_LOGIN));
                return false;
            case 4003:
            case 4004:
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(getMsg(responseJson, ResultEnum.PARAM_ERROR));
                return false;
            case 402:
                result.setReturnCode(ResultEnum.TIMEOUT_ERROR.code());
                result.setMessage(getMsg(responseJson, ResultEnum.TIMEOUT_ERROR));
                return false;
            default:
                result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
                result.setMessage(getMsg(responseJson, ResultEnum.SYSTEM_ERROR));
                return false;
        }
    }

    private static String getMsg(JSONObject responseJson, ResultEnum resultEnum) {
        String msg = responseJson.getString("msg");
        if (StringUtils.isEmpty(msg)) {
            return resultEnum.message();
        }
        return msg;
    }

}
