package com.epoch.loan.workshop.mq.collection;

import com.epoch.loan.workshop.common.mq.collection.CollectionMQManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.repayment
 * @className : RepaymentConsumerRegister
 * @createTime : 2021/11/3 15:00
 * @description : 订单队列相关消费者初始化
 */
@Component
public class CollectionConsumerRegister {

    /**
     * 推送催收、提还 推送订单队列
     */
    @Autowired
    public CollectionOrder collectionOrder;

    /**
     * 推送催收、提还  同步逾期队列
     */
    @Autowired
    public CollectionOverdue collectionOverdue;

    /**
     * 推送催收、提还 减免金额队列
     */
    @Autowired
    public CollectionReduction collectionReduction;

    /**
     * 推送催收、提还 推送还款队列
     */
    @Autowired
    public CollectionRepay collectionRepay;

    /**
     * 催收、提还队列管理
     */
    @Autowired
    private CollectionMQManager collectionMQManager;

    /**
     * 初始化
     */
    public void init() throws Exception {
        collectionMQManager.init();
    }

    /**
     * 启动
     */
    public void start() throws Exception {
        collectionOrder.start();
        collectionOverdue.start();
        collectionReduction.start();
        collectionRepay.start();
    }
}
