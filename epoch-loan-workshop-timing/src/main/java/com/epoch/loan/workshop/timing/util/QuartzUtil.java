package com.epoch.loan.workshop.timing.util;

import com.epoch.loan.workshop.common.dao.LoanTimingDao;
import com.epoch.loan.workshop.common.entity.LoanTimingEntity;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zeping.li
 * @version 1.0
 * @className QuartzUtil
 * @package com.epoch.remind.config
 * @description 定时任务工具类
 * @since 2020/11/20 17:28
 */
@Component
public class QuartzUtil {
    /**
     * 定时任务
     */
    @Autowired
    public LoanTimingDao timingDao;
    /**
     * 定时任务
     */
    @Autowired
    private Scheduler scheduler;

    /**
     * 项目启动初始化所有定时任务
     *
     * @throws SchedulerException
     */
    public void startJob() throws SchedulerException {
        startJob(scheduler);
    }

    /**
     * 启动定时任务
     *
     * @param scheduler
     * @throws SchedulerException
     */
    private void startJob(Scheduler scheduler) throws SchedulerException {
        // 查询 定时任务列表
        List<LoanTimingEntity> timingEntityList = timingDao.findTiming();

        // 手动指定任务列表 用于本地单独测试某一定时任务 (不要提交)
        /*timingEntityList = new ArrayList<>();
        TimingEntity timing = new TimingEntity();
        timing.setClassPath("com.epoch.pf.option.timing.timing.DingTalkHalfHourStatistsTask");
        timing.setName("DingTalkHalfHourStatistsTask");
        timing.setLastRunTime(new Date());
        timing.setUpdateTime(new Date());
        timing.setStatus(1);
        timing.setTime("0 0/1 * * * ?");
        timingEntityList.add(timing);*/

        // 本地数据
        timingEntityList.forEach(timingEntity -> {
            try {
                // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
                // JobDetail 是具体Job实例
                Class jobClass = Class.forName(timingEntity.getClassPath());
                JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(timingEntity.getName(), "epoch").build();

                // 基于表达式构建触发器
                CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(timingEntity.getTime());
                // CronTrigger表达式触发器 继承于Trigger
                // TriggerBuilder 用于构建触发器实例
                CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(timingEntity.getName(), "epoch").withSchedule(cronScheduleBuilder).build();

                // 派发定时任务
                scheduler.scheduleJob(jobDetail, cronTrigger);
            } catch (Exception e) {
                LogUtil.sysError("[QuartzUtil]", e);
            }
        });

        // 启动所有定时任务
        scheduler.start();
    }

    /**
     * 删除任务
     *
     * @param jobName
     */
    public void removeJob(String jobName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, "epoch");
        try {
            if (checkExists(jobName, "epoch")) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查看任务是否存在
     *
     * @param jobName
     * @param groupName
     * @return
     * @throws SchedulerException
     */
    public boolean checkExists(String jobName, String groupName) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, groupName);
        return scheduler.checkExists(triggerKey);
    }

    /**
     * 更新任务
     *
     * @param classPath
     * @param jobName
     * @param time
     */
    public void updateJob(String classPath, String jobName, String time) {
        try {
            // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
            // JobDetail 是具体Job实例
            Class jobClass = Class.forName(classPath);
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, "epoch").build();

            // 基于表达式构建触发器
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(time);
            Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, "epoch").withSchedule(cronScheduleBuilder).build();
            removeJob(jobName);
            scheduler.scheduleJob(jobDetail, newTrigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
