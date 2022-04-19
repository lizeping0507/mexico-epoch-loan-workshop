package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanOcrProviderConfig
 * @createTime : 2022/04/18 15:18
 * @Description: curp服务商权重配置
 */
@Data
public class LoanOcrProviderConfig implements Serializable {

    /**
     * 三方服务商
     */
    private String channel;

    /**
     * 比重
     */
    private Integer proportion;

    /**
     * 描述
     */
    private String describe;

    /**
     * 状态
     */
    private Integer status;

    /**
     * appId
     */
    private Integer appId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

}
