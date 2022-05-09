package com.epoch.loan.workshop.timing.task;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.timing.task
 * @className : CollectionPushTask
 * @createTime : 2022/04/29 16:43
 * @Description: 放款推送催收提还接口
 */
@DisallowConcurrentExecution
@Component
public class CollectionPushTask extends BaseTask implements Job {

    /**
     * 方法主体
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LogUtil.sysInfo("开始执行催收、提还任务，context: {} " , context);
        String params = this.getParams(context);
        if (StringUtils.isNotBlank(params)) {
            // 发送下一个模型
            CollectionParams collectionParams = JSONObject.parseObject(params, CollectionParams.class);
            try {
                collectionMQManager.sendMessage(collectionParams, "Collection");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
