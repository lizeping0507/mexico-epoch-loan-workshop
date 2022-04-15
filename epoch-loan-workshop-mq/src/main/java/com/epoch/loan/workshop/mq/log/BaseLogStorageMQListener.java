package com.epoch.loan.workshop.mq.log;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.dao.elastic.AccessLogElasticDao;
import com.epoch.loan.workshop.common.mq.log.LogMQManager;
import com.epoch.loan.workshop.common.mq.log.params.AccessLogParams;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.log
 * @className : BaseLogStorageMQListener
 * @createTime : 2022/3/21 12:01
 * @description : 日志·队列基类
 */
@RefreshScope
@Component
public abstract class BaseLogStorageMQListener {
    /**
     * 日志队列
     */
    @Autowired
    public LogMQManager logMQManager;

    /**
     * 日志记录
     */
    @Autowired
    public AccessLogElasticDao accessLogElasticDao;

    /**
     * 获取子类消息监听
     *
     * @return
     */
    protected abstract MessageListenerConcurrently getMessageListener();

    /**
     * 消费任务启动
     */
    public void start() throws Exception {
        // 获取子类
        MessageListenerConcurrently messageListenerConcurrently = getMessageListener();

        // 启动队列
        logMQManager.consumer(messageListenerConcurrently, subExpression());
    }

    /**
     * 获取标签
     *
     * @return
     */
    public String subExpression() {
        return getMessageListener().getClass().getSimpleName();
    }

    /**
     * 获取消息内容
     *
     * @param message 消息对象
     * @return
     */
    public AccessLogParams getMessage(Message message) throws Exception {
        // 获取消息
        byte[] body = message.getBody();
        if (ObjectUtils.isEmpty(body)) {
            throw new Exception("Message is Null");
        }

        // 转为String
        String res = new String(body);
        try {
            // 将JSON解析为对象
            return JSONObject.parseObject(res, AccessLogParams.class);
        } catch (Exception e) {
            return null;
        }
    }
}
