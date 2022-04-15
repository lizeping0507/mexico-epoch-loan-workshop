package com.epoch.loan.workshop.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Package com.longway.daow.config.util
 * @Description: 日志帮助类，使用了Log4j2，部分使用了异步日志具体配置需要看配置
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/8/21 10:55
 */
@Component
public class LogUtil {

    /**
     * 记录：系统日志
     */
    private static final Logger sys = LogManager.getLogger("EPOCH_SYS");

    /**
     * 记录：请求日志
     */
    private static final Logger request = LogManager.getLogger("EPOCH_REQ");

    /**
     * 记录：守护进程日志
     */
    private static final Logger daemon = LogManager.getLogger("EPOCH_DAE");

    /**
     * 单列对象
     */
    private static LogUtil logUtil;

    /**
     * 静态调用方式-记录请求日志
     *
     * @param message
     */
    public static void request(Object message) {
        logUtil.requestLog(message);
    }

    /**
     * 记录守护线程info日志
     *
     * @param message
     */
    public static void daemonInfo(String message) {
        daemon.info(message);
    }

    /**
     * 记录系统异常日志
     *
     * @param message
     * @param e
     */
    public static void daemonError(String message, Exception e) {
        daemon.error(message, e);
    }

    /**
     * 记录系统异常日志
     *
     * @param message
     */
    public static void daemonError(String message) {
        daemon.error(message);
    }

    /**
     * 记录系统debug日志
     *
     * @param message
     */
    public static void sysDebug(String message) {
        sys.debug(message);
    }

    /**
     * 记录系统info日志
     *
     * @param message
     */
    public static void sysInfo(String message) {
        sys.info(message);
    }

    /**
     * 记录系统info日志,采用参数替换的方式
     *
     * @param paramString
     * @param paramVarArgs
     */
    public static void sysInfo(String paramString, Object... paramVarArgs) {
        sys.info(paramString, paramVarArgs);
    }

    /**
     * 记录系统异常日志
     *
     * @param message
     * @param e
     */
    public static void sysError(String message, Exception e) {
        sys.error(message, e);
    }

    /**
     * 记录系统异常日志
     *
     * @param message
     */
    public static void sysError(String message) {
        sys.error(message);
    }

    /**
     * 方法注入
     *
     * @param logUtil
     */
    @Autowired
    public void setLogUtil(LogUtil logUtil) {
        LogUtil.logUtil = logUtil;
    }

    /**
     * 记录请求日志
     *
     * @param message
     */
    public void requestLog(Object message) {
        request.info(JSONObject.toJSONString(message));
    }
}
