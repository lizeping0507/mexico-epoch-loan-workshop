package com.epoch.loan.workshop.common.zookeeper;

import lombok.Data;
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
@Data
public class ZookeeperClient {
    /**
     * 等待时间
     */
    private static final int SLEEP_TIME = 1000;
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
     * 连接
     */
    private CuratorFramework client;

    /**
     * 分布式锁
     *
     * @param mutex
     * @param <T>
     * @return
     */
    public <T> T lock(AbstractZookeeperLock<T> mutex) {
        String path = PATH + mutex.getLockPath();

        //创建锁对象
        InterProcessMutex lock = new InterProcessMutex(this.getClient(), path);
        boolean success = false;
        try {
            try {
                //获取锁
                success = lock.acquire(mutex.getTimeout(), mutex.getTimeUnit());
            } catch (Exception e) {
                throw new RuntimeException("obtain lock error " + e.getMessage() + ", path " + path);
            }

            if (success) {
                return (T) mutex.execute();
            } else {
                return null;
            }
        } finally {
            try {
                if (success) {
                    lock.release(); //释放锁
                }
            } catch (Exception e) {
                throw new RuntimeException("obtain lock error " + e.getMessage() + ", path " + path);
            }
        }
    }

    public void init() {
        this.client = CuratorFrameworkFactory
                .builder()
                .connectString(SERVER)
                .retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME, MAX_RETRIES))
                .build();
        this.client.start();
    }
}
