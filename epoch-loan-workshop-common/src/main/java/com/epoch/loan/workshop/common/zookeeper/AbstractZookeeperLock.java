package com.epoch.loan.workshop.common.zookeeper;

import java.util.concurrent.TimeUnit;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.zookeeper
 * @className : AbstractZookeeperLock
 * @createTime : 2022/4/18 23:09
 * @description : AbstractZookeeperLock 锁
 */
public abstract class AbstractZookeeperLock<T> {
    /**
     * 锁获取最大时间
     */
    private static final int TIME_OUT = 10;

    /**
     * 锁路径
     *
     * @return
     */
    public abstract String getLockPath();

    /**
     * 执行方法
     *
     * @return
     */
    public abstract T execute() throws Exception;

    public int getTimeout() {
        return TIME_OUT;
    }

    /**
     * 时间单位
     *
     * @return
     */
    public TimeUnit getTimeUnit() {
        return TimeUnit.SECONDS;
    }
}
