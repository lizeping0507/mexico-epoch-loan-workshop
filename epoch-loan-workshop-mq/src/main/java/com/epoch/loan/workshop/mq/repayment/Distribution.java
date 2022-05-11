package com.epoch.loan.workshop.mq.repayment;

import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
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
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.repayment
 * @className : DistributionRepayment
 * @createTime : 2022/4/7 17:55
 * @description : 支付分配
 */
@RefreshScope
@Component
@Data
public class Distribution extends BaseRepaymentMQListener implements MessageListenerConcurrently {
    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    public static void main(String[] args) {

        System.out.println();


    }

    /**
     * 消费任务
     *
     * @param msgs
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        // 循环处理消息
        for (Message msg : msgs) {
            // 消息对象
            DistributionRepaymentParams distributionRepaymentParams = null;

            try {
                // 获取消息对象
                distributionRepaymentParams = getMessage(msg, DistributionRepaymentParams.class);
                if (ObjectUtils.isEmpty(distributionRepaymentParams)) {
                    continue;
                }

                // 订单id
                String orderId = distributionRepaymentParams.getOrderId();

                // 订单账单id
                String orderBillId = distributionRepaymentParams.getOrderBillId();

                // 判断是否正在计算逾期
                Object orderBillDueLock = redisClient.get(RedisKeyField.ORDER_BILL_DUE_LOCK + orderId);
                if (ObjectUtils.isNotEmpty(orderBillDueLock)) {
                    // 等待重试
                    retryDistributionRepayment(distributionRepaymentParams, subExpression());
                    continue;
                }

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 根据订单账单id查询账单
                LoanOrderBillEntity loanOrderBillEntity = loanOrderBillDao.findOrderBill(orderBillId);
                if (ObjectUtils.isEmpty(loanOrderBillEntity)) {
                    continue;
                }

                // 获取订单账单还款日期
                Date repaymentTime = loanOrderBillEntity.getRepaymentTime();

                // 计算出订单还款日距离当日有几天
                int intervalDays = DateUtil.getIntervalDays(DateUtil.StringToDate(DateUtil.DateToString(repaymentTime, "yyyy-MM-dd"), "yyyy-MM-dd"), DateUtil.StringToDate(DateUtil.getDefault(), "yyyy-MM-dd"));

                // 还款组
                String repaymentDistributionGroup = "";

                // 根据订单状态判断选择策略
                if (loanOrderBillEntity.getStatus() == OrderBillStatus.WAY) {
                    // 订单在途
                    // 还款当日
                    repaymentDistributionGroup = "H:" + intervalDays;
                } else if (loanOrderBillEntity.getStatus() == OrderBillStatus.DUE) {
                    // 订单逾期
                    if (intervalDays >= 1 && intervalDays <= 7) {
                        repaymentDistributionGroup = "S:1";
                    } else {
                        repaymentDistributionGroup = "S:2";
                    }
                } else {
                    continue;
                }

                // 更新订单还款策略
                loanOrderDao.updateOrderRepaymentDistributionGroup(orderId, repaymentDistributionGroup, new Date());
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retryDistributionRepayment(distributionRepaymentParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[DistributionRepayment]", exception);
                }
                LogUtil.sysError("[DistributionRepayment]", e);
            }


        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

}
