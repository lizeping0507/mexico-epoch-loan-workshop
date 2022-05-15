package com.epoch.loan.workshop.common.mq;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq
 * @className : DelayMQParams
 * @createTime : 2021/11/17 17:27
 * @description : 延时队列参数
 */
@Data
public class DelayMQParams {

    /**
     * 参数
     */
    private Object params;

    /**
     * 时间
     */
    private Long time;

    /**
     * 延时时间
     */
    private int delayTime;
}
