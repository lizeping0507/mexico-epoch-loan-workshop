package com.epoch.loan.workshop.mq.collection;

import com.epoch.loan.workshop.common.constant.CollectionField;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanProductEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanProductExtEntity;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.mq.collection.overdue.CollectionOverdueParam;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.mq.collection
 * @className : CollectionOverdue
 * @createTime : 2022/07/15 16:48
 * @Description:
 */
@RefreshScope
@Component
@Data
public class CollectionOverdue extends BaseCollectionMQ implements MessageListenerConcurrently {

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

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
            CollectionParams collectionParams = null;

            try {
                // 获取消息对象
                collectionParams = getMessage(msg);
                if (ObjectUtils.isEmpty(collectionParams)) {
                    continue;
                }

                // 订单id
                String orderId = collectionParams.getOrderId();

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 根据订单账单id查询账单
                LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderId);
                if (ObjectUtils.isEmpty(lastOrderBill)) {
                    continue;
                }

                // 产品过滤
                LoanProductEntity product = loanProductDao.findProduct(loanOrderEntity.getProductId());
                if (ObjectUtils.isEmpty(product)) {
                    continue;
                }

                // 产品过滤
                LoanProductExtEntity productExt= loanProductExtDao.findByProductId(loanOrderEntity.getProductId());
                if (ObjectUtils.isEmpty(productExt) || ObjectUtils.isEmpty(productExt.getReactType())
                        || CollectionField.NO_PUSH == productExt.getReactType()) {
                    continue;
                }

                // 订单逾期-在催收提环系统更新案件
                if (CollectionField.PUSH_REMIND == productExt.getReactType() || CollectionField.NO_PUSH == productExt.getReactType()) {
                    continue;
                }

                int pushRes = rePushOverdue(loanOrderEntity, product, lastOrderBill,productExt);
                if (CollectionField.PUSH_SUCCESS != pushRes) {
                    retryCollection(collectionParams, subExpression());
                }
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retryCollection(collectionParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[Collection]", exception);
                }
                LogUtil.sysError("[Collection]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 同步逾期费用
     *
     * @param orderEntity   订单类
     * @param product       产品类
     * @param lastOrderBill 最后一期订单账单
     * @param productExt 产品扩展信息
     * @return 1--推送成功  0--推送失败
     */
    public Integer rePushOverdue(LoanOrderEntity orderEntity, LoanProductEntity product, LoanOrderBillEntity lastOrderBill,LoanProductExtEntity productExt) {
        CollectionOverdueParam param = new CollectionOverdueParam();
        param.setOrderNo(orderEntity.getId());

        // 总利息
        Double interestAmount = loanOrderBillDao.sumOrderInterestAmount(orderEntity.getId());
        param.setInterest(interestAmount);

        // 总罚息
        Double punishmentAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        param.setPenaltyAmount(punishmentAmount);

        // 逾期天数
        int intervalDays = DateUtil.getIntervalDays(DateUtil.getStartForDay(lastOrderBill.getActualRepaymentTime()), DateUtil.getStartForDay(lastOrderBill.getRepaymentTime()));
        param.setPenaltyDay(intervalDays);

        param.setUserType(orderEntity.getUserType());
        param.setFundType(1);

        String requestParam = getRequestParam(product, productExt,CollectionField.PUSH_SYNC_OVERDUE, param);
        return push(requestParam, CollectionField.PUSH_REACT);
    }
}
