package com.epoch.loan.workshop.common.zookeeper;

import com.epoch.loan.workshop.common.zookeeper.lock.AbstractZookeeperLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.zookeeper
 * @className : ZookeeperClient
 * @createTime : 2022/4/18 23:11
 * @description : Zookeeper 链接工具类
 */
@RefreshScope
@Component
public class ZookeeperClient {
    /**
     * 等待时间
     */
    private static final int SLEEP_TIME = 10000;
    /**
     * 最大次数
     */
    private static final int MAX_RETRIES = 3;
    /**
     * 服务地址
     */
    @Value("${zookeeper.server}")
    private String SERVER;
    /**
     * 锁地址
     */
    @Value("${zookeeper.path}")
    private String PATH;

    /**
     * zookeeper 链接
     */
    private static CuratorFramework client;

    /**
     * 初始化Zookeeper
     */
    protected void init() {
        client = CuratorFrameworkFactory.builder().connectString(SERVER).retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME, MAX_RETRIES)).build();
        client.start();
    }

    /**
     * 分布式锁
     *
     * @param mutex
     * @param <T>
     * @return
     */
    public <T> T lock(AbstractZookeeperLock<T> mutex) throws Exception {
        // 懒加载
        if (client == null) {
            init();
        }

        // 锁路径
        String path = PATH + mutex.getLockPath();

        // 获取锁
        InterProcessMutex lock = new InterProcessMutex(client, path);

        // 是否获取锁标识
        boolean success = false;
        try {
            //获取锁
            success = lock.acquire(mutex.getTimeout(), mutex.getTimeUnit());

            // 判断是否获取到锁
            if (success) {
                return (T) mutex.execute();
            } else {
                // 没用获取到锁返回空
                return null;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (success) {
                    //释放锁
                    lock.release();
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }


}
