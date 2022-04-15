package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.SdkPushInfoParams;
import com.epoch.loan.workshop.common.params.params.request.SdkUploadParams;
import com.epoch.loan.workshop.common.params.result.Result;
import com.epoch.loan.workshop.common.params.result.SdkPushInfoResult;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.api.controller
 * @className : SdkController
 * @createTime : 22/3/30 15:01
 * @description : SDK相关
 */
@RestController
@RequestMapping(URL.SDK)
public class SdkController extends BaseController {

    /**
     * SDK上传同步回调
     *
     * @param params 入参
     * @return 上传结果
     */
    @PostMapping(URL.SDK_UPLOAD_CALLBACK)
    public Result<Object> sdkPuloadCallBack(SdkUploadParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {

            return sdkService.sdkPuloadCallBack(params);
        } catch (Exception e) {
            LogUtil.sysError("[SdkController sdkPuloadCallBack]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

    }

    /**
     * 是否推送基本信息
     *
     * @param params 入参
     * @return 跳转推荐列表还是银行卡列表
     */
    @PostMapping(URL.SDK_IS_PUSH_INFO)
    public Result<SdkPushInfoResult> sdkIsPushInfo(SdkPushInfoParams params) {
        // 结果集
        Result<SdkPushInfoResult> result = new Result<>();

        try {

            return sdkService.sdkIsPushInfo(params);
        } catch (Exception e) {
            LogUtil.sysError("[SdkController sdkIsPushInfo]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

    }
}
