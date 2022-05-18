package com.epoch.loan.workshop.timing.task;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.mq.DelayMQParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 处理队列延时消息
 */
@DisallowConcurrentExecution
@Component
public class MqDelayQueueTask extends BaseTask implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        /*定时*/
        Set<String> stringSet = redisClient.getValues(RedisKeyField.MQ_DELAY);

        stringSet.parallelStream().forEach(s -> {
            try {
                Object json = redisClient.get(s);
                DelayMQParams delayMQParamss = JSONObject.parseObject((String) json, DelayMQParams.class);
                boolean isHas = redisClient.hasKey(RedisKeyField.MQ_DELAY_INDEX + delayMQParamss.getId());
                if (isHas) {
                    return;
                }

                if (delayMQParamss.getTopic().startsWith("ORDER")) {
                    orderMQManager.sendMessage(delayMQParamss.getParams(), delayMQParamss.getSubExpression());
                } else if (delayMQParamss.getTopic().startsWith("REMITTANCE")) {
                    remittanceMQManager.sendMessage(delayMQParamss.getParams(), delayMQParamss.getSubExpression());
                } else if (delayMQParamss.getTopic().startsWith("LOG")) {
                    logMQManager.sendMessage(delayMQParamss.getParams(), delayMQParamss.getSubExpression());
                }else if (delayMQParamss.getTopic().startsWith("REPAYMENT")) {
                    repaymentMQManager.sendMessage(delayMQParamss.getParams(), delayMQParamss.getSubExpression());
                }else if (delayMQParamss.getTopic().startsWith("COLLECTION")) {
                    collectionMQManager.sendMessage(delayMQParamss.getParams(), delayMQParamss.getSubExpression());
                }

                redisClient.del(RedisKeyField.MQ_DELAY + delayMQParamss.getId());
            } catch (Exception e) {
                LogUtil.sysError("[MqDelayQueueTask]", e);
            }
        });
    }
}
