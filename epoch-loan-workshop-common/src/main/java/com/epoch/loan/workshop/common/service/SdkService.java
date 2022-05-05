package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.SdkPushInfoParams;
import com.epoch.loan.workshop.common.params.params.request.SdkUploadParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.SdkPushInfoResult;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.api.controller
 * @className : SdkController
 * @createTime : 22/3/30 15:01
 * @description : SDK相关
 */
public interface SdkService {

    /**
     * SDK上传同步回调
     *
     * @param params 入参
     * @return 上传结果
     */
    Result<Object> sdkUploadCallBack(SdkUploadParams params);
}
