package com.epoch.loan.workshop.mq.remittance;

import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.constant.LoanRemittancePaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PayStrategy;
import com.epoch.loan.workshop.common.entity.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.LoanProductRemittanceConfigEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittanceDistributionEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionParams;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : Duke
 * @packageName : m.epoch.loan.workshop.mq.remittance
 * @className : Distribution
 * @createTime : 2021/12/16 11:00
 * @description : 支付分配
 */
@RefreshScope
@Component
@Data
public class Distribution extends BaseRemittanceMQListener implements MessageListenerConcurrently {
    /**
     * 放款队列标签标签
     */
    @Value("${rocket.remittance.distribution.subExpression}")
    protected String subExpression;

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
            DistributionParams distributionParams = null;

            try {
                // 获取消息对象
                distributionParams = getMessage(msg, DistributionParams.class);
                if (ObjectUtils.isEmpty(distributionParams)) {
                    continue;
                }

                // 查询支付分配列表
                List<LoanRemittanceDistributionEntity> loanRemittanceDistributions = loanRemittanceDistributionDao.findRemittanceDistribution(distributionParams.getGroupName());

                // 去除已过滤列表
                List<String> paymentFilter = distributionParams.getPaymentFilter();
                if (CollectionUtils.isNotEmpty(paymentFilter)) {
                    for (int i = loanRemittanceDistributions.size() - 1; i >= 0; i--) {
                        if (paymentFilter.contains(loanRemittanceDistributions.get(i).getPaymentId())) {
                            loanRemittanceDistributions.remove(i);
                        }
                    }
                } else {
                    paymentFilter = new ArrayList<>();
                }

                // 备选渠道判空
                if (CollectionUtils.isEmpty(loanRemittanceDistributions)) {
                    // 修改状态
                    updateRemittanceOrderRecordStatus(distributionParams.getId(), LoanRemittanceOrderRecordStatus.FAILED);
                    continue;
                }

                // 查询挑选策略
                LoanProductRemittanceConfigEntity config = loanProductRemittanceConfigDao.findByGroupName(distributionParams.getGroupName());

                // 根据策略挑选渠道
                LoanRemittanceDistributionEntity selectedRemittanceDistribution = null;
                if (config.getStrategyName().equals(PayStrategy.WEIGHT)) {
                    // 根据权重策略选择渠道
                    selectedRemittanceDistribution = chooseByWeight(loanRemittanceDistributions);
                    if (ObjectUtils.isEmpty(selectedRemittanceDistribution)) {
                        // 修改状态
                        updateRemittanceOrderRecordStatus(distributionParams.getId(), LoanRemittanceOrderRecordStatus.FAILED);
                        continue;
                    }

                    // 支付渠道校验
                    String paymentId = selectedRemittanceDistribution.getPaymentId();
                    LoanPaymentEntity loanPayment = loanPaymentDao.getById(paymentId);
                    if (ObjectUtils.isEmpty(loanPayment) || loanPayment.getStatus() != 1) {
                        // 支付渠道无效标记过滤，并重回分配队列
                        paymentFilter.add(paymentId);
                        retryDistribution(distributionParams, subExpression);
                        continue;
                    }

                    // 创建订单详情记录
                    LoanRemittancePaymentRecordEntity loanRemittancePaymentRecordEntity = new LoanRemittancePaymentRecordEntity();
                    String paymentLogId = ObjectIdUtil.getObjectId();
                    loanRemittancePaymentRecordEntity.setId(paymentLogId);
                    loanRemittancePaymentRecordEntity.setStatus(LoanRemittancePaymentRecordStatus.CREATE);
                    loanRemittancePaymentRecordEntity.setPaymentId(paymentId);
                    loanRemittancePaymentRecordEntity.setRemittanceOrderRecordId(distributionParams.getId());
                    loanRemittancePaymentRecordEntity.setCreateTime(new Date());
                    loanRemittancePaymentRecordEntity.setUpdateTime(new Date());
                    loanRemittancePaymentRecordDao.insert(loanRemittancePaymentRecordEntity);

                    // 更新渠道
                    updateRemittanceOrderRecordPayment(distributionParams.getId(), paymentId);

                    // 进行中订单详情Id
                    updateProcessRemittancePaymentRecordId(distributionParams.getId(), paymentLogId);

                    // 进入放款队列
                    RemittanceParams params = new RemittanceParams();
                    params.setId(paymentLogId);
                    params.setGroupName(distributionParams.getGroupName());
                    params.setPaymentFilter(distributionParams.getPaymentFilter());
                    sendToRemittance(params, loanPayment.getName());

                } else {
                    // 修改状态
                    updateRemittanceOrderRecordStatus(distributionParams.getId(), LoanRemittanceOrderRecordStatus.EXCEPTION);

                    // 重回分配队列
                    retryDistribution(distributionParams, subExpression);
                    continue;
                }
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retryDistribution(distributionParams, subExpression);
                } catch (Exception exception) {
                    LogUtil.sysError("[Distribution]", exception);
                }

                LogUtil.sysError("[Distribution]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 根据权重策略挑选渠道
     *
     * @param list 备选渠道列表
     */
    private LoanRemittanceDistributionEntity chooseByWeight(List<LoanRemittanceDistributionEntity> list) {
        LoanRemittanceDistributionEntity res = null;

        // 查询渠道列表
        // 随机范围 = 渠道权重和
        int range = list.stream().mapToInt(LoanRemittanceDistributionEntity::getProportion).sum();
        if (range == 0) {
            return null;
        }

        // 取随机数
        Random random = new Random();
        int randomNum = random.nextInt(range) + 1;

        // 选择渠道
        int start = 0;
        for (LoanRemittanceDistributionEntity entity : list) {
            Integer proportion = entity.getProportion();
            if (randomNum > start && randomNum <= (start + proportion)) {
                res = entity;
                break;
            } else {
                start += proportion;
            }
        }
        return res;
    }
}