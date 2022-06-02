package com.epoch.loan.workshop.common.mq.log.params;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq.log.params
 * @className : Result
 * @createTime : 2021/3/10 21:59
 * @description : 访问拦截器
 */
@Data
public class AccessLogParams extends BaseParams {
    /**
     * 响应流水号
     */
    private String serialNo;

    /**
     * 请求开始时间
     */
    private Long requestTime;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 映射地址
     */
    private String mappingUrl;

    /**
     * 请求ip
     */
    private String ip;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 当前服务地址
     */
    private String serverIp;

    /**
     * 端口
     */
    private String port;

    /**
     * 响应时间
     */
    private Long responseTime;

    /**
     * 响应数据
     */
    private String response;

    /**
     * 请求数据
     */
    private String request;

    /**
     * 访问耗时时间(MS)
     */
    private Long accessSpend;

    /**
     * 异常信息
     */
    private String ex;
}
