package com.epoch.loan.workshop.mq.collection;

import com.epoch.loan.workshop.common.constant.CollectionField;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.mq.collection.repay.CollectionOrderInfoParam;
import com.epoch.loan.workshop.mq.collection.repay.CollectionRepayParam;
import com.epoch.loan.workshop.mq.collection.repay.CollectionRepayRecordParam;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.mq.collection
 * @className : CollectionRepay
 * @createTime : 2022/07/15 17:07
 * @Description:
 */
@RefreshScope
@Component
@Data
public class CollectionRepay extends BaseCollectionMQ implements MessageListenerConcurrently {

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

                // 订单还款-在催收提环系统更新案件
                int pushRes = pushRepay(loanOrderEntity, product, lastOrderBill,productExt);
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
     * 还款推送
     *
     * @param orderEntity   订单实体类
     * @param product       产品类
     * @param lastOrderBill 最后一期订单账单
     * @param productExt 产品扩展信息
     * @return 1--推送成功  0--推送失败
     */
    public Integer pushRepay(LoanOrderEntity orderEntity, LoanProductEntity product, LoanOrderBillEntity lastOrderBill,LoanProductExtEntity productExt) {
        CollectionRepayParam repayParam = new CollectionRepayParam();

        // 订单信息填充
        CollectionOrderInfoParam orderInfo = new CollectionOrderInfoParam();
        orderInfo.setOrderNo(orderEntity.getId());
        orderInfo.setOrderStatus((orderEntity.getStatus() == OrderStatus.COMPLETE || orderEntity.getStatus() == OrderStatus.DUE_COMPLETE) ? 1 : 0);
        orderInfo.setThirdUserId(orderEntity.getUserId());
        orderInfo.setPenaltyDays(DateUtil.getIntervalDays(DateUtil.getStartForDay(lastOrderBill.getActualRepaymentTime()), DateUtil.getStartForDay(lastOrderBill.getRepaymentTime())));
        Double penaltyAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        orderInfo.setPenaltyAmount(penaltyAmount);
        orderInfo.setSettledTime(lastOrderBill.getActualRepaymentTime());
        orderInfo.setUserType(orderEntity.getUserType());

        repayParam.setOrder(orderInfo);
        repayParam.setRepaymentPlan(getRepaymentPlan(orderEntity));

        // 封装还款信息
        List<CollectionRepayRecordParam> repayRecord = new ArrayList<>();
        List<LoanRepaymentPaymentRecordEntity> successRecordList = loanRepaymentPaymentRecordDao.findListRecordDTOByOrderIdAndStatus(orderEntity.getId(), LoanRepaymentPaymentRecordStatus.SUCCESS);
        if (CollectionUtils.isNotEmpty(successRecordList)) {
            successRecordList.forEach(successRecord -> {
                CollectionRepayRecordParam collectionRepayRecordParam = new CollectionRepayRecordParam();
                collectionRepayRecordParam.setOrderNo(orderEntity.getId());
                collectionRepayRecordParam.setThirdRepayRecordId(successRecord.getId());
                collectionRepayRecordParam.setRepayAmount(successRecord.getAmount());
                collectionRepayRecordParam.setReturnedBillAmount(successRecord.getAmount());
                collectionRepayRecordParam.setReturnedPentaltyAmount(orderEntity.getPenaltyInterest());
                collectionRepayRecordParam.setFundType(1);
                collectionRepayRecordParam.setRepayTime(successRecord.getUpdateTime());
                Double reductionAmount = loanOrderBillDao.sumReductionAmount(orderEntity.getId());
                if (ObjectUtils.isNotEmpty(reductionAmount) && new BigDecimal(reductionAmount).compareTo(BigDecimal.ZERO) > 0) {
                    collectionRepayRecordParam.setRepayType(2);
                } else {
                    collectionRepayRecordParam.setRepayType(1);
                }
                collectionRepayRecordParam.setPayStatus(3);

                repayRecord.add(collectionRepayRecordParam);
            });
        }
        repayParam.setRepayRecord(repayRecord);
        String requestParam = getRequestParam(product,productExt, CollectionField.PUSH_REPAY, repayParam);

        return push(requestParam, productExt.getReactType());
    }
}
