package com.epoch.loan.workshop.common.mq.log;

import com.alibaba.fastjson.JSON;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.DelayMQParams;
import com.epoch.loan.workshop.common.mq.log.params.AccessLogParams;
import com.epoch.loan.workshop.common.redis.RedisClient;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import lombok.Data;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq.log
 * @className : LogMQManager
 * @createTime : 2022/3/21 15:07
 * @description : 日志处理队里
 */
@RefreshScope
@Component
@Data
public class LogMQManager extends BaseMQ {

    /**
     * 生产者对象
     */
    private static DefaultMQProducer producer = null;
    /**
     * Redis链接类
     */
    @Autowired
    public RedisClient redisClient;
    /**
     * 最大线程数量
     */
    @Value("${rocket.log.consumeThreadMax}")
    public int consumeThreadMax;
    /**
     * 最小线程数量
     */
    @Value("${rocket.log.consumeThreadMin}")
    public int consumeThreadMin;
    /**
     * 次消费消息的数量
     */
    @Value("${rocket.log.consumeMessageBatchMaxSize}")
    public int consumeMessageBatchMaxSize;
    /**
     * 生产者所属的组
     */
    @Value("${rocket.log.producerGroup}")
    private String producerGroup = "";
    /**
     * 主题
     */
    @Value("${rocket.log.topic}")
    private String topic = "";
    /**
     * 请求日志标签
     */
    @Value("${rocket.log.accessLogStorage.subExpression}")
    private String requestLogStorageSubExpression;

    /**
     * 初始化
     */
    public void init() throws MQClientException {
        producer = new DefaultMQProducer();
        producer.setProducerGroup(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setRetryTimesWhenSendFailed(3);
        producer.setSendMessageWithVIPChannel(false);
        producer.start();
    }

    /**
     * 发送消息
     *
     * @param params
     * @param tag
     * @throws Exception
     */
    public void sendMessage(Object params, String tag) throws Exception {
        // 消息体
        Message msg = new Message();

        // 发送主题
        msg.setTopic(topic);

        // 标签
        msg.setTags(tag);

        //送消息体内容
        msg.setBody(JSON.toJSONString(params).getBytes());

        // 发送消息
        producer.send(msg);
    }

    /**
     * 发送消息
     *
     * @param params
     * @param subExpression
     * @param delayTimeLevel
     * @throws Exception
     */
    public void sendMessage(Object params, String subExpression, int delayTimeLevel) throws Exception {
        // 生成标识
        String id = ObjectIdUtil.getObjectId();

        // 封装参数
        DelayMQParams delayMQParams = new DelayMQParams();
        delayMQParams.setParams(params);
        delayMQParams.setId(id);
        delayMQParams.setTopic(topic);
        delayMQParams.setSubExpression(subExpression);

        // 增加延时标识
        redisClient.set(RedisKeyField.MQ_DELAY + id, JSON.toJSONString(delayMQParams));
        redisClient.set(RedisKeyField.MQ_DELAY_INDEX + id, "", delayTimeLevel);
    }


    /**
     * 消费者
     *
     * @param messageListenerConcurrently
     * @param subExpression
     * @throws MQClientException
     */
    public void consumer(MessageListenerConcurrently messageListenerConcurrently, String subExpression) throws MQClientException {

        // 定义消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(producerGroup + "_" + subExpression);

        // 程序第一次启动从消息队列头获取数据
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 订阅主题及标签
        consumer.subscribe(topic, subExpression);

        // nameServer地址
        consumer.setNamesrvAddr(nameServer);

        // 最大线程数量
        consumer.setConsumeThreadMax(consumeThreadMax);

        // 最小线程数量
        consumer.setConsumeThreadMin(consumeThreadMin);

        //可以修改每次消费消息的数量，默认设置是每次消费一条
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);

        // 执行方法
        consumer.registerMessageListener(messageListenerConcurrently);

        // 开始消费
        consumer.start();
    }
}
