package com.epoch.loan.workshop.common.util;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.util
 * @className : CheckAllFieldUtils
 * @createTime : 2022/03/25 11:39
 * @Description: 判断对象中属性值工具类
 */
public class CheckFieldUtils {

    /**
     * 判断对象中属性值是否全为空
     *
     * @param object
     * @return
     */
    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }

        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(object) != null && StringUtils.isNotBlank(f.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
