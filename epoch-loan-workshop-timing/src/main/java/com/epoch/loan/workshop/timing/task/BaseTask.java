package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.dao.*;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author : Shangkunfeng
 * @packageName :    package com.epoch.loan.workshop.timing.task; * @className : BaseTask
 * @createTime :  2021/12/15 15:05
 * @description : TODO
 */
public class BaseTask {
    /**
     * 支付分配队列
     */
    @Autowired
    protected RemittanceMQManager remittanceMqManagerProduct;

    /**
     * 定时任务
     */
    @Autowired
    public LoanTimingDao timingDao;

    /**
     * 订单模型审核记录
     */
    @Autowired
    public LoanOrderExamineDao loanOrderExamineDao;

    /**
     * 订单队列
     */
    @Autowired
    public OrderMQManager orderMQManager;

    /**
     * 订单队列
     */
    @Autowired
    public LoanOrderModelDao loanOrderModelDao;

    /**
     * 订单队列
     */
    @Autowired
    public LoanOrderDao loanOrderDao;

    /**
     * 支付渠道
     */
    @Autowired
    public LoanPaymentDao loanPaymentDao;

    /**
     * 订单队列
     */
    @Autowired
    public LoanRemittanceOrderRecordDao loanRemittanceOrderRecordDao;
    /**
     * 订单队列
     */
    @Autowired
    public LoanRemittancePaymentRecordDao loanRemittancePaymentRecordDao;

    /**
     * 获取定时任务参数（直接从数据库加载，获取最新的参数）
     *
     * @param jobExecutionContext
     * @return
     */
    public String getParams(JobExecutionContext jobExecutionContext) {
        return timingDao.findTimingParams(jobExecutionContext.getJobDetail().getKey().getName());
    }
}
