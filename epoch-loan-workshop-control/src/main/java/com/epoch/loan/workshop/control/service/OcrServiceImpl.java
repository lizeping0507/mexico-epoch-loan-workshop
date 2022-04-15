package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.MineParams;
import com.epoch.loan.workshop.common.params.params.request.UserLivenessScoreParams;
import com.epoch.loan.workshop.common.params.result.ChannelTypeResult;
import com.epoch.loan.workshop.common.params.result.LicenseResult;
import com.epoch.loan.workshop.common.params.result.Result;
import com.epoch.loan.workshop.common.params.result.UserLivenessScoreResult;
import com.epoch.loan.workshop.common.service.OcrService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.control.service
 * @className : IdCardServiceImpl
 * @createTime : 2022/03/28 15:53
 * @Description: OCR认证
 */
@DubboService(timeout = 5000)
public class OcrServiceImpl extends BaseService implements OcrService {

    /**
     * 获取用户OCR认证提供商
     *
     * @param params 查询OCR提供商封装类
     * @return 本次使用哪个OCR提供商
     * @throws Exception
     */
    @Override
    public Result<ChannelTypeResult> getOcrChannelType(MineParams params) throws Exception {
        // 结果集
        Result<ChannelTypeResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_OCR_CHANNEL_TYPE;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ChannelTypeResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ChannelTypeResult channelTypeResult = JSONObject.parseObject(data.toJSONString(), ChannelTypeResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(channelTypeResult);
        return result;
    }

    /**
     * 获取advance的license
     *
     * @param params license请求参数
     * @return advance的license
     * @throws Exception
     */
    @Override
    public Result<LicenseResult> advanceLicense(BaseParams params) throws Exception {
        // 结果集
        Result<LicenseResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_OCR_ADVANCE_LICENSE;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, LicenseResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        LicenseResult licenseResult = JSONObject.parseObject(data.toJSONString(), LicenseResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(licenseResult);
        return result;
    }

    /**
     * advance ：活体识别，获取活体分，并进行判断是否需要重新做活体
     *
     * @param params advance获取活体分封装类
     * @return 查询活体分结果
     * @throws Exception
     */
    @Override
    public Result<UserLivenessScoreResult> advanceLivenessScore(UserLivenessScoreParams params) throws Exception {
        // 结果集
        Result<UserLivenessScoreResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_OCR_ADVANCE_LIVENESS_SCORE;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());
        requestParam.put("aadharFaceMatch", params.getLivenessId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, UserLivenessScoreResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        UserLivenessScoreResult scoreResult = JSONObject.parseObject(data.toJSONString(), UserLivenessScoreResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(scoreResult);
        return result;
    }

}
