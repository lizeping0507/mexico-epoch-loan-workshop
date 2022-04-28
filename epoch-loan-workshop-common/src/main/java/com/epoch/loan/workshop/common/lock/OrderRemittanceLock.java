package com.epoch.loan.workshop.common.lock;

import com.epoch.loan.workshop.common.zookeeper.AbstractZookeeperLock;
import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.lock
 * @className : OrderRemittanceLock
 * @createTime : 2022/4/18 23:32
 * @description : 锁
 */
@Data
public abstract class OrderRemittanceLock<String> extends AbstractZookeeperLock<String> {

    /**
     * 锁路径
     */
    private static final java.lang.String LOCK_PATH = "_order_remittance_lock_";

    /**
     * 锁id
     */
    private String lockId;

    public OrderRemittanceLock(String lockId) {
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
