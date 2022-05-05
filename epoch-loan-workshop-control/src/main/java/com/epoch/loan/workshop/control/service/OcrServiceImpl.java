package com.epoch.loan.workshop.control.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.OcrChannelConfigStatus;
import com.epoch.loan.workshop.common.constant.OcrField;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.elastic.OcrLivingDetectionLogElasticEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOcrProviderConfig;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.UserLivenessScoreParams;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.params.params.result.model.AdvanceLicenseResponse;
import com.epoch.loan.workshop.common.service.OcrService;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;

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
     */
    @Override
    public Result<ChannelTypeResult> getOcrChannelType(BaseParams params) {
        // 结果集
        Result<ChannelTypeResult> result = new Result<>();
        List<LoanOcrProviderConfig> thirdConfigList = loanOcrProviderConfigDao.findProviderConfigListByAppNameAndStatus(params.getAppName(), OcrChannelConfigStatus.START);

        // 该APP是否有可用聚道
        if (CollectionUtils.isEmpty(thirdConfigList)) {
            result.setReturnCode(ResultEnum.CHANNEL_UN_DO_ERROR.code());
            result.setMessage(ResultEnum.CHANNEL_UN_DO_ERROR.message());
            return result;
        }

        LoanOcrProviderConfig thirdConfig = chooseByWeight(thirdConfigList);

        // 是否选举出可用聚道
        if (ObjectUtils.isEmpty(thirdConfig)) {
            result.setReturnCode(ResultEnum.CHANNEL_UN_DO_ERROR.code());
            result.setMessage(ResultEnum.CHANNEL_UN_DO_ERROR.message());
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(new ChannelTypeResult(thirdConfig.getChannelCode()));
        return result;
    }

    /**
     * 获取advance的license
     *
     * @param params license请求参数
     * @return advance的license
     * @throws Exception 请求异常
     */
    @Override
    public Result<LicenseResult> advanceLicense(BaseParams params) throws Exception {
        // 结果集
        Result<LicenseResult> result = new Result<>();
        String userId = params.getUser().getId();

        // 先查询 redis中 license是否过期
        String licenseExpireTimeCache = getLicenseExpireTimeCache();
        if (StringUtils.isNotBlank(licenseExpireTimeCache)) {
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(new LicenseResult(licenseExpireTimeCache));
            return result;
        }

        // license过期或者为空时,请求advance
        String appName = params.getAppName();
        String appPackageName = getAdvanceConfig(appName, OcrField.ADVANCE_APP_PACKAGE_NAME);
        String licenseUrl = getAdvanceConfig(appName, OcrField.ADVANCE_LICENSE_URL);

        // 封装请求头 和 请求参数
        Map<String, String> headers = getAdvanceHeard(params.getAppName());
        HashMap<String, String> param = Maps.newHashMap();
        param.put(OcrField.ADVANCE_APP_ID_KEY, appPackageName);
        param.put(OcrField.ADVANCE_LICENSE_EFFECTIVE_SECONDS, OcrField.ADVANCE_LICENSE_SECONDS);

        // 发送请求
        String response = HttpUtils.POST_WITH_HEADER_NOCHARSET(licenseUrl, JSON.toJSONString(param), headers);
        if (StringUtils.isBlank(response)) {
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
        AdvanceLicenseResult licenseResult = JSONObject.parseObject(response, AdvanceLicenseResult.class);

        // 日志写入Elastic
        OcrLivingDetectionLogElasticEntity livingDetectionLog = new OcrLivingDetectionLogElasticEntity();
        BeanUtils.copyProperties(licenseResult, livingDetectionLog);
        livingDetectionLog.setRequestUrl(licenseUrl);
        livingDetectionLog.setRequestParam(param);
        livingDetectionLog.setRequestHeard(headers);
        livingDetectionLog.setResponse(response);
        livingDetectionLog.setUserId(userId);
        livingDetectionLog.setCreateTime(new Date());
        ocrLivingDetectionLogElasticDao.save(livingDetectionLog);

        // 判断是否请求成功
        String code = licenseResult.getCode();
        if (!OcrField.ADVANCE_SUCCESS_CODE.equalsIgnoreCase(code)) {
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

        // 判断响应信息
        AdvanceLicenseResponse licenseResponse = licenseResult.getData();
        if (ObjectUtils.isEmpty(licenseResponse)) {
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

        // 判断授权码和过期时间
        Long expireTimestamp = licenseResponse.getExpireTimestamp();
        licenseExpireTimeCache = licenseResponse.getLicense();
        if (ObjectUtils.isEmpty(expireTimestamp) || StringUtils.isBlank(licenseExpireTimeCache)) {
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
        setLicenseExpireTimeStore(expireTimestamp, licenseExpireTimeCache);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(new LicenseResult(licenseExpireTimeCache));
        return result;
    }

    /**
     * advance ：活体识别，获取活体分，并进行判断是否需要重新做活体
     *
     * @param params advance获取活体分封装类
     * @return 查询活体分结果
     * @throws Exception 请求异常
     */
    @Override
    public Result<UserLivenessScoreResult> advanceLivenessScore(UserLivenessScoreParams params) throws Exception {
        // 结果集
        Result<UserLivenessScoreResult> result = new Result<>();
        String scoreUrl = getAdvanceConfig(params.getAppName(), OcrField.ADVANCE_LIVENESS_SCORE_URL);
        String livenessThreshold = getAdvanceConfig(params.getAppName(), OcrField.LIVENESS_THRESHOLD);
        String userId = params.getUser().getId();

        // 封装请求参数
        HashMap<String, String> param = Maps.newHashMap();
        param.put(OcrField.ADVANCE_FACE_IMAGE_ID, params.getLivenessId());
        param.put(OcrField.ADVANCE_RESULT_TYPE, OcrField.ADVANCE_DEFAULT_IMAGE_TYPE);

        // 封装请求头
        Map<String, String> headers = getAdvanceHeard(params.getAppName());

        // 发送请求
        String responseStr = HttpUtils.POST_WITH_HEADER_NOCHARSET(scoreUrl, JSON.toJSONString(param), headers);

        // 处理响应结果
        if (StringUtils.isBlank(responseStr)) {
            result.setReturnCode(ResultEnum.KYC_Liveness_ERROR.code());
            result.setMessage(ResultEnum.KYC_Liveness_ERROR.message());
            return result;
        }
        AdvanceScoreResult scoreResult = JSONObject.parseObject(responseStr, AdvanceScoreResult.class);

        // 日志写入Elastic
        OcrLivingDetectionLogElasticEntity livingDetectionLog = new OcrLivingDetectionLogElasticEntity();
        BeanUtils.copyProperties(scoreResult, livingDetectionLog);
        livingDetectionLog.setRequestUrl(scoreUrl);
        livingDetectionLog.setRequestParam(param);
        livingDetectionLog.setRequestHeard(headers);
        livingDetectionLog.setResponse(responseStr);
        livingDetectionLog.setUserId(userId);
        livingDetectionLog.setCreateTime(new Date());
        ocrLivingDetectionLogElasticDao.save(livingDetectionLog);

        // 判断是否响应成功
        String code = scoreResult.getCode();
        if (!OcrField.ADVANCE_SUCCESS_CODE.equalsIgnoreCase(code)) {
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

        // 获取响应分数
        UserLivenessScoreResult data = scoreResult.getData();
        String livenessScore = data.getLivenessScore();
        if (ObjectUtils.isEmpty(livenessScore)) {
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

        // 判断响应分数是否大于建议阀值
        if (new BigDecimal(livenessScore).compareTo(new BigDecimal(livenessThreshold)) <= 0) {
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(data);
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
        if (range == 0) {
            return null;
        }

        // 取随机数
        Random random = new Random();
        int randomNum = random.nextInt(range) + 1;

        // 选择渠道
        int start = 0;
        for (LoanOcrProviderConfig entity : providerConfigList) {
            Integer proportion = entity.getProportion();
            if (randomNum > start && randomNum <= (start + proportion)) {
                res = entity;
                break;
            }
            start += proportion;
        }

        return res;
    }

    /**
     * 获取advance 请求头
     *
     * @param appName app标识
     * @return 封装的请求头
     */
    public Map<String, String> getAdvanceHeard(String appName) {
        HashMap<String, String> headers = Maps.newHashMap();
        String advanceConfig = getAdvanceConfig(appName, OcrField.ADVANCE_ACCESS_KEY);
        headers.put(OcrField.ADVANCE_ACCESS_KEY_KEY, advanceConfig);
        headers.put(HTTP.CONTENT_TYPE, HttpUtils.CONTENT_TYPE_JSON);
        return headers;
    }

    /**
     * 获取advance license
     *
     * @return 获取redis中存储的 advance授权码
     */
    public String getLicenseExpireTimeCache() {
        String license = null;
        boolean hasKey = redisClient.hasKey(RedisKeyField.ADVANCE_LICENSE);

        // 是否存在key
        if (!hasKey) {
            return license;
        }

        // 是否存在value
        Map<Object, Object> values = redisClient.hmget(RedisKeyField.ADVANCE_LICENSE);
        if (MapUtils.isEmpty(values)) {
            return license;
        }

        // 获取时间
        Object object = values.get(OcrField.ADVANCE_LICENSE_EXPIRE_TIME);
        if (object == null) {
            return license;
        }

        // 不过期 生成license
        long expireTime = Long.parseLong(String.valueOf(object));
        if (expireTime > System.currentTimeMillis()) {
            Object object2 = values.get(OcrField.ADVANCE_LICENSE_NAME);
            if (object2 != null) {
                license = String.valueOf(object2);
            }
        } else {
            redisClient.del(RedisKeyField.ADVANCE_LICENSE);
        }
        return license;
    }

    /**
     * 存储advance license
     *
     * @param expireTime advance响应的过期时间点
     * @param license    advance授权码
     */
    public void setLicenseExpireTimeStore(Long expireTime, String license) {
        if (expireTime == null || StringUtils.isBlank(license)) {
            return;
        }

        try {
            boolean hasKey = redisClient.hasKey(RedisKeyField.ADVANCE_LICENSE);
            if (hasKey) {
                redisClient.del(RedisKeyField.ADVANCE_LICENSE);
            }

            HashMap<Object, Object> map = Maps.newHashMap();
            map.put(OcrField.ADVANCE_LICENSE_NAME, license);
            map.put(OcrField.ADVANCE_LICENSE_EXPIRE_TIME, expireTime);
            redisClient.hmset(RedisKeyField.ADVANCE_LICENSE, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取advance 指定配置
     *
     * @param appName   app标识
     * @param configKey 指定配置名称
     * @return 指定配置值
     */
    private String getAdvanceConfig(String appName, String configKey) {
        // 获取advance相关配置
        String advanceConfig = loanOcrProviderConfigDao.selectAdvanceConfigByAppNameAndStatus(appName, OcrChannelConfigStatus.START);
        String result = null;
        if (StringUtils.isNotBlank(advanceConfig)) {
            JSONObject config = JSONObject.parseObject(advanceConfig);
            result = config.getString(configKey);
        }
        return result;
    }
}
