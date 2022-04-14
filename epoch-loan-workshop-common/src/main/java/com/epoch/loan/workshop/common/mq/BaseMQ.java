package com.epoch.loan.workshop.common.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq
 * @className : BaseMQ
 * @createTime : 2021/11/17 17:27
 * @description : 队列基类
 */
@RefreshScope
@Component
public class BaseMQ {

    /**
     * MQ的地址
     */
    @Value("${rocket.nameServer}")
    public String nameServer = "";
}
