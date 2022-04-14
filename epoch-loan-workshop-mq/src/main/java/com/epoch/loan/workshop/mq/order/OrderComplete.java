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
 * @className : OrderComplete
 * @createTime : 2021/11/16 18:09
 * @description : 订单完成-已还款
 */
@RefreshScope
@Component
@Data
public class OrderComplete extends BaseOrderMQListener implements MessageListenerConcurrently {

    /**
     * 标签
     */
    @Value("${rocket.order.orderComplete.subExpression}")
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

                // 判断订单状态是否为废弃
                if (loanOrderEntity.getStatus() == OrderStatus.ABANDONED) {
                    continue;
                }

                // 判断订单状态是否未放款
                if (loanOrderEntity.getStatus() <= OrderStatus.WAIT_PAY) {
                    continue;
                }

                // 根据订单账单id查询账单
                LoanOrderBillEntity orderBillEntity = loanOrderBillDao.findOrderBill(orderBillId);
                if (ObjectUtils.isEmpty(orderBillEntity)) {
                    continue;
                }

                // 查看本期账单是否有逾期
                int orderBillStatus = OrderBillStatus.COMPLETE;
                if (orderBillEntity.getStatus() == OrderBillStatus.DUE) {
                    orderBillStatus = OrderBillStatus.DUE_COMPLETE;
                }

                // 更新实际还款时间
                loanOrderBillDao.updateOrderBillActualRepaymentTime(orderBillId, new Date(), new Date());

                // 更新订单账单状态
                updateOrderBillStatus(orderBillId, orderBillStatus);

                // 计算已还款金额更新实际还款总额
                double amount = loanOrderBillDao.sumOrderCompleteRepaymentAmount(orderId);
                loanOrderDao.updateOrderActualRepaymentAmount(orderId, amount, new Date());

                // 查询未还款订单账单数量
                int orderBillNotCompleteCount = loanOrderBillDao.findOrderNotCompleteCount(orderId);
                if (orderBillNotCompleteCount > 0) {
                    /* 未全部还款 */
                    // 更新订单状态为在途
                    updateOrderStatus(orderId, OrderStatus.WAY);
                    continue;
                } else {
                    /* 全部还款 */
                    // 查询是否存在结清-有逾期订单
                    int orderStatus = OrderStatus.COMPLETE;
                    int dueComplete = loanOrderBillDao.findOrderDueCompleteCount(orderId);
                    if (dueComplete > 0) {
                        orderStatus = OrderStatus.DUE_COMPLETE;
                    }

                    // 更新订单状态
                    updateOrderStatus(orderId, orderStatus);
                }
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression);
                } catch (Exception exception) {
                    LogUtil.sysError("[OrderComplete]", exception);
                }

                LogUtil.sysError("[OrderComplete]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
