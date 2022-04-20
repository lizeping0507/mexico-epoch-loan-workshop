package com.epoch.loan.workshop.common.sms.channel;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.sms.channel
 * @className : Situation
 * @createTime : 2022/4/20 15:57
 * @description : TODO 一句话描述该类的功能
 */
@Data
public class Situation {

    /**
     * 结果
     */
    private boolean result;

    /**
     * 请求
     */
    private String request;

    /**
     * 响应
     */
    private String response;
}
