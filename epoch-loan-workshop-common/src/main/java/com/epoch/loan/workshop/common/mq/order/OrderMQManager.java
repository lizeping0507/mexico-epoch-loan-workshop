package com.epoch.loan.workshop.common.mq.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
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
@Data
public class OrderMQManager extends BaseMQ {

    /**
     * 生产者对象
     */
    private static DefaultMQProducer producer = null;

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
    public void sendMessage(Object orderParams, String tag) throws Exception {
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
    public void sendMessage(Object orderParams, String tag, int delayTimeLevel) throws Exception {
        // 加入redis队列中
        JSONObject result = new JSONObject();
        result.put("params", orderParams);
        result.put("time", System.currentTimeMillis());
        result.put("delayed", delayTimeLevel * 1000);
        redisClient.rPush(topic + ":" + tag, result.toJSONString());
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

        // 延时消费
        PullDelayedMessage pullDelayedMessage = new PullDelayedMessage(subExpression);
        pullDelayedMessage.start();
    }

    /**
     * 延时队列拉取消息实现
     */
    class PullDelayedMessage extends Thread {

        /**
         * 标签
         */
        private String subExpression;

        /**
         * 线程
         */
        @Override
        public void run() {
            while (true) {
                try {
                    // 消费者为空
                    if (producer == null) {
                        continue;
                    }

                    // 从队列中取出数据
                    Object object = redisClient.lPop(topic + ":" + subExpression);
                    if (ObjectUtils.isEmpty(object)) {
                        Thread.sleep(5000);
                        continue;
                    }

                    // 解析队列参数
                    JSONObject result = JSONObject.parseObject(object.toString());

                    // 判断是否达到延时时间
                    if (result.getLong("time") + result.getLong("delayed") > System.currentTimeMillis()) {
                        redisClient.lPush(topic + ":" + subExpression, result.toJSONString());
                        continue;
                    }

                    sendMessage(result.getJSONObject("params"), subExpression);
                } catch (Exception e) {
                    LogUtil.sysError("[OrderMQManager]", e);
                }
            }

        }

        /**
         * 有参构造函数
         *
         * @param subExpression
         */
        public PullDelayedMessage(String subExpression) {
            this.subExpression = subExpression;
        }
    }


}
