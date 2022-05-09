package com.epoch.loan.workshop.common.mq.collection;

import com.alibaba.fastjson.JSON;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq.collection
 * @className : CollectionMQManager
 * @createTime : 2022/2/27 16:43
 * @description : 催收还提
 */
@RefreshScope
@Component
public class CollectionMQManager extends BaseMQ {

    /**
     * 生产者对象
     */
    private static DefaultMQProducer producer = null;
    /**
     * 最大线程数量
     */
    @Value("${rocket.collection.consumeThreadMax}")
    public int consumeThreadMax;
    /**
     * 最小线程数量
     */
    @Value("${rocket.collection.consumeThreadMin}")
    public int consumeThreadMin;
    /**
     * 次消费消息的数量
     */
    @Value("${rocket.collection.consumeMessageBatchMaxSize}")
    public int consumeMessageBatchMaxSize;
    /**
     * 生产者所属的组
     */
    @Value("${rocket.collection.producerGroup}")
    private String producerGroup = "";
    /**
     * 主题
     */
    @Value("${rocket.collection.topic}")
    private String topic = "";

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
    public void sendMessage(CollectionParams params, String tag) throws Exception {
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
     * 发送延时消息
     *
     * @param params
     * @param tag
     * @throws Exception
     */
    public void sendMessage(CollectionParams params, String tag, int delayTimeLevel) throws Exception {
        // 消息体
        Message msg = new Message();

        // 发送主题
        msg.setTopic(topic);

        // 标签
        msg.setTags(tag);

        // 延时级别
        msg.setDelayTimeLevel(delayTimeLevel);

        //送消息体内容
        msg.setBody(JSON.toJSONString(params).getBytes());

        // 发送消息
        producer.send(msg);
    }

    /**
     * 消费者
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
