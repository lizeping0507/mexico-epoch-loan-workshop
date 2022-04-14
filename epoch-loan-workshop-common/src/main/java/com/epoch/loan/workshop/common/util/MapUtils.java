package com.epoch.loan.workshop.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建日期: 2021/1/24 14:19
 *
 * @author HaoRuYi
 */
public class MapUtils {
    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, Object> paraObjectFilter(Map<String, Object> sArray) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key) == null ? "" : sArray.get(key).toString();
            if (value == null || "".equals(value) || "sign".equalsIgnoreCase(key)
                    || "signType".equalsIgnoreCase(key)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
}
