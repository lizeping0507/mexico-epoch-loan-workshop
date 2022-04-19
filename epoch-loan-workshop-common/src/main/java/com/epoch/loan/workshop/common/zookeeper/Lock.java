package com.epoch.loan.workshop.common.zookeeper;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.zookeeper
 * @className : Lock
 * @createTime : 2022/4/18 23:32
 * @description : 锁
 */
@Data
public abstract class Lock<String> extends AbstractZookeeperLock<String> {

    /**
     * 锁路径
     */
    private static final java.lang.String LOCK_PATH = "_lock_";

    /**
     * 锁id
     */
    private String lockId;

    /**
     * 锁路径
     *
     * @return
     */
    @Override
    public java.lang.String getLockPath() {
        return LOCK_PATH + this.lockId;
    }


    public Lock(String lockId) {
        this.lockId = lockId;
    }

}
