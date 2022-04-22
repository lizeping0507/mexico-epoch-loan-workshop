package com.epoch.loan.workshop.common.lock;

import com.epoch.loan.workshop.common.zookeeper.AbstractZookeeperLock;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.lock
 * @className : UserApplyDetailLock
 * @createTime : 2022/4/22 19:17
 * @description : 用户申请
 */
public abstract class UserApplyDetailLock<String> extends AbstractZookeeperLock<String> {
    /**
     * 锁路径
     */
    private static final java.lang.String LOCK_PATH = "_user_apply_lock_";

    /**
     * 锁id
     */
    private String lockId;

    public UserApplyDetailLock(String lockId) {
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
