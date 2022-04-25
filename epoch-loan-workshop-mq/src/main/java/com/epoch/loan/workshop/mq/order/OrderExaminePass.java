package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.OrderBillType;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.PlatformReceiveOrderApproveFeedbackEntity;
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

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 批准金额(本金)
                double approvalAmount = loanOrderEntity.getApprovalAmount();

                /*放款金额计算*/
                // 手续费费率
                double processingFeeProportion = loanOrderEntity.getProcessingFeeProportion();

                // 扣除费用
                double incidentalAmount = approvalAmount * (processingFeeProportion / 100);

                // 计算扣除费用后真实放款金额
                double realAmount = loanOrderEntity.getApprovalAmount();

                // 更新订单扣除费用
                loanOrderDao.updateOrderIncidentalAmount(orderId, incidentalAmount, new Date());

                // 更新实际放款金额
                loanOrderDao.updateOrderActualAmount(orderId, realAmount, new Date());

                /* 计算还款金额 */
                // 利率
                double interest = loanOrderEntity.getInterest();

                // 总利息费用
                double interestAmount = approvalAmount * (interest / 100);

                // 总还款金额(预计)
                Double estimatedRepaymentAmount = approvalAmount + interestAmount;

                // 每期应还款金额
                double stagesRepaymentAmount = estimatedRepaymentAmount / loanOrderEntity.getStages();

                // 每期应还本金
                double stagesPrincipalAmount = approvalAmount / loanOrderEntity.getStages();

                // 每期应还利息
                double stagesInterestAmount = interestAmount / loanOrderEntity.getStages();

                // 初始化订单账单
                for (int i = 1; i <= loanOrderEntity.getStages(); i++) {
                    LoanOrderBillEntity loanOrderBillEntity = new LoanOrderBillEntity();
                    loanOrderBillEntity.setId(ObjectIdUtil.getObjectId());
                    loanOrderBillEntity.setOrderId(loanOrderEntity.getId());
                    loanOrderBillEntity.setRepaymentAmount(stagesRepaymentAmount);
                    loanOrderBillEntity.setPrincipalAmount(stagesPrincipalAmount);
                    loanOrderBillEntity.setInterestAmount(stagesInterestAmount);
                    loanOrderBillEntity.setReductionAmount(0.00);
                    loanOrderBillEntity.setPunishmentAmount(0.00);
                    loanOrderBillEntity.setIncidentalAmount(0.00);
                    loanOrderBillEntity.setReceivedAmount(0.00);
                    loanOrderBillEntity.setStages(i);
                    loanOrderBillEntity.setCreateTime(new Date());
                    loanOrderBillEntity.setUpdateTime(new Date());
                    loanOrderBillEntity.setStatus(OrderBillStatus.CREATE);
                    loanOrderBillEntity.setType(OrderBillType.LOAN);
                    loanOrderBillDao.insertOrderBill(loanOrderBillEntity);
                }

                // 更新还款金额
                loanOrderDao.updateOrderEstimatedRepaymentAmount(orderId, estimatedRepaymentAmount, new Date());

                // 插入审批结果反馈表  FIXME 老表
                PlatformReceiveOrderApproveFeedbackEntity approveFeedbackEntity = new PlatformReceiveOrderApproveFeedbackEntity();
                approveFeedbackEntity.setOrderNo(orderId);
                approveFeedbackEntity.setApprovalAmount(BigDecimal.valueOf(loanOrderEntity.getApprovalAmount()));
                Double orderEstimatedRepaymentAmount = loanOrderEntity.getEstimatedRepaymentAmount();
                approveFeedbackEntity.setPayAmount(orderEstimatedRepaymentAmount == null ? null : BigDecimal.valueOf(orderEstimatedRepaymentAmount));
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
                updateModeExamine(orderId, subExpression(), OrderExamineStatus.PASS);

                // 发送到下一模型
                sendNextModel(orderParams, subExpression());
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression(), OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[OrderExaminePass]", exception);
                }

                LogUtil.sysError("[OrderExaminePass]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
