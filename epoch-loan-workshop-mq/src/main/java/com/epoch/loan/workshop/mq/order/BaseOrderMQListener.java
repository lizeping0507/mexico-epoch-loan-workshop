package com.epoch.loan.workshop.mq.order;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.config.RiskConfig;
import com.epoch.loan.workshop.common.constant.LoanOrderModelStatus;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderModelEntity;
import com.epoch.loan.workshop.common.mq.collection.CollectionMQManager;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.common.mq.repayment.params.DistributionRepaymentParams;
import com.epoch.loan.workshop.common.redis.RedisClient;
import com.epoch.loan.workshop.common.zookeeper.ZookeeperClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order
 * @className : OrderBaseMQ
 * @createTime : 2021/11/17 18:42
 * @description : 订单队列基类
 */
@RefreshScope
@Component
public abstract class BaseOrderMQListener {

    /**
     * Redis工具累
     */
    @Autowired
    public RedisClient redisClient;

    /**
     * 用户渠道
     */
    @Autowired
    public LoanChannelDao loanChannelDao;

    /**
     * 风控配置
     */
    @Autowired
    public RiskConfig riskConfig;

    /**
     * 订单队列生产者
     */
    @Autowired
    public OrderMQManager orderMQManager;

    /**
     * 订单
     */
    @Autowired
    public LoanOrderDao loanOrderDao;

    /**
     * 订单模型审核
     */
    @Autowired
    public LoanOrderExamineDao loanOrderExamineDao;

    /**
     * 订单账单
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;

    /**
     * 订单模型
     */
    @Autowired
    public LoanOrderModelDao loanOrderModelDao;

    /**
     * 订单支付记录
     */
    @Autowired
    public LoanRemittanceOrderRecordDao loanRemittanceOrderRecordDao;

    /**
     * 放款队列生产
     */
    @Autowired
    public RemittanceMQManager remittanceMQManagerProduct;

    /**
     * 还款队列生产
     */
    @Autowired
    public RepaymentMQManager repaymentMQManager;

    /**
     * 支付记录表
     */
    @Autowired
    public LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;

    /**
     * 用户详细信息
     */
    @Autowired
    public LoanUserInfoDao loanUserInfoDao;

    /**
     * 变身包
     */
    @Autowired
    public LoanMaskDao loanMaskDao;

    /**
     * 放款账户
     */
    @Autowired
    public LoanRemittanceAccountDao loanRemittanceAccountDao;

    /**
     * zookeeper工具类
     */
    @Autowired
    public ZookeeperClient zookeeperClient;

    /**
     * 催收还提
     */
    @Autowired
    public CollectionMQManager collectionMQManager;

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
        orderMQManager.consumer(messageListenerConcurrently, subExpression());
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
     * 队列拦截
     *
     * @param groupName 组名
     * @param modelName 模型名称
     * @return
     */
    public boolean intercept(String groupName, String modelName) {
        if (StringUtils.isEmpty(groupName)) {
            return false;
        }

        // 查询模型信息
        LoanOrderModelEntity loanOrderModelEntity = loanOrderModelDao.findModelByGroupAndModelName(groupName, modelName);

        // 判断是否存在模型信息
        if (ObjectUtils.isEmpty(loanOrderModelEntity)) {
            return true;
        }

        // 判断模型状态是否开启
        if (loanOrderModelEntity.getStatus() == LoanOrderModelStatus.STOP) {
            return true;
        }

        return false;
    }

    /**
     * 查询模型状态
     *
     * @param orderId 订单id
     * @param tag     标签
     * @return
     */
    public int getModelStatus(String orderId, String tag) {
        return loanOrderExamineDao.findOrderExamineStatus(orderId, tag);
    }

    /**
     * 获取消息内容
     *
     * @param message 消息对象
     * @return
     */
    public OrderParams getMessage(Message message) throws Exception {
        // 获取消息
        byte[] body = message.getBody();
        if (ObjectUtils.isEmpty(body)) {
            throw new Exception("Message is Null");
        }

        // 转为String
        String res = new String(body);
        try {
            // 将JSON解析为对象
            return JSONObject.parseObject(res, OrderParams.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 重试当前模型
     *
     * @param orderParams 队列消息
     * @param tag         重试标签
     * @throws Exception
     */
    public void retry(OrderParams orderParams, String tag) throws Exception {
        orderMQManager.sendMessage(orderParams, tag, 60);
    }

    /**
     * 发送订单到下一个模型
     *
     * @param orderParams 队列消息
     * @param tag         标签
     * @throws Exception
     */
    public void sendNextModel(OrderParams orderParams, String tag) throws Exception {
        // 查询模型列表
        List<String> modelList = orderParams.getModelList();
        if (CollectionUtils.isEmpty(modelList)) {
            return;
        }

        // 是否是下一个模型
        boolean isNext = false;

        // 下一个模型标签
        String nextTag = "";

        // 循环模型列表查看下一个模型
        for (String model : modelList) {
            if (isNext) {
                nextTag = model;
                break;
            }

            // 判断是否是当前步骤
            if (model.equals(tag)) {
                isNext = true;
                continue;
            }
        }

        // 最后
        if (StringUtils.isEmpty(nextTag)) {
            return;
        }

        // 发送下一个模型
        orderMQManager.sendMessage(orderParams, nextTag);
    }

    /**
     * 推送催收
     *
     * @param orderId
     * @param collectionEvent
     * @throws Exception
     */
    public void sendCollection(String orderId, int collectionEvent) throws Exception {
        // 推送催收参数
        CollectionParams collection = new CollectionParams();
        collection.setCollectionEvent(collectionEvent);
        collection.setOrderId(orderId);
        collectionMQManager.sendMessage(collection, "");
    }

    /**
     * 修改订单状态
     *
     * @param orderId 订单ID
     * @param status  状态
     */
    public void updateOrderStatus(String orderId, int status) {
        loanOrderDao.updateOrderStatus(orderId, status, new Date());
    }

    /**
     * 修改订单状态
     *
     * @param orderBillId 订单账单ID
     * @param status      状态
     */
    public void updateOrderBillStatus(String orderBillId, int status) {
        loanOrderBillDao.updateOrderBillStatus(orderBillId, status, new Date());
    }

    /**
     * 通过订单Id修改订单状态
     *
     * @param orderId 订单ID
     * @param status  状态
     */
    public void updateOrderBillStatusByOrderId(String orderId, int status) {
        loanOrderBillDao.updateOrderBillStatusByOrderId(orderId, status, new Date());
    }

    /**
     * 更新模型审核状态
     *
     * @param orderId   订单ID
     * @param modelName 模型名称
     * @param status    状态
     */
    public void updateModeExamine(String orderId, String modelName, int status) {
        loanOrderExamineDao.updateOrderExamineStatusByOrderId(orderId, modelName, status, new Date());
    }

    /**
     * 发送订单放款分配队列
     *
     * @param params 队列参数
     * @throws Exception
     */
    public void sendDistribution(DistributionRemittanceParams params) throws Exception {
        remittanceMQManagerProduct.sendMessage(params, remittanceMQManagerProduct.getDistributionSubExpression());
    }

    /**
     * 发送订单放款分配队列
     *
     * @param params 队列参数
     * @throws Exception
     */
    public void sendRepaymentDistribution(DistributionRepaymentParams params) throws Exception {
        repaymentMQManager.sendMessage(params, repaymentMQManager.getDistributionSubExpression());
    }

    /**
     * 更新订单放款时间
     *
     * @param orderId 订单ID
     */
    public void updateLoanTime(String orderId) {
        loanOrderDao.updateLoanTime(orderId, new Date(), new Date());
    }

}
