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
     * 标识
     */
    private String id;

    /**
     * 参数
     */
    private Object params;

    /**
     * 主题
     */
    private String topic;

    /**
     * 标签
     */
    private String subExpression;
}
