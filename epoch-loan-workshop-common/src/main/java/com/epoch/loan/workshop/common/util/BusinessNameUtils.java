package com.epoch.loan.workshop.common.util;

import com.epoch.loan.workshop.common.constant.Field;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.UrlBase64;
import org.bouncycastle.util.encoders.UrlBase64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.util
 * @className : BusinessNameUtils
 * @createTime : 2022/04/22 16:35
 * @Description: 名称生成工具类
 */
public class BusinessNameUtils {

    public static final LocalDateTime LocalDate3000 = LocalDateTime.parse("3000-01-01T00:00:00.000");

    /**
     * 生成文件路径
     * @param path 用户图片存储基础路径
     * @param userInfoId 用户详情id
     * @param type 类型
     * @return 文件路径
     */
    public static String createUserIdTypeFileName(String path, String userInfoId, String type) {
        LocalDateTime now = LocalDateTime.now();
        long millis = now.until(LocalDate3000, ChronoUnit.MILLIS);
        StringBuilder fileName = new StringBuilder().append(path)
                .append("/").append(userInfoId)
                .append("/").append(type)
                .append("/").append(millis);
        return fileName.toString();
    }
}
