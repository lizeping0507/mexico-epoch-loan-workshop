package com.epoch.loan.workshop.mq.repayment;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.dao.mysql.LoanOrderBillDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanOrderDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanPaymentDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanRepaymentPaymentRecordDao;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.common.mq.repayment.params.DistributionRepaymentParams;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.common.redis.RedisClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.repayment
 * @className : BaseRepaymentMQListener
 * @createTime : 2022/03/09 11:01
 * @description : 放款队列基类
 */
@RefreshScope
@Component
public abstract class BaseRepaymentMQListener {

    /**
     * 还款队列生产
     */
    @Autowired
    public RepaymentMQManager repaymentMQManager;
    /**
     * 还款队列生产
     */
    @Autowired
    public OrderMQManager orderMQManager;
    /**
     * 订单
     */
    @Autowired
    public LoanOrderDao loanOrderDao;
    /**
     * 订单账单
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;
    /**
     * redis工具类
     */
    @Autowired
    public RedisClient redisClient;
    /**
     * 订单放款记录
     */
    @Autowired
    protected LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;
    /**
     * 放款渠道
     */
    @Autowired
    protected LoanPaymentDao loanPaymentDao;

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
        repaymentMQManager.consumer(messageListenerConcurrently, subExpression());
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
     * 重回队列
     *
     * @param repaymentParams 还款参数
     * @param subExpression   标签
     */
    protected void retryRepayment(RepaymentParams repaymentParams, String subExpression) throws Exception {
        repaymentMQManager.sendMessage(repaymentParams, subExpression, 60);
    }

    /**
     * 重回分配队列
     *
     * @param distributionRepaymentParams 还款参数
     * @param subExpression               标签
     */
    protected void retryDistributionRepayment(DistributionRepaymentParams distributionRepaymentParams, String subExpression) throws Exception {
        repaymentMQManager.sendMessage(distributionRepaymentParams, subExpression, 60);
    }

    /**
     * 发送到订单完结队列
     *
     * @param orderParams 队列参数
     */
    protected void sendToOrderCompleteQueue(OrderParams orderParams, String subExpression) throws Exception {
        orderMQManager.sendMessage(orderParams, subExpression);
    }


    /**
     * 修改还款支付详情状态
     *
     * @param id     id
     * @param status 状态
     */
    protected void updateRepaymentPaymentRecordStatus(String id, int status) {
        loanRepaymentPaymentRecordDao.updateStatus(id, status, new Date());
    }

    /**
     * 修改查询请求响应参数
     *
     * @param id       id
     * @param request  请求
     * @param response 响应
     */
    protected void updateSearchRequestAndResponse(String id, String request, String response) {
        loanRepaymentPaymentRecordDao.updateSearchRequestAndResponse(id, request, response, new Date());
    }

    /**
     * 更新实际支付金额
     *
     * @param id           id
     * @param actualAmount 实际支付金额
     */
    protected void updateRepaymentPaymentRecordActualAmount(String id, double actualAmount) {
        loanRepaymentPaymentRecordDao.updateRepaymentPaymentRecordActualAmount(id, actualAmount, new Date());
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

}
