package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceAccountEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import com.epoch.loan.workshop.common.lock.OrderRemittanceLock;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
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
 * @className : OrderRemittance
 * @createTime : 2022/2/10 10:59
 * @description : 订单放款
 */
@RefreshScope
@Component
@Data
public class OrderRemittance extends BaseOrderMQListener implements MessageListenerConcurrently {
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

                // 判断订单状态是否为废弃
                if (loanOrderEntity.getStatus() == OrderStatus.ABANDONED) {
                    continue;
                }

                // 用户id
                String userId = loanOrderEntity.getUserId();

                // 查询当前模型处理状态
                int status = getModelStatus(orderId, subExpression());

                // 判断是否是创建状态
                if (status == OrderExamineStatus.CREATE) {
                    // 使用分布式锁进行新增放款
                    zookeeperClient.lock(new OrderRemittanceLock<String>(orderId) {
                        @Override
                        public String execute() throws Exception {
                            // 查询当前模型处理状态
                            int status = getModelStatus(orderId, subExpression());

                            // 账户
                            String accountId = loanOrderEntity.getBankCardId();

                            // 判断模型状态
                            if (status != OrderExamineStatus.CREATE) {
                                return null;
                            }
                            // 查询用户信息
                            LoanUserInfoEntity loanUserInfoEntity = loanUserInfoDao.findUserInfoById(userId);

                            // 查询银行卡信息
                            LoanRemittanceAccountEntity loanRemittanceAccountEntity = loanRemittanceAccountDao.findRemittanceAccount(accountId);

                            // 新增支付账单
                            String id = ObjectIdUtil.getObjectId();
                            LoanRemittanceOrderRecordEntity loanRemittanceOrderRecordEntity = new LoanRemittanceOrderRecordEntity();
                            loanRemittanceOrderRecordEntity.setId(id);
                            loanRemittanceOrderRecordEntity.setOrderId(orderId);
                            loanRemittanceOrderRecordEntity.setPaymentId("");
                            loanRemittanceOrderRecordEntity.setAmount(loanOrderEntity.getActualAmount());
                            loanRemittanceOrderRecordEntity.setName(loanUserInfoEntity.getPapersName());
                            loanRemittanceOrderRecordEntity.setRemarks(loanUserInfoEntity.getPapersName() + " remittance");
                            loanRemittanceOrderRecordEntity.setRemittanceAccount(loanRemittanceAccountEntity.getAccountNumber());
                            loanRemittanceOrderRecordEntity.setBank(loanRemittanceAccountEntity.getBank());
                            loanRemittanceOrderRecordEntity.setType(loanRemittanceAccountEntity.getType());
                            loanRemittanceOrderRecordEntity.setRfc(loanUserInfoEntity.getRfc());
                            loanRemittanceOrderRecordEntity.setCurp(loanUserInfoEntity.getPapersVoterId());
                            loanRemittanceOrderRecordEntity.setStatus(LoanRemittanceOrderRecordStatus.CREATE);
                            loanRemittanceOrderRecordEntity.setProcessRemittancePaymentRecordId("");
                            loanRemittanceOrderRecordEntity.setSuccessRemittancePaymentRecordId("");
                            loanRemittanceOrderRecordEntity.setCreateTime(new Date());
                            loanRemittanceOrderRecordEntity.setUpdateTime(new Date());
                            loanRemittanceOrderRecordDao.insert(loanRemittanceOrderRecordEntity);

                            // 更新订单状态为等待放款
                            updateOrderStatus(orderId, OrderStatus.WAIT_PAY);

                            // 发送放款请求
                            DistributionRemittanceParams distributionRemittanceParams = new DistributionRemittanceParams();
                            distributionRemittanceParams.setId(id);
                            distributionRemittanceParams.setGroupName(loanOrderEntity.getRemittanceDistributionGroup());
                            sendDistribution(distributionRemittanceParams);

                            // 更改模型审核状态为等待
                            updateModeExamine(orderId, subExpression(), OrderExamineStatus.WAIT);
                            return null;
                        }
                    });

                    // 放入队列等待放款成功
                    retry(orderParams, subExpression());
                    continue;
                }

                // 查询支付状态
                Integer loanRemittanceOrderRecordStatus = loanRemittanceOrderRecordDao.findLoanRemittanceOrderRecordStatusByOrderId(orderId);

                // 判断支付状态是否合法
                if (loanRemittanceOrderRecordStatus == null) {
                    // 更改模型审核状态为等待
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.CREATE);

                    // 放入队列等待放款成功
                    retry(orderParams, subExpression());
                    continue;
                } else if (loanRemittanceOrderRecordStatus == LoanRemittanceOrderRecordStatus.SUCCESS) {
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.PASS);

                    // 更新放款时间
                    updateLoanTime(orderId);

                    // 发送下一模型
                    sendNextModel(orderParams, subExpression());
                    continue;
                } else if (loanRemittanceOrderRecordStatus == LoanRemittanceOrderRecordStatus.THOROUGHLY_FAILED) {
                    // 异常状况 此订单放款彻底失败  流程结束
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.FAIL);

                    // 更改新表订单状态 : 废弃
                    updateOrderStatus(orderId, OrderStatus.ABANDONED);
                } else {
                    // 放入队列等待放款成功
                    retry(orderParams, subExpression());
                    continue;
                }
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression(), OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[OrderRemittance]", exception);
                }

                LogUtil.sysError("[OrderRemittance]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 标准化人名
     * 去除非字母字符 及多余空格
     *
     * @param name 名字
     * @return 标准名字
     */
    private String standardiseName(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }

        char[] chars = name.toCharArray();

        StringBuilder noSymbolStr = new StringBuilder();
        char preChar = 0;
        for (char aChar : chars) {
            // 合法字符拼接 非法字符转空格
            if ((aChar >= 65 && aChar <= 90) || (aChar >= 97 && aChar <= 122)) {
                noSymbolStr.append(aChar);
            } else {
                aChar = 32;
            }

            // 单独空格拼接 连续空格去除
            if (aChar == 32 && preChar != 32) {
                noSymbolStr.append(aChar);
            }
            preChar = aChar;
        }

        // 头尾空格去除
        return noSymbolStr.toString().trim();
    }
}
