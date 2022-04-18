package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.PlatformUrl;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.service.UserService;
import com.epoch.loan.workshop.common.util.CheckFieldUtils;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import com.epoch.loan.workshop.common.util.PlatformUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    /**
     * 判断手机号是否已经注册
     *
     * @param isRegisterParams 请求参数封装
     * @return Result<IsRegisterResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<IsRegisterResult> isRegister(IsRegisterParams isRegisterParams) throws Exception {
        // 结果结果集
        Result<IsRegisterResult> result = new Result<>();

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_ISREGISTER + isRegisterParams.getPhoneNumber();

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appFlag", isRegisterParams.getAppName());
        requestParam.put("phoneNumber", isRegisterParams.getPhoneNumber());
        requestParam.put("versionNumber", isRegisterParams.getAppVersion());
        requestParam.put("mobileType", isRegisterParams.getMobileType());

        // 请求
        String responseStr = HttpUtils.POST(url, requestParam.toJSONString());

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, IsRegisterResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        IsRegisterResult isRegisterResult = new IsRegisterResult();
        isRegisterResult.setIsExists(data.getString("isExists"));

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(isRegisterResult);
        return result;
    }

    /**
     * 用户注册
     *
     * @param registerParams 请求参数封装
     * @return Result<RegisterResult>
     * @throws Exception 请求异常
     */
    @Override
    public Result<RegisterResult> register(RegisterParams registerParams) throws Exception {
        // 结果结果集
        Result<RegisterResult> result = new Result<>();




        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
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

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_PWREGISTER;

        // 封装请求参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("loginName", params.getLoginName());
        requestParam.put("passwd", params.getPassword());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("appFlag", params.getAppName());

        // 请求
        String responseStr = HttpUtils.POST(url, requestParam.toJSONString());

        // 解析响应结果
        JSONObject responseJson = JSONObject.parseObject(responseStr);

        // 判断接口响应是否正常
        if (!PlatformUtil.checkResponseCode(result, LoginResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        LoginResult loginResult = new LoginResult();
        loginResult.setUserId(data.getString("userId"));
        loginResult.setToken(data.getString("token"));
        loginResult.setAppId(data.getString("appId"));
        loginResult.setNeedCatchData(data.getBoolean("needCatchData"));
        loginResult.setDataNo(data.getString("dataNo"));

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

        // 拼接请求路径
        String url = platformConfig.getPlatformDomain() + PlatformUrl.PLATFORM_OCR_ADVANCE_CARD_INFO;

        // 封装请求参数
        Map<String, String> requestParam = new HashMap(5);
        requestParam.put("appFlag", params.getAppName());
        requestParam.put("versionNumber", params.getAppVersion());
        requestParam.put("mobileType", params.getMobileType());
        requestParam.put("userId", params.getUserId());
        requestParam.put("imageType", params.getImageType());

        // 文件列表
        Map<String, File> fileMap = new HashMap(1);
        fileMap.put("image", convertToFile(params.getImageData()));

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
        if (!PlatformUtil.checkResponseCode(result, UserOcrResult.class, responseJson)) {
            return result;
        }

        // 获取结果集
        JSONObject data = responseJson.getJSONObject("data");

        // 封装结果就
        UserOcrResult res = JSONObject.parseObject(data.toJSONString(), UserOcrResult.class);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(res);
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
}
