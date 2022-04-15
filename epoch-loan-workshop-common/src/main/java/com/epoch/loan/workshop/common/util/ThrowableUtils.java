package com.epoch.loan.workshop.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.util
 * @className : ThrowableUtils
 * @createTime : 2022/3/30 11:27
 * @description : 异常工具类
 */
public class ThrowableUtils {
    /**
     * 将异常信息转化为字符串
     *
     * @param throwable 异常对象
     * @return 异常信息字符串
     */
    public static String throwableToString(Throwable throwable) {
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter writer = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(writer);
            StringBuffer buffer = stringWriter.getBuffer();
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
