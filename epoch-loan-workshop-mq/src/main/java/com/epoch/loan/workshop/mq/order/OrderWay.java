package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.CollectionField;
import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.mq.repayment.params.DistributionRepaymentParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order
 * @className : OrderWay
 * @createTime : 2021/11/16 18:07
 * @description : 订单在途
 */
@RefreshScope
@Component
@Data
public class OrderWay extends BaseOrderMQListener implements MessageListenerConcurrently {
    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 消费任务
     *
     * @param msgs    消息列表
     * @param context 消息轨迹对象
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        // 循环处理消息
        for (Message msg : msgs) {
            // 消息对象
            OrderParams orderParams = null;

            try {
                // 获取消息对象
                orderParams = getMessage(msg);
                if (ObjectUtils.isEmpty(orderParams)) {
                    continue;
                }

                // 队列拦截
                if (intercept(orderParams.getGroupName(), subExpression())) {
                    // 等待重试
                    retry(orderParams, subExpression());
                    continue;
                }

                // 订单id
                String orderId = orderParams.getOrderId();

                // 判断模型状态
                int status = getModelStatus(orderId, subExpression());
                if (status == OrderExamineStatus.PASS) {
                    // 发送下一模型
                    sendNextModel(orderParams, subExpression());
                    continue;
                }

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                /*计算还款金额及更新还款时间*/
                // 计算每期还款时间
                Map<Integer, Date> repaymentTimeMap = new HashMap<>();
                Date firstRepaymentTime;
                for (int i = 1; i <= loanOrderEntity.getStages(); i++) {
                    // 第一期起始时间
                    if (i == 1) {
                        firstRepaymentTime = DateUtil.addDay(new Date(), 6);
                        repaymentTimeMap.put(i, firstRepaymentTime);
                        continue;
                    }

                    // 还款时间
                    Date repaymentTime = DateUtil.addDay(new Date(), 6 * i);
                    repaymentTimeMap.put(i, repaymentTime);
                }

                // 应还金额
                double repaymentAmount = loanOrderEntity.getEstimatedRepaymentAmount() / loanOrderEntity.getStages();

                // 查询订单账单列表
                List<LoanOrderBillEntity> loanOrderBillEntityList = loanOrderBillDao.findOrderBillByOrderId(orderId);


                // 修改订单账单还款实际及还款金额
                LoanOrderBillEntity orderBillEntity = null;
                for (LoanOrderBillEntity loanOrderBillEntity : loanOrderBillEntityList) {
                    // 取期数为1的账单id
                    if (loanOrderBillEntity.getStages().equals(1)) {
                        orderBillEntity = loanOrderBillEntity;
                    }

                    // 修改应还金额
                    loanOrderBillDao.updateOrderBillRepaymentAmount(loanOrderBillEntity.getId(), repaymentAmount, new Date());

                    // 修改还款时间
                    Date repaymentTime = DateUtil.StringToDate(DateUtil.DateToString(repaymentTimeMap.get(loanOrderBillEntity.getStages()), "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
                    loanOrderBillEntity.setRepaymentTime(repaymentTime);
                    loanOrderBillDao.updateOrderBillRepaymentTime(loanOrderBillEntity.getId(), repaymentTime, new Date());
                }

                // 修改订单和订单账单状态为在途
                updateOrderStatus(orderId, OrderStatus.WAY);
                updateOrderBillStatusByOrderId(orderId, OrderBillStatus.WAY);

                // 修改审核状态
                updateModeExamine(orderId, subExpression(), OrderExamineStatus.PASS);

                // 发送还款策略组分配队列计算还款策略
                DistributionRepaymentParams distributionRepaymentParams = new DistributionRepaymentParams();
                distributionRepaymentParams.setOrderId(orderId);
                distributionRepaymentParams.setOrderBillId(orderBillEntity.getId());
                sendRepaymentDistribution(distributionRepaymentParams);

                // 推送催收
                sendCollection(orderId, CollectionField.EVENT_CREATE);

                // 发送到下一模型
                sendNextModel(orderParams, subExpression());
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression(), OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[OrderWay]", exception);
                }

                LogUtil.sysError("[OrderWay]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
