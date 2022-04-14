package com.epoch.loan.workshop.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Package com.epoch.remind.entity
 * @Description: 定时任务配置类
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/11/4 16:06
 */
@Data
public class LoanTimingEntity implements Serializable {
    /**
     * 任务名称
     */
    private String name;

    /**
     * 执行时间
     */
    private String time;

    /**
     * 任务参数
     */
    private String params;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 执行任务类型
     */
    private Integer type;

    /**
     * 执行任务类
     */
    private String classPath;

    /**
     * 上次执行时间
     */
    private Date lastRunTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}