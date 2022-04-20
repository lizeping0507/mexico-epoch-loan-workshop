package com.epoch.loan.workshop.common.entity.mysql;

import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanSmsProviderConfig
 * @createTime : 2022/04/18 19:34
 * @Description:  短信服务商权重配置
 */
public class LoanSmsProviderConfig {

    /**
     * 三方服务商编号
     */
    private String channelCode;

    /**
     * 比重
     */
    private Integer proportion;

    /**
     * 状态  0 停用 1 启用
     */
    private Integer status;

    /**
     * 渠道配置
     */
    private String config;

    /**
     * app标识
     */
    private Integer appName;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
