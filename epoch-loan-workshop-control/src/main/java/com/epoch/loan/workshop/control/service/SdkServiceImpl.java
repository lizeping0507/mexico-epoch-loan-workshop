package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.SdkPushInfoParams;
import com.epoch.loan.workshop.common.params.params.request.SdkUploadParams;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.SdkPushInfoResult;
import com.epoch.loan.workshop.common.service.SdkService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.control.service
 * @className : SdkServiceImpl
 * @createTime : 22/3/30 15:26
 * @description : SDK相关
 */
@DubboService(timeout = 5000)
public class SdkServiceImpl extends BaseService implements SdkService {

    /**
     * SDK上传同步回调
     *
     * @param params 入参
     * @return 上传结果
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> sdkPuloadCallBack(SdkUploadParams params) throws Exception {
        // 结果集
        Result<Object> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_SDK_REPORT_SYNC;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("userId", params.getUserId());
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("reportStatus", params.getReportStatus());
        requestParam.put("code", params.getCode());
        requestParam.put("message", params.getMessage());
        requestParam.put("type", params.getType());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());


        // 封装请求头
        Map<String, String> headers = new HashMap<>(1);
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

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
    @Override
    public Result<SdkPushInfoResult> sdkIsPushInfo(SdkPushInfoParams params) throws Exception {
        // 结果集
        Result<SdkPushInfoResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_SDK_PUSH_DETAIL_INFO;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("userId", params.getUserId());
        requestParam.put("orderNo", params.getOrderNo());
        requestParam.put("productId", params.getProductId());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        // 封装请求头
        Map<String, String> headers = new HashMap<>(1);
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, SdkPushInfoResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        SdkPushInfoResult infoResult = JSONObject.parseObject(data.toJSONString(), SdkPushInfoResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(infoResult);
        return result;
    }
}
