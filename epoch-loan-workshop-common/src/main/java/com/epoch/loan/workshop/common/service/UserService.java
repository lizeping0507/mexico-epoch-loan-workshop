package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.account.service;
 * @className : UserServiceImpl
 * @createTime : 2022/3/21 11:53
 * @description : 用户相关业务接口
 */
public interface UserService {
    /**
     * 判断手机号是否已经注册
     *
     * @param params 请求参数封装
     * @return Result<IsRegisterResult>
     * @throws Exception 请求异常
     */
    Result<IsRegisterResult> isRegister(IsRegisterParams params) throws Exception;

    /**
     * 用户注册
     *
     * @param params 请求参数封装
     * @return Result<RegisterResult>
     * @throws Exception 请求异常
     */
    Result<RegisterResult> register(RegisterParams params) throws Exception;

    /**
     * 忘记密码
     *
     * @param params 请求参数封装
     * @return Result<ChangePasswordResult>
     * @throws Exception 请求异常
     */
    Result<ChangePasswordResult> forgotPwd(ForgotPwdParams params) throws Exception;

    /**
     * 更新密码
     *
     * @param params 请求参数封装
     * @return Result<ChangePasswordResult>
     * @throws Exception 请求异常
     */
    Result<ChangePasswordResult> modifyPassword(ModifyPasswordParams params) throws Exception;

    /**
     * 密码登录
     *
     * @param params 请求参数封装
     * @return Result<LoginResult>
     * @throws Exception 请求异常
     */
    Result<LoginResult> login(LoginParams params) throws Exception;

    /**
     * 我的个人中心
     *
     * @param params 请求参数封装
     * @return Result<MineResult>
     * @throws Exception 请求异常
     */
    Result<MineResult> mine(MineParams params) throws Exception;

    /**
     * 获取用户OCR保存信息
     *
     * @param params 请求参数封装
     * @return Result<UserOcrBasicInfoResult>
     * @throws Exception 请求异常
     */
    Result<UserOcrBasicInfoResult> getOcrInfo(BaseParams params) throws Exception;

    /**
     * ocr识别信息保存接口
     *
     * @param params 请求参数封装
     * @return Result<UploadS3Result>
     * @throws Exception 请求异常
     */
    Result<Object> saveFile(SaveFileParams params) throws Exception;

    /**
     * 获取证件和人脸相似度
     *
     * @param params 获取人脸相似度信息请求参数封装类
     * @return 人脸相似度信息
     * @throws Exception 请求异常
     */
    Result<UserFaceComparisonResult> faceComparison(UserFaceComparisonParams params) throws Exception;

    /**
     * advance获取证件信息
     *
     * @param params 获取证件信息请求参数封装类
     * @return 证件信息
     * @throws Exception 请求异常
     */
    Result<UserOcrResult> userOcrInfo(UserOcrFullInfoParams params) throws Exception;

    /**
     * 保存用户补充信息信息
     *
     * @param params
     * @return
     */
    Result<SaveUserInfoResult> saveUserAddInfo(UserAddInfoParams params);

    /**
     * 获取用户信息
     *
     * @param params
     * @return
     */
    Result<User> getUserInfo(BaseParams params);

    /**
     * 保存用户个人信息
     * @param params
     * @return
     */
    Result<SaveUserInfoResult> saveUserBasicInfo(UserBasicInfoParams params);

    /**
     * 版本检查
     * @param params
     * @return
     */
    Result<VersionResult> checkVersion(BaseParams params);
}
