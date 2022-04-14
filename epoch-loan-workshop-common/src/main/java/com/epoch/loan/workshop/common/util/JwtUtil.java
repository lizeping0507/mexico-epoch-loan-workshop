package com.epoch.loan.workshop.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package com.longway.daow.util
 * @Description: JWT工具类
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/9/4 15:16
 */
public class JwtUtil {
    /**
     * 设置过期时间及密匙
     * CALENDAR_FIELD 时间单位
     * CALENDAR_INTERVAL 有效时间
     * SECRET_KEY 密匙
     */
    public static final int CALENDAR_FIELD = Calendar.MINUTE;
    public static final int CALENDAR_INTERVAL = 60 * 24 * 30;
    private static final String SECRET_KEY = "6A50A18D70FA63636645C65459F1D78A";

    /**
     * 创建Token
     *
     * @param userMap 自己需要存储进token中的信息
     * @return token
     */
    public static String createToken(HashMap<String, Object> userMap) {
        // 头部
        Map<String, Object> headerMap = new HashMap<>(4);
        headerMap.put("alg", "HS256");
        headerMap.put("typ", "JWT");

        // 当前时间与过期时间
        Calendar time = Calendar.getInstance();
        Date now = time.getTime();
        time.add(CALENDAR_FIELD, CALENDAR_INTERVAL);
        Date expireTime = time.getTime();

        // 选择签名加密算法
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        // 创建token并返回
        return JWT.create().withHeader(headerMap)
                .withIssuedAt(now)
                .withExpiresAt(expireTime)
                .withSubject("user")
                .withClaim("userInfo", userMap)
                .sign(algorithm);
    }

    /**
     * 验证、解析Token
     *
     * @param token 用户提交的token
     * @return 该token中的信息
     */
    public static Map<String, Object> verifyToken(String token) {
        DecodedJWT verifier = null;
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        try {
            verifier = JWT.require(algorithm).build().verify(token);
        } catch (Exception e) {
            return null;
        }
        assert verifier != null;
        return verifier.getClaim("userInfo").asMap();
    }
}
