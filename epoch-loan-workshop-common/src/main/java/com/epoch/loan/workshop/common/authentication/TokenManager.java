package com.epoch.loan.workshop.common.authentication;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.dao.mysql.LoanRemittanceAccountDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanUserDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanUserInfoDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.redis.RedisClient;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.util
 * @className : TokenUtil
 * @createTime : 2021/11/3 16:03
 * @description : Token管理器
 */
@Component
public class TokenManager {

    /**
     * Redis工具类
     */
    @Autowired
    private RedisClient redisClient;

    /**
     * 新用户表
     */
    @Autowired
    public LoanUserDao loanUserDao;
    /**
     * 新用户详细表
     */
    @Autowired
    public LoanUserInfoDao loanUserInfoDao;

    /**
     * 放款账户
     */
    @Autowired
    public LoanRemittanceAccountDao loanRemittanceAccountDao;

    /**
     * 用户是否在线
     *
     * @param token
     * @return
     */
    public boolean userOnline(String token) {
        // 非空校验
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        // 通过redis获取用户信息
        Object userIdObject = redisClient.get(RedisKeyField.TOKEN + token);
        if (ObjectUtils.isEmpty(userIdObject)) {
            return false;
        }

        // 用户ID
        String userId = String.valueOf(userIdObject);

        // 判断用户Token是否为最新
        Object userTokenObject = redisClient.get(RedisKeyField.USER_TOKEN + userId);
        if (ObjectUtils.isEmpty(userTokenObject)) {
            return false;
        }

        // 用户Token
        String userToken = String.valueOf(userTokenObject);
        if (!token.equals(userToken)) {
            return false;
        }

        // 在线
        return true;
    }

    /**
     * 更新用户缓存
     *
     * @param userId
     */
    public void updateUserCache(String userId) {
        User user = new User();
        LoanUserEntity userEntity = loanUserDao.findById(userId);
        LoanUserInfoEntity userInfoEntity = loanUserInfoDao.findUserInfoById(userId);

        user.setId(userEntity.getId());
        user.setAndroidId(userEntity.getAndroidId());
        user.setChannelId(userEntity.getChannelId());
        user.setGaId(userEntity.getGaId());
        user.setImei(userEntity.getImei());
        user.setPlatform(userEntity.getPlatform());
        user.setLoginName(userEntity.getLoginName());
        user.setPassword(userEntity.getPassword());
        user.setAppName(userEntity.getAppName());
        user.setAppVersion(userEntity.getAppVersion());
        user.setUserInfoId(userInfoEntity.getId());
        user.setMobile(userInfoEntity.getMobile());
        user.setGps(userInfoEntity.getGps());
        user.setGpsAddress(userInfoEntity.getGpsAddress());
        user.setRegisterGps(userInfoEntity.getRegisterGps());
        user.setRegisterAddress(userInfoEntity.getRegisterAddress());
        user.setIp(userInfoEntity.getIp());
        user.setIpAddress(userInfoEntity.getIpAddress());
        user.setContacts(userInfoEntity.getContacts());
        user.setMonthlyIncome(userInfoEntity.getMonthlyIncome());
        user.setPayPeriod(userInfoEntity.getPayPeriod());
        user.setOccupation(userInfoEntity.getOccupation());
        user.setPayMethod(userInfoEntity.getPayMethod());
        user.setEmail(userInfoEntity.getEmail());
        user.setEducation(userInfoEntity.getEducation());
        user.setMarital(userInfoEntity.getMarital());
        user.setChildrenNumber(userInfoEntity.getChildrenNumber());
        user.setLoanPurpose(userInfoEntity.getLoanPurpose());
        user.setLiveType(userInfoEntity.getLiveType());
        user.setPapersState(userInfoEntity.getPapersState());
        user.setPapersAddress(userInfoEntity.getPapersAddress());
        user.setPapersFatherName(userInfoEntity.getPapersFatherName());
        user.setPapersName(userInfoEntity.getPapersName());
        user.setPapersMotherName(userInfoEntity.getPapersMotherName());
        user.setPapersFullName(userInfoEntity.getPapersFullName());
        user.setPapersId(userInfoEntity.getPapersId());
        user.setPapersVoterId(userInfoEntity.getPapersVoterId());
        user.setPapersGender(userInfoEntity.getPapersGender());
        user.setPapersAge(userInfoEntity.getPapersAge());
        user.setPapersDateOfBirth(userInfoEntity.getPapersDateOfBirth());
        user.setPostalCode(userInfoEntity.getPostalCode());
        user.setRfc(userInfoEntity.getRfc());
        user.setCustomFatherName(userInfoEntity.getCustomFatherName());
        user.setCustomName(userInfoEntity.getCustomName());
        user.setCustomMotherName(userInfoEntity.getCustomMotherName());
        user.setCustomFullName(userInfoEntity.getCustomFullName());
        user.setFrontPath(userInfoEntity.getFrontPath());
        user.setBackPath(userInfoEntity.getBackPath());
        user.setFacePath(userInfoEntity.getFacePath());
        user.setRemittanceAccountAuth(true);
        user.setCurp(userInfoEntity.getCurp());

        // 查询放款账户数量
        Integer remittanceAccountCount = loanRemittanceAccountDao.findUserRemittanceAccountCount(userId);
        if (remittanceAccountCount == null || remittanceAccountCount == 0) {
            user.setRemittanceAccountAuth(false);
        }
        // ocr认证状况
        if (StringUtils.isNotEmpty(user.getPapersId())){
            user.setIdentityAuth(true);
        }
        // 用户信息认证状况
        if (StringUtils.isNotEmpty(user.getChildrenNumber())){
            user.setAddInfoAuth(true);
        }
        // 基本信息认证状况
        if (StringUtils.isNotEmpty(user.getMonthlyIncome())){
            user.setBasicInfoAuth(true);
        }

        redisClient.set(RedisKeyField.USER_CACHE + user.getId(), JSONObject.toJSONString(user));
    }

    /**
     * 更新用户Token
     *
     * @param userId
     * @return
     */
    public String updateUserToken(String userId) {
        // 生成Token
        String token = ObjectIdUtil.getObjectId();

        // 增加Token
        redisClient.set(RedisKeyField.TOKEN + token, userId);
        redisClient.set(RedisKeyField.USER_TOKEN + userId, token);
        return token;
    }

    /**
     * 获取用户
     *
     * @param token
     * @return
     */
    public User getUserCache(String token) {
        // Token有效性校验
        if (!userOnline(token)) {
            return null;
        }

        // 通过redis获取用户信息
        Object userIdObject = redisClient.get(RedisKeyField.TOKEN + token);
        if (ObjectUtils.isEmpty(userIdObject)) {
            return null;
        }

        // 用户ID
        String userId = String.valueOf(userIdObject);

        // 用户缓存
        Object userCacheObject = redisClient.get(RedisKeyField.USER_CACHE + userId);
        if (ObjectUtils.isEmpty(userCacheObject)) {
            return null;
        }

        // 用户缓存
        User user = JSONObject.parseObject(String.valueOf(userCacheObject), User.class);
        return user;
    }
}
