package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.params.params.request.StaticResourcesParam;
import com.epoch.loan.workshop.common.service.StaticResourcesService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.account.service;
 * @className : StaticResourcesServiceImpl
 * @createTime : 2022/3/18 14:20
 * @description : 静态资源处理接口
 */
@DubboService(timeout = 5000)
public class StaticResourcesServiceImpl implements StaticResourcesService {

    /**
     * 注册页地址命名规则
     */
    private static final String STATIC_DOMAIN_TEMPLATE = "http://static.";

    /**
     * 注册页地址命名规则
     */
    private static final String REGISTER_PAGE_TEMPLATE = "/page/register_%s.html";

    /**
     * 注册页地址命名规则
     */
    private static final String HELP_PAGE_TEMPLATE = "/page/help_%s.html";

    /**
     * 注册页地址命名规则
     */
    private static final String PRIVACY_PAGE_TEMPLATE = "/page/privacy_%s.html";

    /**
     * utr视频地址命名规则
     */
    private static final String CONTRACT_PAGE_TEMPLATE = "/page/contract.html?orderNo=";

    /**
     * utr视频地址命名规则
     */
    private static final String UTR_PAGE_TEMPLATE = "/video/%s.html";

    /**
     * 注册协议地址
     *
     * @param appName appName
     * @return 注册协议地址
     */
    @Override
    public String getRegisterPageUrl(String appName) {
        // 拼接隐私协议地址
        return splicingAppDoamin(appName) + String.format(REGISTER_PAGE_TEMPLATE, appName);
    }

    /**
     * 根据appName获取隐私协议地址
     *
     * @param appName appName
     * @return 隐私协议地址
     */
    @Override
    public String getHelpPageUrl(String appName) {
        // 拼接隐私协议地址
        return splicingAppDoamin(appName) + String.format(HELP_PAGE_TEMPLATE, appName);
    }

    /**
     * 隐私协议地址
     *
     * @param appName appName
     * @return 隐私协议地址
     */
    @Override
    public String getPrivacyPageUrl(String appName) {
        // 拼接隐私协议地址
        return splicingAppDoamin(appName) + String.format(PRIVACY_PAGE_TEMPLATE, appName);
    }

    /**
     * 根据appName获取合同地址
     *
     * @param params params
     * @return 合同地址
     */
    @Override
    public String contractPage(StaticResourcesParam params) {
        return splicingAppDoamin(params.getAppName()) + CONTRACT_PAGE_TEMPLATE + params.getOrderNo();
    }

    @Override
    public String getVideoUtrPageUrl(String appName) {
        return splicingAppDoamin(appName) + String.format(UTR_PAGE_TEMPLATE, appName);
    }


    /**
     * 拼接app请求域名
     *
     * @param appName app名称
     * @return app请求域名
     */
    private String splicingAppDoamin(String appName) {
        // app顶级域名判断
        switch (appName) {
            case "CreditoPeso":
                return STATIC_DOMAIN_TEMPLATE + "creditopeso.com";
            default:
                return null;
        }
    }
}
