package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.LoanOrderEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order
 * @className : OrderDue
 * @createTime : 2021/11/16 18:11
 * @description : 订单逾期
 */
@RefreshScope
@Component
@Data
public class OrderDue extends BaseOrderMQListener implements MessageListenerConcurrently {
    /**
     * 标签
     */
    @Value("${rocket.order.orderDue.subExpression}")
    private String subExpression;

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 消费任务
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
                if (intercept("SYSTEM", subExpression)) {
                    // 等待重试
                    retry(orderParams, subExpression);
                    continue;
                }

                // 订单id
                String orderId = orderParams.getOrderId();

                // 订单账单id
                String orderBillId = orderParams.getOrderBillId();

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 根据订单账单id查询账单
                LoanOrderBillEntity orderBillEntity = loanOrderBillDao.findOrderBill(orderBillId);
                if (ObjectUtils.isEmpty(orderBillEntity)) {
                    continue;
                }

                // 更新还款金额
                loanOrderBillDao.updateOrderBillRepaymentAmount(orderBillId, orderParams.getAmount(), new Date());

                // 计算预计还款金额更新预计还款金额
                double amount = loanOrderBillDao.sumOrderRepaymentAmount(orderId);
                loanOrderDao.updateOrderEstimatedRepaymentAmount(orderId, amount, new Date());

                // 更新订单账单状态为逾期
                updateOrderBillStatus(orderBillId, OrderBillStatus.DUE);

                // 更新订单状态为逾期
                updateOrderStatus(orderId, OrderStatus.DUE);
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression);
                } catch (Exception exception) {
                    LogUtil.sysError("[OrderDue]", exception);
                }

                LogUtil.sysError("[OrderDue]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
