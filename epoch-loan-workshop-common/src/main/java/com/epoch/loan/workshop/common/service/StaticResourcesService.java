package com.epoch.loan.workshop.common.service;

import com.epoch.loan.workshop.common.params.params.request.StaticResourcesParam;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.service;
 * @className : StaticResourcesService
 * @createTime : 2022/3/18 11:08
 * @description : 静态资源处理接口
 */
public interface StaticResourcesService {

    /**
     * 注册协议地址
     *
     * @param appName appName
     * @return 注册协议地址
     */
    String getRegisterPageUrl(String appName);

    /**
     * 根据appName获取帮助中心地址
     *
     * @param appName appName
     * @return 帮助中心地址
     */
    String getHelpPageUrl(String appName);

    /**
     * 根据appName获取协议地址
     *
     * @param appName appName
     * @return 协议地址
     */
    String getPrivacyPageUrl(String appName);

    /**
     * 根据appName获取合同地址
     *
     * @param params params
     * @return 合同地址
     */
    String contractPage(StaticResourcesParam params);

    /**
     * 根据appName获取utr视频地址
     *
     * @param appName appName
     * @return 合同地址
     */
    String getVideoUtrPageUrl(String appName);
}
