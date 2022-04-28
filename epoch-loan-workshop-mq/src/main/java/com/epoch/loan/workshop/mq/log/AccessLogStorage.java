package com.epoch.loan.workshop.mq.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.entity.elastic.AccessLogElasticEntity;
import com.epoch.loan.workshop.common.mq.log.params.AccessLogParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import lombok.Data;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.log
 * @className : RequestLogStorage
 * @createTime : 2022/3/21 12:01
 * @description : 请求日志存储
 */
@RefreshScope
@Component
@Data
public class AccessLogStorage extends BaseLogStorageMQListener implements MessageListenerConcurrently {

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 消费任务
     *
     * @param list                       消息列表
     * @param consumeConcurrentlyContext 消息轨迹对象
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        // 循环处理消息
        for (Message msg : list) {
            // 消息对象
            AccessLogParams accessLogParams = null;
            try {
                // 获取消息对象
                accessLogParams = getMessage(msg);

                // 将日志存入队列
                AccessLogElasticEntity accessLogElasticEntity = new AccessLogElasticEntity();
                accessLogElasticEntity.setSerialNo(accessLogParams.getSerialNo());
                accessLogElasticEntity.setRequestTime(System.currentTimeMillis());
                accessLogElasticEntity.setResponseTime(System.currentTimeMillis());
                accessLogElasticEntity.setUrl(accessLogParams.getUrl());
                accessLogElasticEntity.setMappingUrl(accessLogElasticEntity.getMappingUrl());
                accessLogElasticEntity.setIp(accessLogParams.getIp());
                accessLogElasticEntity.setApplicationName(accessLogParams.getApplicationName());
                accessLogElasticEntity.setServerIp(accessLogParams.getServerIp());
                accessLogElasticEntity.setPort(accessLogParams.getPort());
                accessLogElasticEntity.setRequest(JSON.toJSON(accessLogParams.getRequest()));

                // 判断响应结果是否是JSON
                if (isjson(JSONObject.toJSONString(accessLogParams.getResponse()))) {
                    accessLogElasticEntity.setResponse(JSON.toJSON(accessLogParams.getResponse()));
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Field.MESSAGE, accessLogParams.getResponse());
                    accessLogElasticEntity.setResponse(jsonObject);
                }
                accessLogElasticEntity.setAccessSpend(accessLogParams.getAccessSpend());
                accessLogElasticEntity.setEx(accessLogParams.getEx());
                accessLogElasticDao.save(accessLogElasticEntity);
            } catch (Exception e) {
                LogUtil.sysError("[RequestLogStorage]" + JSON.toJSONString(accessLogParams), e);
            }
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }


    /**
     * 判断是否是JSON数据
     *
     * @param string
     * @return
     */
    private boolean isjson(String string) {
        try {
            JSONObject jsonStr = JSONObject.parseObject(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
