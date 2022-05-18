package com.epoch.loan.workshop.mq.remittance;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.zookeeper.ZookeeperClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.remittance
 * @className : BaseRemittanceMQ
 * @createTime : 2021/12/16 11:01
 * @description : 支付队列基类
 */
@RefreshScope
@Component
public abstract class BaseRemittanceMQListener {

    /**
     * 银行卡
     */
    @Autowired
    public LoanRemittanceBankDao loanRemittanceBankDao;

    /**
     * 支付分配
     */
    @Autowired
    public LoanRemittanceDistributionDao loanRemittanceDistributionDao;

    /**
     * 策略配置
     */
    @Autowired
    public LoanProductRemittanceConfigDao loanProductRemittanceConfigDao;

    /**
     * 支付记录
     */
    @Autowired
    public LoanRemittanceOrderRecordDao loanRemittanceOrderRecordDao;

    /**
     * 支付记录详情
     */
    @Autowired
    public LoanRemittancePaymentRecordDao loanRemittancePaymentRecordDao;

    /**
     * 支付渠道
     */
    @Autowired
    public LoanPaymentDao loanPaymentDao;

    /**
     * 放款队列生产
     */
    @Autowired
    public RemittanceMQManager remittanceMQManager;

    /**
     * Zookeeper工具类
     */
    @Autowired
    public ZookeeperClient zookeeperClient;

    /**
     * 获取子类消息监听
     */
    protected abstract MessageListenerConcurrently getMessageListener();

    /**
     * 消费任务启动
     */
    public void start() throws Exception {
        // 获取子类
        MessageListenerConcurrently messageListenerConcurrently = getMessageListener();

        // 启动队列
        remittanceMQManager.consumer(messageListenerConcurrently, subExpression());
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
     * @param message 消息内容
     * @param clazz   消息实体类型
     * @return clazz
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

    /**
     * 重试当前模型
     *
     * @param params 队列参数
     */
    public void retryDistribution(DistributionRemittanceParams params, String tag) throws Exception {
        remittanceMQManager.sendMessage(params, tag, 60);
    }

    /**
     * 进入放款队列
     *
     * @param params 放款队列参数
     * @param tag    标签
     * @throws Exception E
     */
    public void sendToRemittance(RemittanceParams params, String tag) throws Exception {
        remittanceMQManager.sendMessage(params, tag);
    }

    /**
     * 更新支付记录状态
     */
    public void updateRemittanceOrderRecordStatus(String id, Integer status) {
        Date now = new Date();
        loanRemittanceOrderRecordDao.updateStatus(id, status, now);
    }

    /**
     * 更新支付记录渠道
     */
    public void updateRemittanceOrderRecordPayment(String id, String paymentId) {
        Date now = new Date();
        loanRemittanceOrderRecordDao.updatePaymentId(id, paymentId, now);
    }

    /**
     * 更新支付记录进行中记录Id
     */
    public void updateProcessRemittancePaymentRecordId(String id, String paymentLogId) {
        Date now = new Date();
        loanRemittanceOrderRecordDao.updateProcessRemittancePaymentRecordId(id, paymentLogId, now);
    }

    /**
     * 更新支付记录已完成记录Id
     */
    public void updateSuccessRemittancePaymentRecordId(String id, String paymentLogId) {
        Date now = new Date();
        loanRemittanceOrderRecordDao.updateSuccessRemittancePaymentRecordId(id, paymentLogId, now);
    }
}
