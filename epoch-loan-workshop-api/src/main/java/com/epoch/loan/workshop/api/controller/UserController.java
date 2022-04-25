package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.config.URL;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.params.params.result.*;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller;
 * @className : ForwardController
 * @createTime : 2022/3/21 10:59
 * @description : 转发到贷超服务的接口
 */
@RestController
@RequestMapping(URL.USER)
public class UserController extends BaseController {

    /**
     * 判断手机号是否已经注册过
     *
     * @param isRegisterParams
     * @return 是否存在
     */
    @PostMapping(URL.IS_REGISTER)
    public Result<IsRegisterResult> isRegister(IsRegisterParams isRegisterParams) {
        // 结果集
        Result<IsRegisterResult> result = new Result<>();

        try {
            // 判断手机号是否已经注册过
            return userService.isRegister(isRegisterParams);
        } catch (Exception e) {
            LogUtil.sysError("[UserController isRegister]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 用户注册
     *
     * @param registerParams
     * @return 结果
     */
    @PostMapping(URL.REGISTER)
    public Result<RegisterResult> register(RegisterParams registerParams) {
        LogUtil.sysInfo("用户注册 : {}", JSONObject.toJSONString(registerParams));
        // 结果集
        Result<RegisterResult> result = new Result<>();

        try {
            // 验证请求参数是否合法
            if (!registerParams.isMobileLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":mobile");
                return result;
            }

            if (!registerParams.isPasswordLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":password");
                return result;

            }

            if (!registerParams.isSmsCodeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":smsCode");
                return result;
            }

            if (!registerParams.isAndroidIdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":androidId");
                return result;
            }

            if (!registerParams.isPlatformLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":platform");
                return result;
            }

            if (!registerParams.isGaIdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":gaId");
                return result;
            }

            if (!registerParams.isImeiLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":imei");
                return result;
            }

            // 用户注册
            return userService.register(registerParams);
        } catch (Exception e) {
            LogUtil.sysError("[UserController register]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 密码登录
     *
     * @param loginParams 入参
     * @return 登陆结果
     */
    @PostMapping(URL.LOGIN)
    public Result<LoginResult> login(LoginParams loginParams) {
        LogUtil.sysInfo("密码登录 : {}", JSONObject.toJSONString(loginParams));
        // 结果集
        Result<LoginResult> result = new Result<>();

        try {
            // 参数校验
            if (!loginParams.isLoginNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":loginName");
                return result;
            }
            if (!loginParams.isPasswordNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":password");
                return result;
            }

            // 密码登录
            return userService.login(loginParams);
        } catch (Exception e) {
            LogUtil.sysError("[UserController login]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 更新密码
     *
     * @param modifyPasswordParams 更新密码参数
     * @return 新token和用户id
     */
    @PostMapping(URL.MODIFY_PASSWORD)
    @Authentication(auth = true)
    public Result<ChangePasswordResult> modifyPassword(ModifyPasswordParams modifyPasswordParams) {
        LogUtil.sysInfo("更新密码 : {}", JSONObject.toJSONString(modifyPasswordParams));
        // 结果集
        Result<ChangePasswordResult> result = new Result<>();

        try {
            // 验证请求参数是否合法
            if (!modifyPasswordParams.isPhoneNumberLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":PhoneNumber");
                return result;
            }
            if (!modifyPasswordParams.isOldPasswordLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":OldPassword");
                return result;
            }
            if (!modifyPasswordParams.isNewPasswordLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":NewPassword");
                return result;
            }
            if (!modifyPasswordParams.isEnterPasswordLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":EnterPassword");
                return result;
            }
            // 更新密码
            return userService.modifyPassword(modifyPasswordParams);
        } catch (Exception e) {
            LogUtil.sysError("[UserController modifyPassword]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 忘记密码
     *
     * @param forgotPwdParams 忘记密码参数
     * @return 结果
     */
    @PostMapping(URL.FORGOT_PWD)
    public Result<ChangePasswordResult> forgotPwd(ForgotPwdParams forgotPwdParams) {
        // 结果集
        Result<ChangePasswordResult> result = new Result<>();

        try {
            // 参数校验
            if (!forgotPwdParams.isPasswdLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":Passwd");
                return result;
            }
            // 参数校验
            if (!forgotPwdParams.isPhoneNumberLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":PhoneNumber");
                return result;
            }
            // 参数校验
            if (!forgotPwdParams.isSmsCodeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":SmsCode");
                return result;
            }

            // 忘记密码
            return userService.forgotPwd(forgotPwdParams);
        } catch (Exception e) {
            LogUtil.sysError("[UserController forgotPwd]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 我的个人中心
     *
     * @param mineParams 入参
     * @return 个人中心响应参数
     */
    @Authentication(auth = true)
    @PostMapping(URL.MINE)
    public Result<MineResult> mine(MineParams mineParams) {
        // 结果集
        Result<MineResult> result = new Result<>();

        try {

            // 我的个人中心
            return userService.mine(mineParams);
        } catch (Exception e) {
            LogUtil.sysError("[UserController mine]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 保存用户基本信息
     *
     * @param params 入参
     * @return 个人信息
     */
    @Authentication(auth = true)
    @PostMapping(URL.SAVE_BASIC_INFO)
    public Result<SaveUserInfoResult> saveUserBasicInfo(UserBasicInfoParams params) {
        // 结果集
        Result<SaveUserInfoResult> result = new Result<>();

        try {
            if (!params.isMonthlyIncomeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":MonthlyIncome");
                return result;
            }
            if (!params.isPayPeriodLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":PayPeriod");
                return result;
            }
            if (!params.isOccupationLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":Occupation");
                return result;
            }
            if (!params.isPayMethodLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":PayMethod");
                return result;
            }
            if (!params.isEmailLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":Email");
                return result;
            }
            if (!params.isEducationLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":Education");
                return result;
            }
            if (!params.isMaritalLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":Marital");
                return result;
            }
            if (!params.isLoanPurposeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":LoanPurpose");
                return result;
            }
            if (!params.isCustomFatherNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":CustomFatherName");
                return result;
            }
            if (!params.isCustomFullNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":CustomFullName");
                return result;
            }
            if (!params.isCustomMotherNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":CustomMotherName");
                return result;
            }
            if (!params.isCustomNameLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":CustomName");
                return result;
            }
            // 获取个人信息
            return userService.saveUserBasicInfo(params);
        } catch (Exception e) {
            LogUtil.sysError("[UserController saveUserBasicInfo]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 保存用户个人信息
     *
     * @param params 入参
     * @return 个人信息
     */
    @Authentication(auth = true)
    @PostMapping(URL.SAVE_PERSON_INFO)
    public Result<SaveUserInfoResult> saveUserPersonInfo(UserPersonInfoParams params) {
        // 结果集
        Result<SaveUserInfoResult> result = new Result<>();

        try {

            if (!params.isContactsLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":Contacts");
                return result;
            }

            if (!params.isChildrenNumberLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":ChildrenNumber");
                return result;
            }

            if (!params.isLiveTypeLegal()) {
                // 异常返回结果
                result.setReturnCode(ResultEnum.PARAM_ERROR.code());
                result.setMessage(ResultEnum.PARAM_ERROR.message() + ":LiveType");
                return result;
            }
            // 保存个人信息
            return userService.saveUserPersonInfo(params);
        } catch (Exception e) {
            LogUtil.sysError("[UserController saveUserPersonInfo]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }

    /**
     * 获取用户信息
     *
     * @param params 入参
     * @return 个人信息
     */
    @Authentication(auth = true)
    @PostMapping(URL.GET_INFO)
    public Result<User> getUserInfo(BaseParams params) {
        // 结果集
        Result<User> result = new Result<>();

        try {
            // 获取个人信息
            return userService.getUserInfo(params);
        } catch (Exception e) {
            LogUtil.sysError("[UserController getUserInfo]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
