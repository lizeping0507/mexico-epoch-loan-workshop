package com.epoch.loan.workshop.order.service;

import com.epoch.loan.workshop.common.config.PlatformConfig;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.zookeeper.ZookeeperClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.order.service;
 * @className : BaseService
 * @createTime : 2022/3/22 14:12
 * @description : Order业务基类
 */
public class BaseService {

    /**
     * 产品
     */
    @Autowired
    public LoanProductDao loanProductDao;

    /**
     * 订单
     */
    @Autowired
    public LoanOrderDao loanOrderDao;

    /**
     * 订单账单
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;

    /**
     * 放款账户
     */
    @Autowired
    public LoanRemittanceAccountDao loanRemittanceAccountDao;

    /**
     * 贷超相关配置
     */
    @Autowired
    public PlatformConfig platformConfig;

    /**
     * Zookeeper链接工具累
     */
    @Autowired
    public ZookeeperClient zookeeperClient;

    /**
     * 订单模型
     */
    @Autowired
    public LoanOrderModelDao orderModelDao;

    /**
     * 订单队列
     */
    @Autowired
    public OrderMQManager orderMQManager;

    /**
     * 还款记录
     */
    @Autowired
    public LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;

    /**
     * 用户放款卡
     */
    @Autowired
    public LoanUserBankCardDao loanUserBankCardDao;

}
