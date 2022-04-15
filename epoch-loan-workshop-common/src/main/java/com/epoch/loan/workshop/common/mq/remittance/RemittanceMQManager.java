package com.epoch.loan.workshop.common.mq.remittance;

import com.alibaba.fastjson.JSON;
import com.epoch.loan.workshop.common.mq.BaseMQ;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import lombok.Data;
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
    @Value("${rocket.remittance.yeahPay.subExpression}")
    private String yeahPaySubExpression;

    /**
     * InPay支付标签
     */
    @Value("${rocket.remittance.inPay.subExpression}")
    private String inPaySubExpression;

    /**
     * SunFlowerPay支付标签
     */
    @Value("${rocket.remittance.sunFlowerPay.subExpression}")
    private String sunFlowerPaySubExpression;

    /**
     * OceanPay支付标签
     */
    @Value("${rocket.remittance.oceanPay.subExpression}")
    private String oceanPaySubExpression;

    /**
     * AcPay支付标签
     */
    @Value("${rocket.remittance.acPay.subExpression}")
    private String acPaySubExpression;

    /**
     * IncashPay支付标签
     */
    @Value("${rocket.remittance.inCashPay.subExpression}")
    private String incashPaySubExpression;

    /**
     * IncashPay支付标签
     */
    @Value("${rocket.remittance.inCashXjdPay.subExpression}")
    private String incashXjdPaySubExpression;

    /**
     * TrustPay支付标签
     */
    @Value("${rocket.remittance.trustPay.subExpression}")
    private String trustPaySubExpression;

    /**
     * QePay支付标签
     */
    @Value("${rocket.remittance.qePay.subExpression}")
    private String qePaySubExpression;

    /**
     * HrPay支付标签
     */
    @Value("${rocket.remittance.hrPay.subExpression}")
    private String hrPaySubExpression;

    /**
     * GlobPay支付标签
     */
    @Value("${rocket.remittance.globPay.subExpression}")
    private String globPaySubExpression;

    /**
     * FastPay支付标签
     */
    @Value("${rocket.remittance.fastPay.subExpression}")
    private String fastPaySubExpression;

    /**
     * YeahPa1支付标签
     */
    @Value("${rocket.remittance.yeahPay1.subExpression}")
    private String yeahPay1SubExpression;

    /**
     * YeahPa1支付标签
     */
    @Value("${rocket.remittance.yeahPay1.subExpression}")
    private String yeahPay2SubExpression;

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
    public void sendMessage(RemittanceParams params, String tag) throws Exception {
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
    public void sendMessage(RemittanceParams params, String tag, int delayTimeLevel) throws Exception {
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
     * 发送消息
     *
     * @param distributionRemittanceParams
     * @param tag
     * @throws Exception
     */
    public void sendMessage(DistributionRemittanceParams distributionRemittanceParams, String tag) throws Exception {
        // 消息体
        Message msg = new Message();

        // 发送主题
        msg.setTopic(topic);

        // 标签
        msg.setTags(tag);

        //送消息体内容
        msg.setBody(JSON.toJSONString(distributionRemittanceParams).getBytes());

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
    public void sendMessage(DistributionRemittanceParams params, String tag, int delayTimeLevel) throws Exception {
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
