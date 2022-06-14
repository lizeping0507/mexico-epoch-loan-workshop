package com.epoch.loan.workshop.common.lock;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.lock
 * @className : UserProductDetailLock
 * @createTime : 2022/4/22 19:17
 * @description : 用户产品
 */
public abstract class UserProductDetailLock<String> extends AbstractZookeeperLock<String> {
    /**
     * 锁路径
     */
    private static final java.lang.String LOCK_PATH = "_user_product_detail_lock_";

    /**
     * 锁id
     */
    private String lockId;

    public UserProductDetailLock(String lockId) {
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
