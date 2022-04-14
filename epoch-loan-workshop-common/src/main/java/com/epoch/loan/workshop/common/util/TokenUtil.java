package com.epoch.loan.workshop.common.util;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.params.system.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.util
 * @className : TokenUtil
 * @createTime : 2021/11/3 16:03
 * @description : Token工具类
 */
@Component
public class TokenUtil {

    /**
     * Redis工具类
     */
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户安全密匙
     *
     * @param token
     * @return
     */
    public String getUserSecretKey(String token) {
        // Token有效性校验
        if (!userOnline(token)) {
            return null;
        }

        // 通过redis获取用户信息
        Object userIdObject = redisUtil.get(RedisKeyField.TOKEN + token);
        if (ObjectUtils.isEmpty(userIdObject)) {
            return null;
        }

        // 用户ID
        String userId = String.valueOf(userIdObject);

        // 用户缓存
        Object userCacheObject = redisUtil.get(RedisKeyField.USER_CACHE + userId);
        if (ObjectUtils.isEmpty(userCacheObject)) {
            return null;
        }

        // 用户缓存
        UserCache userCache = JSONObject.parseObject(String.valueOf(userCacheObject), UserCache.class);
        return userCache.getDeviceId();
    }

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
        Object userIdObject = redisUtil.get(RedisKeyField.TOKEN + token);
        if (ObjectUtils.isEmpty(userIdObject)) {
            return false;
        }

        // 用户ID
        String userId = String.valueOf(userIdObject);

        // 判断用户Token是否为最新
        Object userTokenObject = redisUtil.get(RedisKeyField.USER_TOKEN + userId);
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
     * 获取用户
     *
     * @param token
     * @return
     */
    public UserCache getUserCache(String token) {
        // Token有效性校验
        if (!userOnline(token)) {
            return null;
        }

        // 通过redis获取用户信息
        Object userIdObject = redisUtil.get(RedisKeyField.TOKEN + token);
        if (ObjectUtils.isEmpty(userIdObject)) {
            return null;
        }

        // 用户ID
        String userId = String.valueOf(userIdObject);

        // 用户缓存
        Object userCacheObject = redisUtil.get(RedisKeyField.USER_CACHE + userId);
        if (ObjectUtils.isEmpty(userCacheObject)) {
            return null;
        }

        // 用户缓存
        UserCache userCache = JSONObject.parseObject(String.valueOf(userCacheObject), UserCache.class);
        return userCache;
    }
}
