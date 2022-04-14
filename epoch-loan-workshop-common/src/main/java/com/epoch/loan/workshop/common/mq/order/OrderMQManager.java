package com.epoch.loan.workshop.common.mq.order;

import com.alibaba.fastjson.JSON;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
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
 * @packageName : com.epoch.loan.workshop.common.mq.order.params
 * @className : OrderMQ
 * @createTime : 2021/11/17 17:24
 * @description : 订单处理队列
 */
@RefreshScope
@Component
public class OrderMQManager extends BaseMQ {

    /**
     * 生产者对象
     */
    private static DefaultMQProducer producer = null;

    /**
     * 生产者所属的组
     */
    @Value("${rocket.order.producerGroup}")
    private String producerGroup = "";

    /**
     * 主题
     */
    @Value("${rocket.order.topic}")
    private String topic = "";

    /**
     * 最大线程数量
     */
    @Value("${rocket.order.consumeThreadMax}")
    public int consumeThreadMax;

    /**
     * 最小线程数量
     */
    @Value("${rocket.order.consumeThreadMin}")
    public int consumeThreadMin;

    /**
     * 次消费消息的数量
     */
    @Value("${rocket.order.consumeMessageBatchMaxSize}")
    public int consumeMessageBatchMaxSize;

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
     * @param orderParams
     * @param tag
     * @throws Exception
     */
    public void sendMessage(OrderParams orderParams, String tag) throws Exception {
        // 消息体
        Message msg = new Message();

        // 发送主题
        msg.setTopic(topic);

        // 标签
        msg.setTags(tag);

        //送消息体内容
        msg.setBody(JSON.toJSONString(orderParams).getBytes());

        // 发送消息
        producer.send(msg);
    }


    /**
     * 发送消息
     *
     * @param orderParams
     * @param tag
     * @param delayTimeLevel
     * @throws Exception
     */
    public void sendMessage(OrderParams orderParams, String tag, int delayTimeLevel) throws Exception {
        // 消息体
        Message msg = new Message();

        // 发送主题
        msg.setTopic(topic);

        // 标签
        msg.setTags(tag);

        // 延时级别
        msg.setDelayTimeLevel(delayTimeLevel);

        //送消息体内容
        msg.setBody(JSON.toJSONString(orderParams).getBytes());

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
