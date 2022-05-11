package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.elastic.OcrLivingDetectionLogElasticEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanAppControlEntity;
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
     * 存储用户 照片桶名
     */
    @Value("${oss.bucketName.user}")
    private String userFileBucketName;

    /**
     * 判断手机号是否已经注册
     *
     * @param params 请求参数封装
     * @return Result<IsRegisterResult>
     */
    @Override
    public Result<IsRegisterResult> isRegister(IsRegisterParams params) {
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
     */
    @Override
    public Result<RegisterResult> register(RegisterParams params) {
        // 结果结果集
        Result<RegisterResult> result = new Result<>();

        // 设备是否已经注册
        Integer isExitByAndroidId = loanUserDao.exitByAppNameAndAndroidId(params.getAppName(), params.getAndroidId());
        LogUtil.sysInfo("用户注册 : isExitByAndroidId {}", JSONObject.toJSONString(isExitByAndroidId));
        if (isExitByAndroidId != 0) {
            result.setReturnCode(ResultEnum.DEVICE_REGISTERED.code());
            result.setMessage(ResultEnum.DEVICE_REGISTERED.message());
            return result;
        }

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
            registerCode = (String) redisClient.get(String.format(RedisKeyField.SMS_CODE_TEMPLATE, params.getAppName(), params.getMobile()));
            // TODO 测试用
            registerCode = "0000";
        }
        if (StringUtils.isEmpty(registerCode) || !registerCode.equals(params.getSmsCode())) {
            result.setReturnCode(ResultEnum.SMS_CODE_ERROR.code());
            result.setMessage(ResultEnum.SMS_CODE_ERROR.message());
            return result;
        }

        // 生成user记录
        LoanUserEntity user = new LoanUserEntity();
        user.setId(ObjectIdUtil.getObjectId());
        user.setAndroidId(params.getAndroidId());
        user.setChannelId(0);
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
        userInfo.setGps(params.getGps());
        userInfo.setGpsAddress(params.getGpsAddress());
        userInfo.setRegisterGps(params.getGps());
        userInfo.setRegisterAddress(params.getGpsAddress());
        userInfo.setMobile(params.getMobile());
        userInfo.setUpdateTime(new Date());
        userInfo.setCreateTime(new Date());
        loanUserInfoDao.insert(userInfo);

        // 生成token
        String token = this.tokenManager.updateUserToken(user.getId());

        // 更新缓存
        tokenManager.updateUserCache(user.getId());

        // 封装结果
        RegisterResult registerResult = new RegisterResult();
        registerResult.setToken(token);
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(registerResult);
        return result;
    }

    /**
     * 密码登录
     *
     * @param params 请求参数封装
     * @return Result<LoginResult>
     */
    @Override
    public Result<LoginResult> login(LoginParams params) {
        // 结果结果集
        Result<LoginResult> result = new Result<>();

        // 查询用户
        LoanUserEntity user = loanUserDao.findByLoginNameAndAppName(params.getLoginName(), params.getAppName());

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

        // 更新缓存
        tokenManager.updateUserCache(user.getId());

        // TODO 新增或更新afid

        // 更新版本号,方便指定版本控制
        loanUserDao.updateAppVersion(user.getId(), params.getAppVersion());

        // 封装结果集
        LoginResult loginResult = new LoginResult();
        loginResult.setToken(token);

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(loginResult);
        return result;
    }

    /**
     * 忘记密码
     *
     * @param params 请求参数封装
     * @return Result<ChangePasswordResult>
     */
    @Override
    public Result<ChangePasswordResult> forgotPwd(ForgotPwdParams params) {
        // 结果结果集
        Result<ChangePasswordResult> result = new Result<>();

        // 通过Nacos命名空间判断环境 非生产环境验证码默认使用 0000
        String registerCode;
        if (namespace.contains("dev") || namespace.contains("test")) {
            registerCode = "0000";
        } else {
            registerCode = (String) redisClient.get(String.format(RedisKeyField.SMS_CODE_TEMPLATE, params.getAppName(), params.getPhoneNumber()));
            // TODO 测试用
            registerCode = "0000";
        }
        if (StringUtils.isEmpty(registerCode) || !registerCode.equals(params.getSmsCode())) {
            result.setReturnCode(ResultEnum.SMS_CODE_ERROR.code());
            result.setMessage(ResultEnum.SMS_CODE_ERROR.message());
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

        // 更新缓存
        tokenManager.updateUserCache(user.getId());

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
     */
    @Override
    public Result<ChangePasswordResult> modifyPassword(ModifyPasswordParams params) {
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

        // 更新缓存
        tokenManager.updateUserCache(params.getUser().getId());

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
     * 我的个人中心
     *
     * @param params 请求参数封装
     * @return Result<MineResult>
     */
    @Override
    public Result<MineResult> mine(MineParams params) {
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

        String loginName = userCache.getLoginName();
        data.setPhoneNumber(loginName.substring(0, 3) + "****" + loginName.substring(7));

        // 未完成的订单
        Integer uncompletedOrder = loanOrderDao.findUserBetweenSpecificStatusOrderNum(userCache.getId(), OrderStatus.WAIT_PAY, OrderStatus.DUE);
        data.setUncompletedOrder(uncompletedOrder);

        // 待还款订单数量-
        Integer noCompleteNum = loanOrderDao.findUserBetweenSpecificStatusOrderNum(userCache.getId(), OrderStatus.WAY, OrderStatus.DUE);
        data.setPenRepaymentOrder(noCompleteNum - uncompletedOrder);

        // 用户所有状态的订单数量
        Integer allOrderNum = loanOrderDao.findUserBetweenSpecificStatusOrderNum(userCache.getId(), null, null);
        data.setAllRepaymentOrder(allOrderNum);

        // 帮助中心地址 TODO 待确认

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(data);
        return result;
    }

    /**
     * 保存用户补充信息
     *
     * @param params
     * @return
     */
    @Override
    public Result<SaveUserInfoResult> saveUserAddInfo(UserAddInfoParams params) {
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
        LoanUserInfoEntity userInfo = loanUserInfoDao.findUserInfoById(user.getId());
        userInfo.setCustomDateOfBirth(params.getCustomDateOfBirth());
        userInfo.setCustomGenter(params.getCustomGenter());
        userInfo.setChildrenNumber(params.getChildrenNumber());
        userInfo.setLiveType(params.getLiveType());
        userInfo.setEducation(params.getEducation());
        userInfo.setMarital(params.getMarital());
        userInfo.setLoanPurpose(params.getLoanPurpose());

        // 更新
        loanUserInfoDao.update(userInfo);

        // 更新用户缓存
        tokenManager.updateUserCache(user.getId());

        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 保存用户基本信息
     *
     * @param params
     * @return
     */
    @Override
    public Result<SaveUserInfoResult> saveUserBasicInfo(UserBasicInfoParams params) {
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
        LoanUserInfoEntity userInfo = loanUserInfoDao.findUserInfoById(user.getId());
        userInfo.setMonthlyIncome(params.getMonthlyIncome());
        userInfo.setPayPeriod(params.getPayPeriod());
        userInfo.setOccupation(params.getOccupation());
        userInfo.setPayMethod(params.getPayMethod());
        userInfo.setContacts(params.getContacts());

        // 更新
        loanUserInfoDao.update(userInfo);

        // TODO 更新用户缓存
        tokenManager.updateUserCache(user.getId());

        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 获取用户信息
     *
     * @param params
     * @return
     */
    @Override
    public Result<User> getUserInfo(BaseParams params) {
        Result<User> result = new Result<>();
        result.setData(params.getUser());
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        return result;
    }

    /**
     * 获取用户OCR保存信息
     *
     * @param params 请求参数封装
     * @return Result<UserOcrBasicInfoResult>
     */
    @Override
    public Result<UserOcrBasicInfoResult> getOcrInfo(BaseParams params) {
        // 结果集
        Result<UserOcrBasicInfoResult> result = new Result<>();
        UserOcrBasicInfoResult basicInfo = new UserOcrBasicInfoResult();

        String userId = params.getUser().getId();
        LoanUserInfoEntity info = loanUserInfoDao.findUserInfoById(userId);

        if (ObjectUtils.isEmpty(info)) {
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            return result;
        }

        basicInfo.setRealName(info.getPapersName());
        basicInfo.setFatherName(info.getPapersFatherName());
        basicInfo.setMotherName(info.getPapersMotherName());
        basicInfo.setDateOfBirth(info.getPapersDateOfBirth());
        basicInfo.setGender(info.getPapersGender());
        basicInfo.setAge(info.getPapersAge());
        basicInfo.setIdAddr(info.getPapersAddress());
        basicInfo.setIdNumber(info.getPapersId());
        basicInfo.setRfc(info.getRfc());
        basicInfo.setPostalCode(info.getPostalCode());
        if (StringUtils.isNotBlank(info.getFrontPath())) {
            String fileUrl = alibabaOssClient.getFileUrl(userFileBucketName, info.getFrontPath(), null);
            basicInfo.setFrontImgUrl(fileUrl);
        }
        if (StringUtils.isNotBlank(info.getBackPath())) {
            String fileUrl = alibabaOssClient.getFileUrl(userFileBucketName, info.getBackPath(), null);
            basicInfo.setBackImgUrl(fileUrl);
        }
        if (StringUtils.isNotBlank(info.getFacePath())) {
            String fileUrl = alibabaOssClient.getFileUrl(userFileBucketName, info.getFacePath(), null);
            basicInfo.setFaceImgUrl(fileUrl);
        }

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
        result.setData(basicInfo);
        return result;
    }

    /**
     * 上传文件
     *
     * @param params 请求参数封装
     * @return Result<PersonInfoResult>
     */
    @Override
    public Result<Object> saveFile(SaveFileParams params) {
        // 结果集
        Result<Object> result = new Result<>();

        // 获取请求参数
        String rfc = params.getRfc();
        String appName = params.getAppName();
        User user = params.getUser();
        LoanUserInfoEntity userInfoById = loanUserInfoDao.findUserInfoById(user.getId());
        AdvanceOcrFrontInfoResult frontInfo = JSON.parseObject(params.getFrontJson(), AdvanceOcrFrontInfoResult.class);
        String idNumber = frontInfo.getIdNumber();

        // curp校验
        String textIdNumber = "(?=.*[A-Z])(?=.*\\d)[A-Z\\d]{18}";
        String textRfc = "(?=.*[A-Z])(?=.*\\d)[A-Z\\d]{13}";
        if (StringUtils.isBlank(rfc) || StringUtils.isBlank(idNumber)
                || !rfc.matches(textRfc) || !idNumber.matches(textIdNumber)) {
            result.setReturnCode(ResultEnum.RFC_INF_ERROR.code());
            result.setMessage(ResultEnum.RFC_INF_ERROR.message());
            return result;
        }

        // INE/IFE证件id 单包唯一性验证
        LoanUserInfoEntity userInfoEntity = loanUserInfoDao.findUserInfoByPapersIdAndAppName(idNumber, appName);
        if (ObjectUtils.isNotEmpty(userInfoEntity)) {
            // 该 INE/IFE证件id 已经认证过了
            if (userInfoEntity.getUserId().equals(user.getId())) {
                result.setReturnCode(ResultEnum.INF_CERTIFIED_ERROR.code());
                result.setMessage(ResultEnum.INF_CERTIFIED_ERROR.message());
                return result;
            } else {
                result.setReturnCode(ResultEnum.INF_CERTIFIED_OTHER_ERROR.code());
                result.setMessage(ResultEnum.INF_CERTIFIED_OTHER_ERROR.message());
                return result;
            }
        }

        // rfc 单包唯一性验证
        LoanUserInfoEntity userInfoRfc = loanUserInfoDao.findUserInfoByRfcAndAppName(rfc, appName);
        if (ObjectUtils.isNotEmpty(userInfoRfc)) {
            // 该 INE/IFE证件id 已经认证过了
            if (userInfoRfc.getUserId().equals(user.getId())) {
                result.setReturnCode(ResultEnum.RFC_CERTIFIED_ERROR.code());
                result.setMessage(ResultEnum.RFC_CERTIFIED_ERROR.message());
                return result;
            } else {
                result.setReturnCode(ResultEnum.RFC_CERTIFIED_OTHER_ERROR.code());
                result.setMessage(ResultEnum.RFC_CERTIFIED_OTHER_ERROR.message());
                return result;
            }
        }

        // 根据INE/IFE证件id 全包校验 rfc 与 手机号
        LoanUserInfoEntity lastByPapersId = loanUserInfoDao.findLastUserInfoByPapersId(idNumber);
        if (ObjectUtils.isNotEmpty(lastByPapersId)) {
            if (!rfc.equals(lastByPapersId.getRfc()) || !user.getLoginName().equals(lastByPapersId.getMobile())) {
                result.setReturnCode(ResultEnum.RFC_MOBILE_CERTIFIED_ERROR.code());
                result.setMessage(ResultEnum.RFC_MOBILE_CERTIFIED_ERROR.message());
                return result;
            }
        }

        // 根据RFC 全包校验 手机号 与 INE/IFE证件id
        LoanUserInfoEntity lastByRfc = loanUserInfoDao.findLastUserInfoLastByRfc(rfc);
        if (ObjectUtils.isNotEmpty(lastByRfc)) {
            if (!idNumber.equals(lastByRfc.getPapersId()) || !user.getLoginName().equals(lastByRfc.getMobile())) {
                result.setReturnCode(ResultEnum.INF_MOBILE_CERTIFIED_OTHER_ERROR.code());
                result.setMessage(ResultEnum.INF_MOBILE_CERTIFIED_OTHER_ERROR.message());
                return result;
            }
        }

        // 根据手机号 全包校验 rfc 与 INE/IFE证件id
        LoanUserInfoEntity lastByMobile = loanUserInfoDao.findLastUserInfoByMobile(user.getMobile());
        if (ObjectUtils.isNotEmpty(lastByMobile)) {
            if (!idNumber.equals(lastByRfc.getPapersId()) || !rfc.equals(lastByPapersId.getRfc())) {
                result.setReturnCode(ResultEnum.RFC_INF_CERTIFIED_ERROR.code());
                result.setMessage(ResultEnum.RFC_INF_CERTIFIED_ERROR.message());
                return result;
            }
        }

        // 与风控交互，获取 CURP校验结果
        JSONObject riskObject = sendRiskCheckCURPRequest(userInfoById, idNumber);
        if (ObjectUtils.isEmpty(riskObject)) {
            result.setReturnCode(ResultEnum.LOAN_RISK_EACH_ERROR.code());
            result.setMessage(ResultEnum.LOAN_RISK_EACH_ERROR.message());
            return result;
        }

        // 解析风控校验结果
        Integer code = riskObject.getInteger(Field.ERROR);
        if (code != 200) {
            result.setReturnCode(ResultEnum.RFC_CHECK_CURP_ERROR.code());
            result.setMessage(ResultEnum.RFC_CHECK_CURP_ERROR.message());
            return result;
        }
        JSONObject dataObject = riskObject.getJSONObject(Field.DATA);
        Integer dataCode = dataObject.getInteger(RiskField.RISK_DATA_ERROR_CODE);
        if (dataCode != RiskField.RISK_CURP_CHECK_PASS_CODE) {
            String errMessage = dataObject.getString(RiskField.RISK_DATA_ERROR_MESSAGE);
            result.setReturnCode(dataCode);
            result.setMessage(errMessage);
            return result;
        }

        // 上传证件正面图片
        String frontPath = BusinessNameUtils.createUserIdTypeFileName(NameField.USR_ID, user.getUserInfoId(), NameField.FRONT_IMAGE_TYPE, params.getIdFrontImgType());
        Boolean frontResult = alibabaOssClient.upload(userFileBucketName, frontPath, params.getIdFrontImgData());
        if (!frontResult) {
            result.setReturnCode(ResultEnum.KYC_UPLOAD_FILE_ERROR.code());
            result.setMessage(ResultEnum.KYC_UPLOAD_FILE_ERROR.message());
            return result;
        }

        //上传证件背面图片 并获取链接
        String backPath = BusinessNameUtils.createUserIdTypeFileName(NameField.USR_ID, user.getUserInfoId(), NameField.BACK_IMAGE_TYPE, params.getIdBackImgType());
        Boolean backResult = alibabaOssClient.upload(userFileBucketName, backPath, params.getIdBackImgData());
        if (!backResult) {
            result.setReturnCode(ResultEnum.KYC_UPLOAD_FILE_ERROR.code());
            result.setMessage(ResultEnum.KYC_UPLOAD_FILE_ERROR.message());
            return result;
        }

        //上传人脸照片
        String facePath = BusinessNameUtils.createUserIdTypeFileName(NameField.USR_ID, user.getUserInfoId(), NameField.FACE_IMAGE_TYPE, params.getFaceImgType());
        Boolean faceResult = alibabaOssClient.upload(userFileBucketName, facePath, params.getFaceImgData());
        if (!faceResult) {
            result.setReturnCode(ResultEnum.KYC_UPLOAD_FILE_ERROR.code());
            result.setMessage(ResultEnum.KYC_UPLOAD_FILE_ERROR.message());
            return result;
        }

        // 保存 图片信息
        userInfoById.setFrontPath(frontPath);
        userInfoById.setBackPath(backPath);
        userInfoById.setFacePath(facePath);

        // 处理扫描识别的证件信息和RFC，保存用户基本信息
        if (ObjectUtils.isNotEmpty(frontInfo)) {
            userInfoById.setPostalCode(frontInfo.getPostalCode());
            userInfoById.setPapersState(frontInfo.getState());
            userInfoById.setPapersAddress(frontInfo.getAddressAll());
            userInfoById.setPapersFatherName(frontInfo.getFatherLastName());
            userInfoById.setPapersName(frontInfo.getName());
            userInfoById.setPapersMotherName(frontInfo.getMotherLastName());
            userInfoById.setPapersFullName(frontInfo.getFullName());
            userInfoById.setPapersId(idNumber);
            userInfoById.setPapersVoterId(frontInfo.getVoterId());
            userInfoById.setRfc(rfc);
            userInfoById.setPapersAge(frontInfo.getAge());
            userInfoById.setPapersGender(frontInfo.getGender());
            userInfoById.setPapersDateOfBirth(frontInfo.getBirthday());
            userInfoById.setCustomName(params.getName());
            userInfoById.setCustomFatherName(params.getFatherName());
            userInfoById.setCustomMotherName(params.getMotherName());
            userInfoById.setCustomFullName(params.getFatherName() + " " + params.getName() + " " + params.getMotherName());
            userInfoById.setCurp(params.getCurp());
        }
        loanUserInfoDao.update(userInfoById);

        // 更新用户缓存
        tokenManager.updateUserCache(user.getId());

        // 封装结果
        result.setReturnCode(ResultEnum.SUCCESS.code());
        result.setMessage(ResultEnum.SUCCESS.message());
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
            if (OcrField.ADVANCE_OCR_NO_RESULT.equalsIgnoreCase(code)
                    || OcrField.ADVANCE_CARD_TYPE_NOT_MATCH.equalsIgnoreCase(code)) {
                result.setMessage(ocrInfoResult.getMessage());
            }
            return result;
        }
        UserOcrResult ocrResult = new UserOcrResult();

        // 封装响应参数
        JSONObject jsonObject = ocrInfoResult.getData();
        if (OcrField.ADVANCE_USER_OCR_ID_FRONT.equals(imageType)) {
            AdvanceOcrInfoResponse<AdvanceOcrFrontInfoResult> data = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<AdvanceOcrInfoResponse<AdvanceOcrFrontInfoResult>>() {
            });
            ocrResult.setType(data.getCardType());
            ocrResult.setInfo(JSONObject.toJSONString(data.getValues()));
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(ocrResult);
        } else if (OcrField.ADVANCE_USER_OCR_ID_BACK.equalsIgnoreCase(imageType)) {
            AdvanceOcrInfoResponse<AdvanceOcrBackInfoResult> data = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<AdvanceOcrInfoResponse<AdvanceOcrBackInfoResult>>() {
            });
            ocrResult.setType(data.getCardType());
            ocrResult.setInfo(JSONObject.toJSONString(data.getValues()));
            result.setReturnCode(ResultEnum.SUCCESS.code());
            result.setMessage(ResultEnum.SUCCESS.message());
            result.setData(ocrResult);
        }

        return result;
    }

    /**
     * 版本检查
     * @param params
     * @return
     */
    @Override
    public Result<VersionResult> checkVersion(BaseParams params){
        //结果集
        Result<VersionResult> result = new Result<>();
        VersionResult versionResult = new VersionResult();
        result.setData(versionResult);

        // 参数
        String appName = params.getAppName();
        String appVersion = params.getAppVersion();

        // 查询
        LoanAppControlEntity loanAppControlEntity = loanAppControlDao.findByAppNameAndAppVersion(appName, appVersion);

        // 封装
        if (ObjectUtils.isEmpty(loanAppControlEntity) || loanAppControlEntity.getStatus() != 1){
            versionResult.setStatus(0);
        }else {
            versionResult.setStatus(1);
        }

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

    /**
     * 请求风控CURP校验
     *
     * @param loanUserInfoEntity 用户实体类
     * @param idNumber           证件号
     * @return 风控响应信息
     */
    private JSONObject sendRiskCheckCURPRequest(LoanUserInfoEntity loanUserInfoEntity, String idNumber) {
        try {
            // 用户Id
            String userId = loanUserInfoEntity.getUserId();

            // 手机号
            String mobile = loanUserInfoEntity.getMobile();

            // 封装请求参数
            Map<String, String> params = new HashMap<>();
            params.put(Field.METHOD, RiskField.RISK_CURP_CHECK_METHOD);
            params.put(Field.APP_ID, riskConfig.getAppId());
            params.put(Field.VERSION, RiskField.RISK_VERSION_V1);
            params.put(Field.SIGN_TYPE, RiskField.RISK_SIGN_TYPE_RSA);
            params.put(Field.FORMAT, RiskField.RISK_MESSAGE_TYPE_JSON);
            params.put(Field.TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
            JSONObject bizData = new JSONObject();
            bizData.put(Field.TRANSACTION_ID, userId);
            bizData.put(RiskField.RISK_MOBILE, mobile);
            bizData.put(RiskField.RISK_CURP, idNumber);
            params.put(Field.BIZ_DATA, bizData.toJSONString());

            // 生成签名
            String paramsStr = RSAUtils.getSortParams(params);
            String sign = RSAUtils.addSign(riskConfig.getPrivateKey(), paramsStr);
            params.put(Field.SIGN, sign);

            // 请求参数
            String requestParams = JSONObject.toJSONString(params);

            // 发送请求
            String riskResult = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
            if (StringUtils.isEmpty(riskResult)) {
                return null;
            }

            // 返回响应参数
            return JSONObject.parseObject(riskResult);
        } catch (Exception e) {
            LogUtil.sysError("[UserServiceImpl sendRiskCheckCURPRequest]", e);
            return null;
        }
    }
}
