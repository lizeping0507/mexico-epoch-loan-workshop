package com.epoch.loan.workshop.mq.collection;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.config.RiskConfig;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.mq.collection.CollectionMQManager;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.oss.LoanOssClient;
import com.epoch.loan.workshop.common.minio.LoanMinIoClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.collection
 * @className : BaseCollectionMQ
 * @createTime : 2022/2/27 16:49
 * @description : 订单催收基类
 */
@RefreshScope
@Component
public abstract class BaseCollectionMQ {

    /**
     * 催收、提还队列管理
     */
    @Autowired
    private CollectionMQManager collectionMQManager;

    /**
     * 还款支付记录
     */
    @Autowired
    public LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;

    /**
     * 订单账单
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;

    /**
     * 订单
     */
    @Autowired
    public LoanOrderDao loanOrderDao;

    /**
     * 产品
     */
    @Autowired
    public LoanProductDao loanProductDao;

    /**
     * 产品扩展
     */
    @Autowired
    public LoanProductExtDao loanProductExtDao;

    /**
     * 风控配置
     */
    @Autowired
    public RiskConfig riskConfig;

    /**
     * 用户信息
     */
    @Autowired
    public LoanUserInfoDao loanUserInfoDao;

    /**
     * 用户银行卡信息
     */
    @Autowired
    public LoanRemittanceAccountDao loanRemittanceAccountDao;

    /**
     * OSS
     */
    @Autowired
    public LoanOssClient loanOssClient;

    /**
     * minio
     */
    @Autowired
    public LoanMinIoClient loanMiNioClient;

    /**
     * 获取子类消息监听
     */
    protected abstract MessageListenerConcurrently getMessageListener();

    /**
     * 获取标签
     *
     * @return
     */
    public String subExpression() {
        return getMessageListener().getClass().getSimpleName();
    }


    /**
     * 消费任务启动
     */
    public void start() throws Exception {
        // 获取子类
        MessageListenerConcurrently messageListenerConcurrently = getMessageListener();

        // 启动队列
        collectionMQManager.consumer(messageListenerConcurrently, subExpression());
    }


    /**
     * 重回催收队列
     *
     * @param params 队列参数
     * @param tag    标签
     * @throws Exception E
     */
    public void retryCollection(CollectionParams params, String tag) throws Exception {
        // 重入放款队列
        collectionMQManager.sendMessage(params, tag, 60);
    }

    /**
     * 获取消息内容
     *
     * @param message
     * @return
     */
    public CollectionParams getMessage(Message message) throws Exception {
        // 获取消息
        byte[] body = message.getBody();
        if (ObjectUtils.isEmpty(body)) {
            throw new Exception("Message is Null");
        }

        // 转为String
        String res = new String(body);
        try {
            // 将JSON解析为对象
            return JSONObject.parseObject(res, CollectionParams.class);
        } catch (Exception e) {
            return null;
        }
    }
}
