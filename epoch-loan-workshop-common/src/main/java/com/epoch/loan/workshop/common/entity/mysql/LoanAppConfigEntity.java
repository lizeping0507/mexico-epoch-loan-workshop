package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformAppConfig
 * @createTime : 2022/07/18 17:06
 * @Description: app相关配置表
 */
@Data
public class LoanAppConfigEntity implements Serializable {

    /**
     * app名称
     */
    private String appName;

    /**
     * 状态  0 停用 1 启用
     */
    private Integer status;

    /**
     * app相关配置
     */
    private String config;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
