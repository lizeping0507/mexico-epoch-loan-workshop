package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.SdkPushInfoParams;
import com.epoch.loan.workshop.common.params.params.request.SdkUploadParams;
import com.epoch.loan.workshop.common.params.result.Result;
import com.epoch.loan.workshop.common.params.result.SdkPushInfoResult;

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
     * @throws Exception 请求异常
     */
    Result<Object> sdkPuloadCallBack(SdkUploadParams params) throws Exception;

    /**
     * 是否推送基本信息
     * <p>
     * 1、判断当前产品是否是a、b公司，如果是a、b公司，通知ape调用银行卡列表接口。
     * 2、如果当前产品非a、b公司，判断当前是否推送基本信息，如果推送，通知app调用银行卡列表接口；
     * 如果未推送，去缓存获取用户模型分及客群，满足进件的条件推送基本信息，并且通知app调用银行卡列表接口，
     * 不满足条件或者缓存中没有数据，通知app调用推荐产品列表接口（展示a、b公司产品）。
     *
     * @param params 入参
     * @return 跳转推荐列表还是银行卡列表
     * @throws Exception 请求异常
     */
    Result<SdkPushInfoResult> sdkIsPushInfo(SdkPushInfoParams params) throws Exception;
}
