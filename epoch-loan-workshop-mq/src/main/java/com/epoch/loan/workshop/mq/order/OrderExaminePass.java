package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.mysql.LoanAppConfigEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.lock.OrderExaminePassLock;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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

                // 订单id
                String orderId = orderParams.getOrderId();

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 审核模型组
                String orderModelGroup = loanOrderEntity.getOrderModelGroup();

                // 队列拦截
                if (intercept(orderModelGroup, subExpression())) {
                    // 等待重试
                    retry(orderParams, subExpression());
                    continue;
                }

                // 判断模型状态
                int status = getModelStatus(orderId, subExpression());
                if (status == OrderExamineStatus.PASS) {
                    // 发送下一模型
                    sendNextModel(orderParams, orderModelGroup, subExpression());
                    continue;
                }

                // 判断订单状态是否为废弃
                if (loanOrderEntity.getStatus() == OrderStatus.ABANDONED) {
                    continue;
                }

                // 批准金额(本金)
                double approvalAmount = loanOrderEntity.getApprovalAmount();

                /*放款金额计算*/
                // 计算扣除费用后真实放款金额
                double realAmount = loanOrderEntity.getApprovalAmount();

                /* 计算还款金额 */
                // 手续费费率
                double processingFeeProportion = loanOrderEntity.getProcessingFeeProportion();

                // 手续费
                double incidentalAmount = approvalAmount * (processingFeeProportion / 100);

                // 利率
                double interest = loanOrderEntity.getInterest();

                // 总利息费用
                double interestAmount = approvalAmount * (interest / 100);

                // 总还款金额(预计)
                Double estimatedRepaymentAmount = approvalAmount + interestAmount + incidentalAmount;

                // 生成账单(使用分布式锁)
                zookeeperClient.lock(new OrderExaminePassLock<String>(orderId) {
                    @Override
                    public String execute() throws Exception {
                        // 查询订单账单数量，如果不符合期数删除重新生成
                        boolean addOrderBill = true;
                        Integer count = loanOrderBillDao.findOrderBillCountByOrderId(orderId);
                        if (count != 0) {
                            if (!count.equals(loanOrderEntity.getStages())) {
                                loanOrderBillDao.removeOrderBillByOrderId(orderId);
                            } else {
                                addOrderBill = false;
                            }
                        }

                        // 判断是否生成账单
                        if (addOrderBill) {
                            // 每期应还款金额
                            double stagesRepaymentAmount = estimatedRepaymentAmount / loanOrderEntity.getStages();

                            // 每期应还本金
                            double stagesPrincipalAmount = approvalAmount / loanOrderEntity.getStages();

                            // 每期应还利息
                            double stagesInterestAmount = interestAmount / loanOrderEntity.getStages();

                            // 每期应还手续费
                            double stagesIncidentalAmount = incidentalAmount / loanOrderEntity.getStages();

                            // 初始化订单账单
                            for (int i = 1; i <= loanOrderEntity.getStages(); i++) {
                                LoanOrderBillEntity loanOrderBillEntity = new LoanOrderBillEntity();
                                loanOrderBillEntity.setId(ObjectIdUtil.getObjectId());
                                loanOrderBillEntity.setOrderId(loanOrderEntity.getId());
                                loanOrderBillEntity.setRepaymentAmount(stagesRepaymentAmount);
                                loanOrderBillEntity.setPrincipalAmount(stagesPrincipalAmount);
                                loanOrderBillEntity.setInterestAmount(stagesInterestAmount);
                                loanOrderBillEntity.setIncidentalAmount(stagesIncidentalAmount);
                                loanOrderBillEntity.setReductionAmount(0.00);
                                loanOrderBillEntity.setPunishmentAmount(0.00);
                                loanOrderBillEntity.setReceivedAmount(0.00);
                                loanOrderBillEntity.setStages(i);
                                loanOrderBillEntity.setCreateTime(new Date());
                                loanOrderBillEntity.setUpdateTime(new Date());
                                loanOrderBillEntity.setStatus(OrderBillStatus.CREATE);
                                loanOrderBillEntity.setType(OrderBillType.LOAN);
                                loanOrderBillDao.insertOrderBill(loanOrderBillEntity);
                            }
                        }

                        return null;
                    }
                });

                // 更新实际放款金额
                loanOrderDao.updateOrderActualAmount(orderId, realAmount, new Date());

                // 更新订单扣除费用
                loanOrderDao.updateOrderIncidentalAmount(orderId, incidentalAmount, new Date());

                // 更新还款金额
                loanOrderDao.updateOrderEstimatedRepaymentAmount(orderId, estimatedRepaymentAmount, new Date());

                // 更新通过时间
                loanOrderDao.updateOrderExaminePassTime(orderId, new Date(), new Date());

                // 修改订单状态为审核通过
                updateOrderStatus(orderId, OrderStatus.EXAMINE_PASS);

                // 修改审核状态
                updateModeExamine(orderId, subExpression(), OrderExamineStatus.PASS);

                // 加载 app 配置
                LoanAppConfigEntity loanAppConfig = loanAppConfigDao.findByAppName(loanOrderEntity.getAppName());
                LoanUserEntity loanUserEntity = loanUserDao.findById(loanOrderEntity.getUserId());
                // 保存并发送af注册打点事件
                if (StringUtils.isNotBlank(loanUserEntity.getAfId()) && ObjectUtils.isEmpty(loanAppConfig)) {
                    int[] statusArray = new int[]{OrderStatus.CREATE, OrderStatus.EXAMINE_WAIT, OrderStatus.EXAMINE_PASS, OrderStatus.EXAMINE_FAIL, OrderStatus.WAIT_PAY, OrderStatus.WAY, OrderStatus.DUE, OrderStatus.COMPLETE, OrderStatus.DUE_COMPLETE, OrderStatus.ABANDONED};
                    Integer statusIn = loanOrderDao.countUserOrderByStatusIn(loanOrderEntity.getUserId(), statusArray);
                    if (1 == statusIn) {
                        loanAfClient.sendAfEvent(AfEventField.PASSORDER, loanUserEntity.getGaId(), loanUserEntity.getAfId(), loanAppConfig.getConfig());
                    }
                }

                // 发送下一模型
                sendNextModel(orderParams, orderModelGroup, subExpression());
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
