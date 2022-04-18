package com.epoch.loan.workshop.common.util;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.params.User;
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
     * 更新用户缓存
     *
     * @param user
     */
    public void updateUserCache(User user) {

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
        redisUtil.set(RedisKeyField.TOKEN + token, userId);
        redisUtil.set(RedisKeyField.USER_TOKEN + userId, token);
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
        User user = JSONObject.parseObject(String.valueOf(userCacheObject), User.class);
        return user;
    }
}
