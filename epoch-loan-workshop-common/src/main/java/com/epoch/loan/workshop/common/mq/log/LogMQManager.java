package com.epoch.loan.workshop.common.mq.log;

import com.alibaba.fastjson.JSON;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.DelayMQParams;
import com.epoch.loan.workshop.common.mq.log.params.AccessLogParams;
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
        // 消息体
        Message msg = new Message();

        // 发送主题
        msg.setTopic(topic);

        // 标签
        msg.setTags(subExpression + "_Delay");

        // 封装参数
        DelayMQParams delayMQParams = new DelayMQParams();
        delayMQParams.setParams(params);
        delayMQParams.setTime(System.currentTimeMillis());
        delayMQParams.setDelayTime(delayTimeLevel);
        msg.setBody(JSON.toJSONString(delayMQParams).getBytes());

        // 发送消息
        producer.send(msg);
    }

    /**
     * 延时消费队列
     *
     * @param topic
     * @param subExpression
     * @param producerGroup
     * @param threadNumber
     * @throws MQClientException
     */
    protected void delayConsumer(String topic, String subExpression, String producerGroup, int threadNumber) throws MQClientException {
        // 定义消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(producerGroup + "_" + subExpression + "_Delay");

        // 程序第一次启动从消息队列头获取数据
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 订阅主题及标签
        consumer.subscribe(topic, subExpression + "_Delay");

        // nameServer地址
        consumer.setNamesrvAddr(nameServer);

        // 最大线程数量
        consumer.setConsumeThreadMax(threadNumber);

        // 最小线程数量
        consumer.setConsumeThreadMin(threadNumber);

        //可以修改每次消费消息的数量，默认设置是每次消费一条
        consumer.setConsumeMessageBatchMaxSize(1);

        // 执行方法
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                // 循环消费
                for (MessageExt messageExt : messageExtList) {
                    // 消息对象
                    DelayMQParams delayMQParams;

                    try {
                        // 获取消息对象
                        delayMQParams = getMessage(messageExt, DelayMQParams.class);

                        // 未达到指定时间
                        long timeStamp =  + delayMQParams.getTime() + delayMQParams.getDelayTime() * 1000L;
                        if (timeStamp > System.currentTimeMillis()) {
                            Thread.sleep(1 * 1000);
                            // 加入延时队列继续等待
                            // 消息体
                            Message msg = new Message();
                            msg.setTopic(topic);
                            msg.setTags(subExpression + "_Delay");
                            msg.setBody(JSON.toJSONString(delayMQParams).getBytes());
                            producer.send(msg);
                            continue;
                        }

                        // 发送消费队列
                        sendMessage(delayMQParams.getParams(), subExpression);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                // 处理成功
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 开始消费
        consumer.start();
    }

    /**
     * 消费者
     *
     * @param messageListenerConcurrently
     * @param subExpression
     * @throws MQClientException
     */
    public void consumer(MessageListenerConcurrently messageListenerConcurrently, String subExpression) throws MQClientException {
        // 定义延时消息队列
        delayConsumer(topic, subExpression, producerGroup, consumeThreadMax);

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
