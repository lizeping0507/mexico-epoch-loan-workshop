package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.SdkPushInfoParams;
import com.epoch.loan.workshop.common.params.params.request.SdkUploadParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.SdkPushInfoResult;
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
    @Authentication
    @PostMapping(URL.SDK_UPLOAD_CALLBACK)
    public Result<Object> sdkUploadCallBack(SdkUploadParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        try {
            // 验证请求参数是否合法
            if (!params.isOrderNoLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":orderNo");
                return result;
            }

            if (!params.isReportStatusLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":reportStatus");
                return result;
            }

            if (!params.isCodeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":code");
                return result;
            }

            if (!params.isMessageLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":message");
                return result;
            }

            if (!params.isTypeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":type");
                return result;
            }

            return sdkService.sdkUploadCallBack(params);
        } catch (Exception e) {
            LogUtil.sysError("[SdkController sdkUploadCallBack]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

    }
}
