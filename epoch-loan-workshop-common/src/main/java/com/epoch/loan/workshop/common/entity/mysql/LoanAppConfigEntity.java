package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformAppConfig
 * @createTime : 2022/07/18 17:06
 * @Description:
 */
@Data
public class LoanAppConfigEntity implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * appId
     */
    private Long appId;

    /**
     * key
     */
    private String configKey;

    /**
     * value
     */
    private String configValue;

    /**
     * 配置说明
     */
    private String description;
}
