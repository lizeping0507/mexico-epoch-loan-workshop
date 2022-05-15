package com.epoch.loan.workshop.common.zookeeper.lock;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.lock
 * @className : DistributionRemittanceLock
 * @createTime : 2022/4/18 23:32
 * @description : 锁
 */
@Data
public abstract class DistributionRemittanceLock<String> extends AbstractZookeeperLock<String> {

    /**
     * 锁路径
     */
    private static final java.lang.String LOCK_PATH = "_distribution_remittance_lock_";

    /**
     * 锁id
     */
    private String lockId;

    public DistributionRemittanceLock(String lockId) {
        this.lockId = lockId;
    }

    /**
     * 锁路径
     *
     * @return
     */
    @Override
    public java.lang.String getLockPath() {
        return LOCK_PATH + this.lockId;
    }

}
