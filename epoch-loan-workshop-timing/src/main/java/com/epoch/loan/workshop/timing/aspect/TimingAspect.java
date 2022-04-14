package com.epoch.loan.workshop.timing.aspect;


import com.alibaba.fastjson.JSONObject;

import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.dao.LoanTimingDao;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author zeping.li
 * @version 1.0
 * @className TimingAspect
 * @package com.epoch.remind.aspect
 * @description 定时任务拦截及监控
 * @since 2020/11/23 15:10
 */
@Aspect
@Component
public class TimingAspect {
    /**
     * 定时任务
     */
    @Autowired
    public LoanTimingDao timingDao;

    /**
     * 定义切入点
     */
    @Pointcut("execution(* com.epoch.loan.workshop.timing.task.*Task.execute(..))")
    public void pointCutMethod() {
    }

    @Around("pointCutMethod()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取定时任务参数
        JobExecutionContext jobExecutionContext = (JobExecutionContext) point.getArgs()[0];

        // 开始时间
        Long startTime = System.currentTimeMillis();

        // 定时任务开始执行
        Object result = point.proceed(point.getArgs());

        // 开始时间
        Long endTime = System.currentTimeMillis();

        // 封装日志打印
        JSONObject log = new JSONObject();
        log.put(Field.START_TIME, DateUtil.getString(new Date(startTime)));
        log.put(Field.END_TIME, DateUtil.getString(new Date(endTime)));
        log.put(Field.SPEND, endTime - startTime);
        log.put(Field.NAME, jobExecutionContext.getJobDetail().getKey().getName());

        // 打印日志
        LogUtil.daemonInfo(log.toJSONString());

        // 更新定时任务最后执行时间
        timingDao.updateTimingLastRunTime(jobExecutionContext.getJobDetail().getKey().getName(), new Date());

        return result;
    }
}
