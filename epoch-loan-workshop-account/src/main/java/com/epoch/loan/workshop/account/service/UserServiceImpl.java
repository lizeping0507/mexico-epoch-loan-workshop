package com.epoch.loan.workshop.account.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.OcrChannelConfigStatus;
import com.epoch.loan.workshop.common.constant.OcrField;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.service.UserService;
import com.epoch.loan.workshop.common.util.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_FORGOTPWD;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("phoneNumber", params.getPhoneNumber());
        requestParam.put("passwd", params.getPasswd());
        requestParam.put("smsCode", params.getSmsCode());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        // 请求
        String responseStr = HttpUtils.POST(url, requestParam.toJSONString());

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ChangePasswordResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();
        changePasswordResult.setToken(data.getString("token"));
        changePasswordResult.setUserId(data.getString("userId"));

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(changePasswordResult);
        return result;
    }

    /**
     * 修改密码
     *
     * @param params 请求参数封装
     * @return Result<EditPasswordResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<EditPasswordResult> editPassword(MineParams params) throws Exception {
        // 结果结果集
        Result<EditPasswordResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_EDITPASSWWORD + params.getUserId();

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("userId", params.getUserId());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        // 请求
        String responseStr = HttpUtils.POST(url, requestParam.toJSONString());

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, EditPasswordResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        EditPasswordResult editPasswordResult = new EditPasswordResult();
        editPasswordResult.setUserId(data.getString("userId"));
        editPasswordResult.setPhoneNumber(data.getString("phoneNumber"));
        editPasswordResult.setSavePhoneNumber(data.getString("savePhoneNumber"));

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(editPasswordResult);
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

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_MODIFYPASSWWORD;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("phoneNumber", params.getPhoneNumber());
        requestParam.put("userId", params.getUserId());
        requestParam.put("oldPassword", params.getOldPassword());
        requestParam.put("newPassword", params.getNewPassword());
        requestParam.put("enterPassword", params.getEnterPassword());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("appFlag", params.getAppName());

        // 封装请求头
        Map<String, String> headers = new HashMap<>(1);
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, ChangePasswordResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();
        changePasswordResult.setToken(data.getString("token"));
        changePasswordResult.setUserId(data.getString("userId"));

        // 封装结果
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

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_MINE + params.getUserId();

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("userId", params.getUserId());
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        // 请求
        String responseStr = HttpUtils.POST(url, requestParam.toJSONString());

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, MineResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        MineResult mineResult = new MineResult();
        if (!CheckFieldUtils.checkObjAllFieldsIsNull(data)) {
            mineResult.setPhoneNumber(data.getString("phoneNumber"));
            mineResult.setUncompletedOrder(data.getInteger("uncompletedOrder"));
            mineResult.setPenRepaymentOrder(data.getInteger("penRepaymentOrder"));
            mineResult.setAllRepaymentOrder(data.getInteger("allRepaymentOrder"));
            mineResult.setHelpUrl(data.getString("helpUrl"));
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(mineResult);
        return result;
    }

    /**
     * 判断并保存用户OCR识别记录
     *
     * @param params OCR识别结果信息
     * @return 识别是否通过
     * @throws Exception 请求异常
     */
    @Override
    public Result<Object> userFaceMatch(UserFaceMatchParams params) throws Exception {
        // 结果集
        Result<Object> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_OCR_USER_FACE_MATCH;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());
        requestParam.put("aadharFaceMatch", params.getAadharFaceMatch());
        requestParam.put("panFaceMatch", params.getPanFaceMatch());
        requestParam.put("ocrChannelType", params.getOcrChannelType());

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

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_GET_OCR_INFO;

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
        if (!PlatformUtil.checkResponseCode(result, UserOcrBasicInfoResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        UserOcrBasicInfoResult basicInfoResult = JSONObject.parseObject(data.toJSONString(), UserOcrBasicInfoResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(basicInfoResult);
        return result;
    }

    /**
     * 新增基本信息
     *
     * @param params 请求参数封装
     * @return Result<UserInfoSaveResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<UserInfoSaveResult> addUserInfo(UserInfoParams params) throws Exception {

        // 结果集
        Result<UserInfoSaveResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_USER_INFO_ADD;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("firstName", params.getFirstName());
        requestParam.put("middleName", params.getMiddleName());
        requestParam.put("lastName", params.getLastName());
        requestParam.put("email", params.getEmail());
        requestParam.put("occupation", params.getOccupation());
        requestParam.put("salary", params.getSalary());
        requestParam.put("marital", params.getMarital());
        requestParam.put("education", params.getEducation());
        requestParam.put("loanPurpose", params.getLoanPurpose());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, UserInfoSaveResult.class, responseJson)) {
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(responseJson.getString("msg"));
        return result;
    }

    /**
     * 获取基本信息
     *
     * @param params 请求参数封装
     * @return Result<UserInfoResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<UserInfoResult> getUserInfo(UserInfoParams params) throws Exception {

        // 结果集
        Result<UserInfoResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_USER_INFO_GET;

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
        if (!PlatformUtil.checkResponseCode(result, UserInfoResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        UserInfoResult res = JSONObject.parseObject(data.toJSONString(), UserInfoResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 新增/更新个人信息
     *
     * @param params 请求参数封装
     * @return Result<PersonInfoUpdateResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<PersonInfoUpdateResult> savePersonInfo(PersonInfoParams params) throws Exception {

        // 结果集
        Result<PersonInfoUpdateResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PERSON_INFO_SAVE;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("houseType", params.getHouseType());
        requestParam.put("childNum", params.getChildNum());
        requestParam.put("designation", params.getDesignation());
        requestParam.put("incomeWay", params.getIncomeWay());
        requestParam.put("familyRelationship", params.getFamilyRelationship());
        requestParam.put("familyName", params.getFamilyName());
        requestParam.put("familyPhone", params.getFamilyPhone());
        requestParam.put("friendName", params.getFriendName());
        requestParam.put("friendPhone", params.getFriendPhone());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, PersonInfoUpdateResult.class, responseJson)) {
            return result;
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(responseJson.getString("msg"));
        return result;
    }

    /**
     * 获取个人信息
     *
     * @param params 请求参数封装
     * @return Result<PersonInfoResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<PersonInfoResult> getPersonInfo(PersonInfoParams params) throws Exception {

        // 结果集
        Result<PersonInfoResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PERSON_INFO;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());

        requestParam.put("userId", params.getUserId());
        requestParam.put("productId", params.getProductId());

        // 封装请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", params.getToken());

        // 请求
        String responseStr = HttpUtils.POST_WITH_HEADER(url, requestParam.toJSONString(), headers);

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, PersonInfoResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        PersonInfoResult res = JSONObject.parseObject(data.toJSONString(), PersonInfoResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
        return result;
    }

    /**
     * 获取个人信息
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
     * @return 人脸相似度信息
     * @throws Exception 请求异常
     */
    @Override
    public Result<UserFaceComparisonResult> faceComparison(UserFaceComparisonParams params) throws Exception {

        // 结果集
        Result<UserFaceComparisonResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_OCR_ADVANCE_FACE_COMPARISON;

        // 封装请求参数
        Map<String, String> requestParam = new HashMap(4);
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());

        // 文件列表
        Map<String, File> fileMap = new HashMap(2);
        fileMap.put("idImage", convertToFile(params.getIdImageData()));
        fileMap.put("faceImage", convertToFile(params.getFaceImageData()));

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
        if (!PlatformUtil.checkResponseCode(result, UserFaceComparisonResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        UserFaceComparisonResult res = JSONObject.parseObject(data.toJSONString(), UserFaceComparisonResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
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
        String appName = params.getAppName();
        String cardInfoUrl = getAdvanceConfig(appName, OcrField.ADVANCE_CARD_INFO_URL);

        // 请求头
        Map<String, String> heardMap = getAdvanceHeard(appName);

        // 请求参数
        HashMap<String, String> paramMap = Maps.newHashMap();
        paramMap.put(OcrField.ADVANCE_CARD_TYPE, params.getImageType());

        // 文件列表
        Map<String, File> fileMap = new HashMap(1);
        fileMap.put("image", convertToFile(params.getImageData()));

        // 发送请求
        String resultStr = HttpUtils.POST_WITH_HEADER_FORM_FILE(cardInfoUrl, paramMap, heardMap, fileMap);
        LogUtil.sysInfo("advance获取证件信息,url {} , result: {}", cardInfoUrl,resultStr );

        // 释放文件
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            File value = entry.getValue();
            value.deleteOnExit();
        }

        // 解析响应结果

        return result;
    }

    private File convertToFile(byte[] byteFile) {
        String objectId = ObjectIdUtil.getObjectId();
        String newFilePath = "/tmp/" + objectId;

        File file = new File(newFilePath);
        try {
            file.createNewFile();
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
