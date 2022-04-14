package com.epoch.loan.workshop.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformChannelEntity
 * @createTime : 2021/12/6 18:24
 * @description : 渠道信息
 */
@Data
public class PlatformChannelEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 渠道编码/包名
     */
    private String channelCode;

    /**
     * 是否启用
     */
    private Boolean isOpen;
}
