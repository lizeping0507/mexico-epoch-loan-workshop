package com.epoch.loan.workshop.common.mq;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.redis.RedisClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 获取消息内容
     *
     * @param message 消息对象
     * @return
     */
    public <T> T getMessage(Message message, Class<T> clazz) throws Exception {
        // 获取消息
        byte[] body = message.getBody();
        if (ObjectUtils.isEmpty(body)) {
            throw new Exception("Message is Null");
        }

        // 转为String
        String res = new String(body);
        try {
            // 将JSON解析为对象
            return JSONObject.parseObject(res, clazz);
        } catch (Exception e) {
            return null;
        }
    }


}
