package com.epoch.loan.workshop.timing.task;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.mq.DelayMQParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.timing.task
 * @className : MqDelayQueueTask
 * @createTime : 2021/11/16 18:09
 * @description : 延时队列
 */
@DisallowConcurrentExecution
@Component
public class MqDelayQueueTask extends BaseTask implements Job {

    /**
     * 定时任务方法
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        /*定时*/
        Set<String> stringSet = redisClient.getValues(RedisKeyField.MQ_DELAY);

        // 使用LinkedHashSet去重消息(重启MQ时会又重复数据)
        Map<String, DelayMQParams> repeat = new ConcurrentHashMap<>();

        // 去重数据
        stringSet.parallelStream().forEach(s -> {
            try {
                Object json = redisClient.get(s);
                DelayMQParams delayMQParams = JSONObject.parseObject((String) json, DelayMQParams.class);
                boolean isHas = redisClient.hasKey(RedisKeyField.MQ_DELAY_INDEX + delayMQParams.getId());
                if (isHas) {
                    return;
                }

                // 去重集合
                JSONObject repeatJson = JSONObject.parseObject((String) json);
                repeatJson.remove("id");
                String repeatStr = repeatJson.toJSONString();
                if (ObjectUtils.isEmpty(repeat.get(repeatStr))) {
                    repeat.put(repeatJson.toJSONString(), delayMQParams);
                    return;
                }

                // 删除重复消息
                redisClient.del(RedisKeyField.MQ_DELAY + delayMQParams.getId());
            } catch (Exception e) {
                LogUtil.sysError("[MqDelayQueueTask]", e);
            }
        });

        // 发送去重后的消息
        for (Map.Entry<String, DelayMQParams> entry : repeat.entrySet()) {
            try {
                // 消息
                DelayMQParams delayMQParams = entry.getValue();

                // 判断队列名称
                if (delayMQParams.getTopic().startsWith("ORDER")) {
                    orderMQManager.sendMessage(delayMQParams.getParams(), delayMQParams.getSubExpression());
                } else if (delayMQParams.getTopic().startsWith("REMITTANCE")) {
                    remittanceMQManager.sendMessage(delayMQParams.getParams(), delayMQParams.getSubExpression());
                } else if (delayMQParams.getTopic().startsWith("LOG")) {
                    logMQManager.sendMessage(delayMQParams.getParams(), delayMQParams.getSubExpression());
                } else if (delayMQParams.getTopic().startsWith("REPAYMENT")) {
                    repaymentMQManager.sendMessage(delayMQParams.getParams(), delayMQParams.getSubExpression());
                } else if (delayMQParams.getTopic().startsWith("COLLECTION")) {
                    collectionMQManager.sendMessage(delayMQParams.getParams(), delayMQParams.getSubExpression());
                }

                // 删除键
                redisClient.del(RedisKeyField.MQ_DELAY + delayMQParams.getId());
            } catch (Exception e) {
                LogUtil.sysError("[MqDelayQueueTask]", e);
            }
        }
    }
}
