package com.epoch.loan.workshop.common.mq.remittance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
 * @packageName : com.epoch.loan.workshop.common.mq.remittance
 * @className : DistributionRemittanceMQProduct
 * @createTime : 2021/12/16 11:05
 * @description : 支付队列
 */
@RefreshScope
@Component
@Data
public class RemittanceMQManager extends BaseMQ {

    /**
     * 生产者对象
     */
    private static DefaultMQProducer producer = null;
    /**
     * 最大线程数量
     */
    @Value("${rocket.remittance.consumeThreadMax}")
    public int consumeThreadMax;
    /**
     * 最小线程数量
     */
    @Value("${rocket.remittance.consumeThreadMin}")
    public int consumeThreadMin;
    /**
     * 次消费消息的数量
     */
    @Value("${rocket.remittance.consumeMessageBatchMaxSize}")
    public int consumeMessageBatchMaxSize;
    /**
     * 生产者所属的组
     */
    @Value("${rocket.remittance.producerGroup}")
    private String producerGroup = "";
    /**
     * 主题
     */
    @Value("${rocket.remittance.topic}")
    private String topic = "";
    /**
     * YeahPay支付标签
     */
    @Value("${rocket.remittance.pandaPay.subExpression}")
    private String pandaPaySubExpression;


    /**
     * 分配队列标签
     */
    @Value("${rocket.remittance.distribution.subExpression}")
    private String distributionSubExpression;


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
     * @param params         队列参数
     * @param tag            标签
     * @param delayTimeLevel 延时级别
     * @throws Exception e
     */
    public void sendMessage(Object params, String tag, int delayTimeLevel) throws Exception {
        // 加入redis队列中
        JSONObject result = new JSONObject();
        result.put("params", params);
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
                    LogUtil.sysError("[RemittanceMQManager]", e);
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
