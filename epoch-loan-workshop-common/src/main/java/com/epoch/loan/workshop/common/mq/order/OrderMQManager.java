package com.epoch.loan.workshop.common.mq.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.DelayMQParams;
import com.epoch.loan.workshop.common.util.LogUtil;
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
 * @packageName : com.epoch.loan.workshop.common.mq.order.params
 * @className : OrderMQ
 * @createTime : 2021/11/17 17:24
 * @description : 订单处理队列
 */
@RefreshScope
@Component
@Data
public class OrderMQManager extends BaseMQ {

    /**
     * 生产者对象
     */
    protected static DefaultMQProducer producer = null;

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
     * 标签
     */
    @Value("${rocket.order.orderComplete.subExpression}")
    private String orderCompleteSubExpression;

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
     * 逾期TAG
     */
    @Value("${rocket.order.orderDue.subExpression}")
    private String orderDueSubExpression = "";

    /**
     * 风控V3TAG
     */
    @Value("${rocket.order.riskModelMask.subExpression}")
    private String riskModelMaskSubExpression = "";

    /**
     * 获取消费者对象
     */
    protected DefaultMQProducer getProduct() throws MQClientException {
        // 如果消费者对象不为空直接返回消费者对象
        if (producer != null) {
            return producer;
        }

        producer = new DefaultMQProducer();
        producer.setProducerGroup(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setRetryTimesWhenSendFailed(3);
        producer.setSendMessageWithVIPChannel(false);
        producer.start();
        return producer;
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
        this.getProduct().send(msg);
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
        msg.setTags(subExpression + "_delay");

        // 封装参数
        DelayMQParams delayMQParams = new DelayMQParams();
        delayMQParams.setParams(params);
        delayMQParams.setTime(System.currentTimeMillis());
        delayMQParams.setDelayTime(delayTimeLevel);
        msg.setBody(JSON.toJSONString(delayMQParams).getBytes());

        // 发送消息
        this.getProduct().send(msg);
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
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(producerGroup + "_" + subExpression + "_delay");

        // 程序第一次启动从消息队列头获取数据
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 订阅主题及标签
        consumer.subscribe(topic, subExpression + "_delay");

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
                        LogUtil.sysError("[delayMQParams]"+ JSONObject.toJSONString(delayMQParams));


                        // 未达到指定时间
                        if (delayMQParams.getDelayTime() + delayMQParams.getTime() * 1000 > System.currentTimeMillis()) {
                            LogUtil.sysError("[delayMQParams not]" + JSONObject.toJSONString(delayMQParams));

                            // 加入延时队列继续等待
                            sendMessage(delayMQParams.getParams(), subExpression, delayMQParams.getDelayTime());
                            continue;
                        }

                        LogUtil.sysError("[delayMQParams yes]"+ JSONObject.toJSONString(delayMQParams));

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
