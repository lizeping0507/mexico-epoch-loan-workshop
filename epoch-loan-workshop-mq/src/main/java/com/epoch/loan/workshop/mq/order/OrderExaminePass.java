package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.LoanProductEntity;
import com.epoch.loan.workshop.common.entity.PlatformReceiveOrderApproveFeedbackEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order
 * @className : OrderExamine
 * @createTime : 2021/11/16 20:00
 * @description : 订单审核通过
 */
@RefreshScope
@Component
@Data
public class OrderExaminePass extends BaseOrderMQListener implements MessageListenerConcurrently {
    /**
     * 标签
     */
    @Value("${rocket.order.orderExaminePass.subExpression}")
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
                if (intercept(orderParams.getGroupName(), subExpression)) {
                    // 等待重试
                    retry(orderParams, subExpression);
                    continue;
                }

                // 订单id
                String orderId = orderParams.getOrderId();

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 查询产品信息
                LoanProductEntity loanProductEntity = loanProductDao.findProduct(loanOrderEntity.getProductId());

                // 计算扣除费用
                double incidentalAmount = loanOrderEntity.getApprovalAmount() * ((loanProductEntity == null ? 40 : loanProductEntity.getProcessingFeeProportion()) / 100);

                // 计算扣除费用后真实放款金额
                double realAmount = loanOrderEntity.getApprovalAmount() - incidentalAmount;

                // 更新订单扣除费用
                loanOrderDao.updateOrderIncidentalAmount(orderId, incidentalAmount, new Date());

                // 更新实际放款金额 FIXME 新老表
                loanOrderDao.updateOrderActualAmount(orderId, realAmount, new Date());
                platformOrderDao.updateOrderReceiveAmount(orderId, realAmount, new Date());

                // 初始化订单账单
                for (int i = 1; i <= loanOrderEntity.getStages(); i++) {
                    LoanOrderBillEntity loanOrderBillEntity = new LoanOrderBillEntity();
                    loanOrderBillEntity.setId(ObjectIdUtil.getObjectId());
                    loanOrderBillEntity.setOrderId(loanOrderEntity.getId());
                    loanOrderBillEntity.setStages(i);
                    loanOrderBillEntity.setCreateTime(new Date());
                    loanOrderBillEntity.setUpdateTime(new Date());
                    loanOrderBillEntity.setStatus(OrderBillStatus.CREATE);
                    loanOrderBillDao.insertOrderBillByOrderId(loanOrderBillEntity);
                }

                // 插入审批结果反馈表  FIXME 老表
                PlatformReceiveOrderApproveFeedbackEntity approveFeedbackEntity = new PlatformReceiveOrderApproveFeedbackEntity();
                approveFeedbackEntity.setOrderNo(orderId);
                approveFeedbackEntity.setApprovalAmount(BigDecimal.valueOf(loanOrderEntity.getApprovalAmount()));
                Double estimatedRepaymentAmount = loanOrderEntity.getEstimatedRepaymentAmount();
                approveFeedbackEntity.setPayAmount(estimatedRepaymentAmount == null ? null : BigDecimal.valueOf(estimatedRepaymentAmount));
                approveFeedbackEntity.setReceiveAmount(BigDecimal.valueOf(realAmount));
                approveFeedbackEntity.setConclusion(10);
                receiveOrderApproveFeedbackDao.save(approveFeedbackEntity);

                // 修改订单状态为审核通过 FIXME 新老表
                updateOrderStatus(orderId, OrderStatus.EXAMINE_PASS);
                platformOrderDao.updateOrderStatus(orderId, 115, new Date());

                // 更新通过时间 FIXME 新老表
                loanOrderDao.updateOrderExaminePassTime(orderId, new Date(), new Date());
                platformOrderDao.updateOrderApprovalTime(orderId, new Date(), new Date());

                // 修改审核状态
                updateModeExamine(orderId, subExpression, OrderExamineStatus.PASS);

                // 发送到下一模型
                sendNextModel(orderParams, subExpression);
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression);
                } catch (Exception exception) {
                    LogUtil.sysError("[OrderExaminePass]", exception);
                }

                LogUtil.sysError("[OrderExaminePass]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
