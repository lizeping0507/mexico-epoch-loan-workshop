package com.epoch.loan.workshop.mq.collection;

import com.epoch.loan.workshop.common.constant.CollectionField;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanProductEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanProductExtEntity;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.mq.collection.reduction.CollectionReductionAmountParam;
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
import java.math.RoundingMode;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.mq.collection
 * @className : CollectionReduction
 * @createTime : 2022/07/15 17:03
 * @Description:
 */
@RefreshScope
@Component
@Data
public class CollectionReduction extends BaseCollectionMQ implements MessageListenerConcurrently {

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

                // 订单同步减免金额-在催收提环系统更新案件
                if (CollectionField.PUSH_REMIND == productExt.getReactType() || CollectionField.NO_PUSH == productExt.getReactType()) {
                    continue;
                }
                int pushRes = rePushReductionAmount(loanOrderEntity, product, productExt);
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
     * 同步减免金额
     *
     * @param orderEntity   订单类
     * @param product       产品类
     * @param productExt 产品扩展信息
     * @return 1--推送成功  0--推送失败
     */
    public Integer rePushReductionAmount(LoanOrderEntity orderEntity, LoanProductEntity product, LoanProductExtEntity productExt) {
        CollectionReductionAmountParam param = new CollectionReductionAmountParam();
        param.setOrderNo(orderEntity.getId());

        // 已还款金额
        Double actualRepaymentAmount = orderEntity.getActualRepaymentAmount();
        param.setReturnedAmount(ObjectUtils.isNotEmpty(actualRepaymentAmount) ? actualRepaymentAmount : 0.0);

        // 减免费用
        Double reductionAmount = loanOrderBillDao.sumReductionAmount(orderEntity.getId());
        param.setReductionAmount(ObjectUtils.isNotEmpty(reductionAmount) ? reductionAmount : 0.0);

        // 剩余还款金额 = 预估还款金额-实际还款金额 - 总减免金额
        Double repaymentAmount = new BigDecimal(orderEntity.getEstimatedRepaymentAmount()).subtract(new BigDecimal(actualRepaymentAmount)).subtract(new BigDecimal(reductionAmount)).setScale(2, RoundingMode.HALF_UP).doubleValue();
        param.setRemainingRepaymentAmount(repaymentAmount);

        String requestParam = getRequestParam(product, productExt,CollectionField.PUSH_SYNC_OVERDUE, param);
        return push(requestParam, CollectionField.PUSH_REACT);
    }
}
