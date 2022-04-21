package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.epoch.loan.workshop.common.constant.OcrChannelConfigStatus;
import com.epoch.loan.workshop.common.constant.OcrField;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.elastic.OcrLivingDetectionLogElasticEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.params.params.result.model.AdvanceFaceComparisonResponse;
import com.epoch.loan.workshop.common.params.params.result.model.AdvanceOcrBackInfoResult;
import com.epoch.loan.workshop.common.params.params.result.model.AdvanceOcrFrontInfoResult;
import com.epoch.loan.workshop.common.params.params.result.model.AdvanceOcrInfoResponse;
import com.epoch.loan.workshop.common.service.UserService;
import com.epoch.loan.workshop.common.util.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.account.service;
 * @className : UserServiceImpl
 * @createTime : 2022/3/21 11:53
 * @description : 用户相关业务 实现
 */
@DubboService(timeout = 5000)
public class UserServiceImpl extends BaseService implements UserService {

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;

    /**
     * 判断手机号是否已经注册
     *
     * @param params 请求参数封装
     * @return Result<IsRegisterResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<IsRegisterResult> isRegister(IsRegisterParams params) throws Exception {
        // 结果集
        Result<IsRegisterResult> result = new Result<>();
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        IsRegisterResult resData = new IsRegisterResult();
        result.setData(resData);

        // 根据appName和手机号查询用户
        Integer isExit = loanUserDao.exitByAppNameAndLoginName(params.getAppName(), params.getPhoneNumber());

        if (isExit == 0) {
            // 用户未注册
            resData.setIsExists("10");
        } else {
            // 用户已注册
            resData.setIsExists("20");
        }

        return result;
    }

    /**
     * 用户注册
     *
     * @param params 请求参数封装
     * @return Result<RegisterResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<RegisterResult> register(RegisterParams params) throws Exception {
        // 结果结果集
        Result<RegisterResult> result = new Result<>();

        // 手机号是否已经注册
        Integer isExit = loanUserDao.exitByAppNameAndLoginName(params.getAppName(), params.getMobile());
        LogUtil.sysInfo("用户注册 : isExit {}", JSONObject.toJSONString(isExit));
        if (isExit != 0) {
            result.setReturnCode(ResultEnum.PHONE_EXIT.code());
            result.setMessage(ResultEnum.PHONE_EXIT.message());
            return result;
        }

        // 通过Nacos命名空间判断环境 非生产环境验证码默认使用 0000
        String registerCode;
        if (namespace.contains("dev") || namespace.contains("test")) {
            registerCode = "0000";
        } else {
            registerCode = (String) redisClient.get(RedisKeyField.REGISTER_SMS_CODE + RedisKeyField.SPLIT + params.getAppName() + RedisKeyField.SPLIT + params.getMobile());
            LogUtil.sysInfo("用户注册 : registerCode {}", JSONObject.toJSONString(registerCode));
            // TODO 测试用
            registerCode = "0000";
        }
        if (StringUtils.isEmpty(registerCode) || !registerCode.equals(params.getSmsCode())) {
            result.setReturnCode(ResultEnum.SMSCODE_ERROR.code());
            result.setMessage(ResultEnum.SMSCODE_ERROR.message());
            return result;
        }

        // 生成user记录
        LoanUserEntity user = new LoanUserEntity();
        user.setId(ObjectIdUtil.getObjectId());
        user.setAndroidId(params.getAndroidId());
        user.setChannelId(params.getChannelCode());
        user.setGaId(params.getGaId());
        user.setImei(params.getImei());
        user.setPlatform(params.getPlatform());
        user.setLoginName(params.getMobile());
        user.setPassword(params.getPassword());
        user.setAppName(params.getAppName());
        user.setAppVersion(params.getAppVersion());
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        loanUserDao.insert(user);

        // 生成userInfo记录
        LoanUserInfoEntity userInfo = new LoanUserInfoEntity();
        userInfo.setId(ObjectIdUtil.getObjectId());
        userInfo.setUserId(user.getId());
        userInfo.setMobile(params.getMobile());
        userInfo.setUpdateTime(new Date());
        userInfo.setCreateTime(new Date());
        loanUserInfoDao.insert(userInfo);

        // 生成token
        String token = this.tokenManager.updateUserToken(user.getId());

        // 封装结果
        RegisterResult registerResult = new RegisterResult();
        registerResult.setToken(token);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(registerResult);
        return result;
    }

    /**
     * 忘记密码
     *
     * @param params 请求参数封装
     * @return Result<ChangePasswordResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<ChangePasswordResult> forgotPwd(ForgotPwdParams params) throws Exception {
        // 结果结果集
        Result<ChangePasswordResult> result = new Result<>();

        // 通过Nacos命名空间判断环境 非生产环境验证码默认使用 0000
        String registerCode;
        if (namespace.contains("dev") || namespace.contains("test")) {
            registerCode = "0000";
        } else {
            registerCode = (String) redisClient.get(RedisKeyField.FORGOTPWD_SMS_CODE + RedisKeyField.SPLIT + params.getAppName() + RedisKeyField.SPLIT + params.getPhoneNumber());
            LogUtil.sysInfo("忘记密码 : registerCode {}", JSONObject.toJSONString(registerCode));
            // TODO 测试用
            registerCode = "0000";
        }
        if (StringUtils.isEmpty(registerCode) || !registerCode.equals(params.getSmsCode())) {
            result.setReturnCode(ResultEnum.SMSCODE_ERROR.code());
            result.setMessage(ResultEnum.SMSCODE_ERROR.message());
            return result;
        }

        // 查询用户
        LoanUserEntity user = loanUserDao.findByLoginNameAndAppName(params.getPhoneNumber(), params.appName);
        if (null == user) {
            result.setReturnCode(ResultEnum.PHONE_NO_EXIT.code());
            result.setMessage(ResultEnum.PHONE_NO_EXIT.message());
            return result;
        }

        // 更新密码
        loanUserDao.updatePassword(user.getId(), params.getPasswd());

        // 生成Token
        String token1 = tokenManager.updateUserToken(user.getId());

        // 封装结果
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();
        changePasswordResult.setToken(token1);
        changePasswordResult.setUserId(user.getId());
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(changePasswordResult);
        return result;
    }

    /**
     * 更新密码
     *
     * @param params 请求参数封装
     * @return Result<ChangePasswordResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<ChangePasswordResult> modifyPassword(ModifyPasswordParams params) throws Exception {
        // 结果结果集
        Result<ChangePasswordResult> result = new Result<>();

        // 查询用户
        String token = params.getToken();
        User userCache = tokenManager.getUserCache(token);
        if (null == userCache) {
            result.setReturnCode(ResultEnum.NO_LOGIN.code());
            result.setMessage(ResultEnum.NO_LOGIN.message());
            return result;
        }

        // 对比手机号
        if (!userCache.getLoginName().equals(params.getPhoneNumber())) {
            result.setReturnCode(ResultEnum.PARAM_ERROR.code());
            result.setMessage(ResultEnum.PARAM_ERROR.message());
            return result;
        }

        // 对比旧密码
        if (!userCache.getPassword().equals(params.getOldPassword())) {
            result.setReturnCode(ResultEnum.PASSWORD_INVALID.code());
            result.setMessage("The original password entered is incorrect");
            return result;
        }

        // 对比新密码和新密码确认
        if (!params.getNewPassword().equals(params.getEnterPassword())) {
            result.setReturnCode(ResultEnum.PASSWORD_INVALID.code());
            result.setMessage("The original password entered is incorrect");
            return result;
        }

        // 更新密码
        loanUserDao.updatePassword(userCache.getId(), params.getNewPassword());

        // 生成Token
        String token1 = tokenManager.updateUserToken(userCache.getId());

        // 封装结果
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();
        changePasswordResult.setToken(token1);
        changePasswordResult.setUserId(userCache.getId());
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(changePasswordResult);
        return result;
    }

    /**
     * 密码登录
     *
     * @param params 请求参数封装
     * @return Result<LoginResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<LoginResult> login(LoginParams params) throws Exception {
        // 结果结果集
        Result<LoginResult> result = new Result<>();

        // 查询用户
        LogUtil.sysInfo("密码登录  params: {}", JSONObject.toJSONString(params));
        LoanUserEntity user = loanUserDao.findByLoginNameAndAppName(params.getLoginName(), params.getAppName());
        LogUtil.sysInfo("密码登录  user: {}", JSONObject.toJSONString(user));

        // 用户是否存在
        if (null == user) {
            result.setReturnCode(ResultEnum.PHONE_NO_EXIT.code());
            result.setMessage(ResultEnum.PHONE_NO_EXIT.message());
            return result;
        }

        // 密码匹配
        if (!params.getPassword().equals(user.getPassword())) {
            result.setReturnCode(ResultEnum.PASSWORD_INVALID.code());
            result.setMessage(ResultEnum.PASSWORD_INVALID.message());
            return result;
        }

        // 生成并更新token
        String token = this.tokenManager.updateUserToken(user.getId());

        // TODO 新增或更新afid

        // 更新版本号,方便指定版本控制
        loanUserDao.updateAppVersion(user.getId(), params.getAppVersion());

        // 封装结果集
        LoginResult loginResult = new LoginResult();
        loginResult.setUserId(user.getId());
        loginResult.setToken(token);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(loginResult);
        return result;
    }

    /**
     * 我的个人中心
     *
     * @param params 请求参数封装
     * @return Result<MineResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<MineResult> mine(MineParams params) throws Exception {
        // 结果结果集
        Result<MineResult> result = new Result<>();
        MineResult data = new MineResult();

        // token校验
        String token = params.getToken();
        User userCache = tokenManager.getUserCache(token);
        if (null == userCache) {
            result.setReturnCode(ResultEnum.NO_LOGIN.code());
            result.setMessage(ResultEnum.NO_LOGIN.message());
            return result;
        }

        // 未完成的订单
        Integer uncompletedOrder = platformOrderDao.findUserLessThanSpecificStatusOrderNum(userCache.getId(), 110);
        data.setUncompletedOrder(uncompletedOrder);

        // 待还款订单数量-
        Integer noCompleteNum = platformOrderDao.findUserWaitRepaymentOrderNum(userCache.getId());
        data.setUncompletedOrder(noCompleteNum - uncompletedOrder);

        // 用户所有状态的订单数量
        Integer allOrderNum = platformOrderDao.findUserAllOrderNum(userCache.getId());
        data.setAllRepaymentOrder(allOrderNum);

        // 帮助中心地址 TODO 待确认

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(data);
        return result;
    }

    /**
     * 用户OCR识别信息保存
     *
     * @param params 保存用户OCR识别信息请求参数封装类
     * @return 保存成功与否
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> saveOcrInfo(UserOcrInfoParams params) throws Exception {
        // 结果集
        Result<Object> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_SAVE_OCR_INFO;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());
        requestParam.put("type", params.getType());
        requestParam.put("info", params.getInfo());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
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
     * 获取用户OCR保存信息
     *
     * @param params 请求参数封装
     * @return Result<UserOcrBasicInfoResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<UserOcrBasicInfoResult> getOcrInfo(MineParams params) throws Exception {
        // 结果集
        Result<UserOcrBasicInfoResult> result = new Result<>();


        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        // result.setData(basicInfoResult);
        return result;
    }

    /**
     * 上传文件
     *
     * @param params 请求参数封装
     * @return Result<PersonInfoResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> uploadS3Images(UploadS3Params params) throws Exception {

        // 结果集
        Result<Object> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_UPLOAD_S3_IMAGES;

        // 封装请求参数
        Map<String, String> requestParam = new HashMap(15);
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("realName", params.getRealName());
        requestParam.put("idNo", params.getIdNo());
        requestParam.put("panCode", params.getPanCode());
        requestParam.put("dateOfBirth", params.getDateOfBirth());
        requestParam.put("adBackJson", params.getAdBackJson());
        requestParam.put("gender", params.getGender());
        requestParam.put("pinCode", params.getPinCode());
        requestParam.put("idAddr", params.getIdAddr());
        requestParam.put("userId", params.getUserId());
        requestParam.put("adFrontJson", params.getAdFrontJson());
        requestParam.put("panJson", params.getPanJson());
        if (StringUtils.isNotBlank(params.getProductId())) {
            requestParam.put("productId", params.getProductId());
        }

        // 文件列表
        Map<String, File> fileMap = new HashMap(4);
        fileMap.put("panImg", convertToFile(params.getPanImgData()));
        fileMap.put("livingImg", convertToFile(params.getLivingImgData()));
        fileMap.put("frontImg", convertToFile(params.getFrontImgData()));
        fileMap.put("backImg", convertToFile(params.getBackImgData()));

        // 封装请求头
        Map<String, String> headers = new HashMap<>(1);
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_FORM_FILE(url, requestParam, fileMap);

        // 释放文件
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            File value = entry.getValue();
            value.deleteOnExit();
        }

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, Object.class, responseJson)) {
            return result;
        }

        // 获取结果集
        String data = responseJson.getString("msg");

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(data);
        return result;
    }

    /**
     * 获取证件和人脸相似度
     *
     * @param params 获取人脸相似度信息请求参数封装类
     * @return 相似度是否通过
     * @throws Exception 请求异常
     */
    @Override
    public Result<UserFaceComparisonResult> faceComparison(UserFaceComparisonParams params) throws Exception {
        // 结果集
        Result<UserFaceComparisonResult> result = new Result<>();
        result.setReturnCode(ResultEnum.KYC_FACE_COMPARISON_ERROR.code());
        result.setMessage(ResultEnum.KYC_FACE_COMPARISON_ERROR.message());

        // 获取参数
        String appName = params.getAppName();
        String userId = params.getUser().getId();
        String faceComparisonUrl = getAdvanceConfig(appName, OcrField.ADVANCE_FACE_COMPARISON_URL);
        String threshold = getAdvanceConfig(appName, OcrField.ADVANCE_FACE_COMPARISON_THRESHOLD);

        // 请求头
        Map<String, String> heardMap = getAdvanceHeard(appName);

        // 文件列表
        HashMap<String, File> fileMap = Maps.newHashMap();
        fileMap.put("firstImage", convertToFile(params.getIdImageData()));
        fileMap.put("secondImage", convertToFile(params.getFaceImageData()));

        // 发送请求
        String resultStr = HttpUtils.POST_WITH_HEADER_FORM_FILE(faceComparisonUrl, null, heardMap, fileMap);

        // 释放文件
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            File value = entry.getValue();
            value.deleteOnExit();
        }

        // 解析响应结果
        if (StringUtils.isBlank(resultStr)) {
            return result;
        }
        AdvanceFaceComparisonResult comparisonResult = JSONObject.parseObject(resultStr, AdvanceFaceComparisonResult.class);

        // 日志写入Elastic
        OcrLivingDetectionLogElasticEntity livingDetectionLog = new OcrLivingDetectionLogElasticEntity();
        BeanUtils.copyProperties(comparisonResult, livingDetectionLog);
        livingDetectionLog.setRequestUrl(faceComparisonUrl);
        livingDetectionLog.setRequestHeard(heardMap);
        livingDetectionLog.setResponse(resultStr);
        livingDetectionLog.setUserId(userId);
        livingDetectionLog.setCreateTime(new Date());
        ocrLivingDetectionLogElasticDao.save(livingDetectionLog);

        // 根据code值进行判定
        String code = comparisonResult.getCode();
        if (!OcrField.ADVANCE_SUCCESS_CODE.equalsIgnoreCase(code)) {
            return result;
        }
        AdvanceFaceComparisonResponse resultData = comparisonResult.getData();
        if (ObjectUtils.isEmpty(resultData)) {
            return result;
        }

        // 判断相似分数与阀值大小
        String similarity = resultData.getSimilarity();
        if (StringUtils.isBlank(similarity)) {
            return result;
        }
        BigDecimal similarityDouble = new BigDecimal(similarity);
        BigDecimal thresholdDouble = new BigDecimal(threshold);
        if (similarityDouble.compareTo(thresholdDouble) < 1) {
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(new UserFaceComparisonResult(similarity));
        return result;
    }

    /**
     * advance获取证件信息
     *
     * @param params 获取证件信息请求参数封装类
     * @return 证件信息
     * @throws Exception 请求异常
     */
    @Override
    public Result<UserOcrResult> userOcrInfo(UserOcrFullInfoParams params) throws Exception {
        // 结果集
        Result<UserOcrResult> result = new Result<>();
        result.setReturnCode(ResultEnum.KYC_SCAN_CARD_ERROR.code());
        result.setMessage(ResultEnum.KYC_SCAN_CARD_ERROR.message());

        // 获取请求参数
        String appName = params.getAppName();
        String imageType = params.getImageType();
        String cardInfoUrl = getAdvanceConfig(appName, OcrField.ADVANCE_CARD_INFO_URL);
        String userId = params.getUser().getId();

        // 请求头
        Map<String, String> heardMap = getAdvanceHeard(appName);

        // 封装请求参数
        HashMap<String, String> paramMap = Maps.newHashMap();
        paramMap.put(OcrField.ADVANCE_CARD_TYPE, imageType);

        // 文件列表
        HashMap<String, File> fileMap = Maps.newHashMap();
        fileMap.put("image", convertToFile(params.getImageData()));

        // 发送请求
        String resultStr = HttpUtils.POST_WITH_HEADER_FORM_FILE(cardInfoUrl, paramMap, heardMap, fileMap);
        LogUtil.sysInfo("advance获取证件信息,url {} , result: {}", cardInfoUrl, resultStr);

        // 释放文件
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            File value = entry.getValue();
            value.deleteOnExit();
        }

        // 解析响应结果
        if (StringUtils.isBlank(resultStr)) {
            return result;
        }
        AdvanceOcrInfoResult ocrInfoResult = JSONObject.parseObject(resultStr, AdvanceOcrInfoResult.class);

        // 日志写入Elastic
        OcrLivingDetectionLogElasticEntity livingDetectionLog = new OcrLivingDetectionLogElasticEntity();
        BeanUtils.copyProperties(ocrInfoResult, livingDetectionLog);
        livingDetectionLog.setRequestUrl(cardInfoUrl);
        livingDetectionLog.setRequestParam(paramMap);
        livingDetectionLog.setRequestHeard(heardMap);
        livingDetectionLog.setResponse(resultStr);
        livingDetectionLog.setUserId(userId);
        livingDetectionLog.setCreateTime(new Date());
        ocrLivingDetectionLogElasticDao.save(livingDetectionLog);

        // 根据code值进行判定
        String code = ocrInfoResult.getCode();
        if (!OcrField.ADVANCE_SUCCESS_CODE.equalsIgnoreCase(code)) {
            return result;
        }
        UserOcrResult ocrResult = new UserOcrResult();

        // 封装响应参数
        JSONObject jsonObject = ocrInfoResult.getData();
        if (OcrField.ADVANCE_USER_OCR_ID_FRONT.equals(imageType)) {
            AdvanceOcrInfoResponse<AdvanceOcrFrontInfoResult> data
                    = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<AdvanceOcrInfoResponse<AdvanceOcrFrontInfoResult>>() {
            });
            ocrResult.setType(data.getCardType());
            ocrResult.setInfo(JSONObject.toJSONString(data.getValues()));
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(ocrResult);
        } else if (OcrField.ADVANCE_USER_OCR_ID_BACK.equalsIgnoreCase(imageType)) {
            AdvanceOcrInfoResponse<AdvanceOcrBackInfoResult> data
                    = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<AdvanceOcrInfoResponse<AdvanceOcrBackInfoResult>>() {
            });
            ocrResult.setType(data.getCardType());
            ocrResult.setInfo(JSONObject.toJSONString(data.getValues()));
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(ocrResult);
        }

        return result;
    }

    @Override
    public Result<SaveUserInfoResult> saveUserInfo(UserInfoParams params) {
        // 结果集
        Result<SaveUserInfoResult> result = new Result<>();

        // 查询用户
        String token = params.getToken();
        User user = tokenManager.getUserCache(token);
        if (null == user) {
            result.setReturnCode(ResultEnum.NO_LOGIN.code());
            result.setMessage(ResultEnum.NO_LOGIN.message());
            return result;
        }

        // 查询用户详细信息
        LoanUserInfoEntity userInfo = loanUserInfoDao.getByUserId(user.getId());

        // 注入数据
        if (null != params.getContacts()) {
            userInfo.setContacts(params.getContacts());
        }

        if (null != params.getMonthlyIncome()) {
            userInfo.setMonthlyIncome(params.getMonthlyIncome());
        }

        if (null != params.getPayPeriod()) {
            userInfo.setPayPeriod(params.getPayPeriod());
        }

        if (null != params.getOccupation()) {
            userInfo.setOccupation(params.getOccupation());
        }

        if (null != params.getPayMethod()) {
            userInfo.setPayMethod(params.getPayMethod());
        }

        if (null != params.getEmail()) {
            userInfo.setEmail(params.getEmail());
        }

        if (null != params.getEducation()) {
            userInfo.setEducation(params.getEducation());
        }

        if (null != params.getMarital()) {
            userInfo.setMarital(params.getMarital());
        }

        if (null != params.getChildrenNumber()) {
            userInfo.setChildrenNumber(params.getChildrenNumber());
        }

        if (null != params.getLoanPurpose()) {
            userInfo.setLoanPurpose(params.getLoanPurpose());
        }

        if (null != params.getLiveType()) {
            userInfo.setLiveType(params.getLiveType());
        }

        if (null != params.getPapersAddress()) {
            userInfo.setPapersAddress(params.getPapersAddress());
        }

        if (null != params.getPapersFatherName()) {
            userInfo.setPapersFatherName(params.getPapersFatherName());
        }

        if (null != params.getPapersFullName()) {
            userInfo.setPapersFullName(params.getPapersFullName());
        }

        if (null != params.getPapersMotherName()) {
            userInfo.setPapersMotherName(params.getPapersMotherName());
        }

        if (null != params.getPapersId()) {
            userInfo.setPapersId(params.getPapersId());
        }

        if (null != params.getPapersName()) {
            userInfo.setPapersName(params.getPapersName());
        }

        if (null != params.getPapersVoterId()) {
            userInfo.setPapersVoterId(params.getPapersVoterId());
        }

        // 更新
        loanUserInfoDao.update(userInfo);


        // TODO 更新用户缓存
        tokenManager.updateUserCache(null);

        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 获取用户信息
     * @param params
     * @return
     */
    @Override
    public Result<User> getUserInfo(BaseParams params){
        Result<User> result= new Result<>();
        result.setData(params.getUser());
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }


    private File convertToFile(byte[] byteFile) {
        String objectId = ObjectIdUtil.getObjectId();
        String newFilePath = "/tmp/" + objectId;

        File file = new File(newFilePath);
        try {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                return null;
            }
            OutputStream os = new FileOutputStream(file);
            // 输出流
            os.write(byteFile);
            os.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
        headers.put(HTTP.CONTENT_TYPE, OcrField.ADVANCE_MULTIPART_VALUE);
        return headers;
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
