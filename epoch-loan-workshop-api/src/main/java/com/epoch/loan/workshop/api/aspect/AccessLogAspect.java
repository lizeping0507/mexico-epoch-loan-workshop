package com.epoch.loan.workshop.api.aspect;

import com.epoch.loan.workshop.common.mq.log.LogMQManager;
import com.epoch.loan.workshop.common.mq.log.params.AccessLogParams;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.aspect
 * @className : AccessLogAspect
 * @createTime : 2022/3/29 16:41
 * @description : 响应日志切面
 */
@Aspect
@Component
public class AccessLogAspect {

    /**
     * 日志队列
     */
    @Autowired
    private LogMQManager logMQManager;

    /**
     * 定义切入点
     */
    @Pointcut("execution(* com.epoch.loan.workshop.common.util.LogUtil.requestLog(..))")
    public void pointCutMethod() {
    }

    @Around("pointCutMethod()")
    public void around(ProceedingJoinPoint point) throws Throwable {
        // 获取调用参数
        Object[] args = point.getArgs();

        // 调用
        point.proceed(args);

        // 加入队列
        AccessLogParams accessLogParams = (AccessLogParams) args[0];
        logMQManager.sendMessage(accessLogParams, logMQManager.getRequestLogStorageSubExpression());
    }
}
