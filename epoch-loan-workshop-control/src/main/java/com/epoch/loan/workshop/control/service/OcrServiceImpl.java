package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.LoanOcrProviderConfig;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.MineParams;
import com.epoch.loan.workshop.common.params.params.request.UserLivenessScoreParams;
import com.epoch.loan.workshop.common.params.params.result.ChannelTypeResult;
import com.epoch.loan.workshop.common.params.params.result.LicenseResult;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.params.params.result.UserLivenessScoreResult;
import com.epoch.loan.workshop.common.service.OcrService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        List<LoanOcrProviderConfig> thirdConfigList = loanOcrProviderConfigDao.findProviderConfig();

        if (CollectionUtils.isNotEmpty(thirdConfigList)) {
            LoanOcrProviderConfig  thirdConfig = chooseByWeight(thirdConfigList);

            if (ObjectUtils.isNotEmpty(thirdConfig)) {
                // 封装结果
                result.setReturnCode(ResultEnum.SUCCESS.code());
                result.setMessage(ResultEnum.SUCCESS.message());
                result.setData(new ChannelTypeResult(thirdConfig.getChannel()));
            }
        }

        // 无可用渠道配置
        LogUtil.sysInfo("根据权重策略选择OCR渠道:{}", "空=====");
        result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
        result.setMessage(ResultEnum.SYSTEM_ERROR.message());
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


    /**
     * 根据权重 递归 挑选渠道
     *
     * @param providerConfigList 渠道权重列表
     * @return 渠道配置
     */
    private LoanOcrProviderConfig chooseByWeight(List<LoanOcrProviderConfig> providerConfigList) {
        LoanOcrProviderConfig res = null;

        // 查询渠道列表
        // 随机范围 = 渠道权重和
        int range = providerConfigList.stream().mapToInt(LoanOcrProviderConfig::getProportion).sum();
        LogUtil.sysInfo("providerConfig range:{}", range);
        if (range == 0) {
            return null;
        }

        // 取随机数
        Random random = new Random();
        int randomNum = random.nextInt(range) + 1;;
        LogUtil.sysInfo("providerConfig randomNum:{}", randomNum);

        // 选择渠道
        int start = 0;
        for (LoanOcrProviderConfig entity : providerConfigList) {
            Integer proportion = entity.getProportion();
            if (randomNum > start && randomNum <= (start + proportion)) {
                res = entity;
                break;
            } else {
                start += proportion;
            }
        }
        LogUtil.sysInfo("providerConfig res:{}", res);

        return res;
    }

}
