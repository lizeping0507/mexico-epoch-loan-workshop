package com.epoch.loan.workshop.common.config;

import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.config
 * @className : StartConfig
 * @createTime : 2021/12/16 19:40
 * @description : 启动配置
 */
public class StartConfig {

    /**
     * 初始化配置
     */
    public static void initConfig() {
        String logPath = System.getProperty("log.path");
        if (StringUtils.isEmpty(logPath)) {
            logPath = "D://";
            System.setProperty("log.path", logPath);
        }
        LogUtil.sysInfo("log path:" + logPath);
        System.setProperty("JM.LOG.PATH", logPath);
        System.setProperty("rocketmq.client.logRoot", logPath);
        System.setProperty("csp.sentinel.log.dir", logPath);
        System.setProperty("rocketmq.client.logLevel", "WARN");
    }
}
