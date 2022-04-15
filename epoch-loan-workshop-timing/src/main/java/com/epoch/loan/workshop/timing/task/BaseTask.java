package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.common.util.RedisUtil;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : Shangkunfeng
 * @packageName :    package com.epoch.loan.workshop.timing.task; * @className : BaseTask
 * @createTime :  2021/12/15 15:05
 * @description : TODO
 */
public class BaseTask {
    /**
     * 定时任务
     */
    @Autowired
    public LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;
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
     * 订单账单
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;

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
     * 支付分配队列
     */
    @Autowired
    protected RemittanceMQManager remittanceMqManagerProduct;

    /**
     * Redis工具类
     */
    @Autowired
    public RedisUtil redisUtil;

    /**
     * 还款队列生产
     */
    @Autowired
    public RepaymentMQManager repaymentMQManager;

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
