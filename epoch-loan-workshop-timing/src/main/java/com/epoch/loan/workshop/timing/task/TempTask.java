package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderExamineEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import lombok.SneakyThrows;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.timing.task;
 * @className : TempTask
 * @createTime : 2022/4/2 17:17
 * @description : 临时处理
 */
@DisallowConcurrentExecution
@Component
public class TempTask extends BaseTask implements Job {

    /**
     * 方法主体
     */
    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        updateOrderBill();
    }

    // 	UPDATE loan_order SET stages = 1 ,stages_day = 7 , interest = 0.70,processing_fee_proportion=40.00,penalty_interest=1.00
    private void updateOrderBill() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd%");
        Date date = simpleDateFormat.parse("2022-01-10%");
        Date now = new Date();


        while(date.getTime() <=now.getTime()){
            String format = simpleDateFormat.format(date);
            List<LoanOrderBillEntity> loanOrderBills = loanOrderBillDao.findByDay(format);
            LogUtil.sysInfo(" {}  数据量: {} 开始处理 ... ", format, loanOrderBills.size());

            for (LoanOrderBillEntity loanOrderBill : loanOrderBills) {
                try{
                    if (loanOrderBill.getType() == null){
                        loanOrderBillDao.updateType(loanOrderBill.getId(), 0, new Date());
                    }
                    if (loanOrderBill.getReceivedAmount() == null){
                        loanOrderBillDao.updateOrderBillReceivedAmount(loanOrderBill.getId(), 0.00, new Date());
                    }
                    if (loanOrderBill.getPrincipalAmount() == null){

                        LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(loanOrderBill.getOrderId());
                        double approvalAmount = loanOrderEntity.getApprovalAmount();
                        double stagesPrincipalAmount = approvalAmount / loanOrderEntity.getStages();
                        // 更新已付金额
                        loanOrderBillDao.updateOrderBillPrincipalAmount(loanOrderBill.getId(), stagesPrincipalAmount, new Date());
                    }
                    if (loanOrderBill.getPunishmentAmount() == null){
                        loanOrderBillDao.updateOrderBillPunishmentAmount(loanOrderBill.getId(), 0.00, new Date());
                    }

                    LoanOrderEntity order = loanOrderDao.findOrder(loanOrderBill.getOrderId());
                    Double approvalAmount = order.getApprovalAmount();
                    Double interest = order.getInterest();
                    Double interestAmount = approvalAmount * (interest / 100);
                    Double stagesInterestAmount = interestAmount / order.getStages();
                    loanOrderBillDao.updateInterestAmount(loanOrderBill.getId(), stagesInterestAmount, new Date());

                    if (loanOrderBill.getIncidentalAmount() == null){
                        loanOrderBillDao.updateOrderBillIncidentalAmount(loanOrderBill.getId(), 0.00, new Date());
                    }
                    if (loanOrderBill.getReductionAmount() == null){
                        loanOrderBillDao.updateOrderBillReductionAmount(loanOrderBill.getId(), 0.00, new Date());
                    }
                    LogUtil.sysInfo(" {}  Id: {} 处理完成 ", format, loanOrderBill.getId());
                }catch (Exception e){
                    LogUtil.sysError(format + "  Id: " +  loanOrderBill.getId() + "处理失败", e);
                }
            }

            LogUtil.sysInfo(" {}  数据量: {} 完成 ... ", format, loanOrderBills.size());
            date = DateUtil.addDay(date, 1);
        }
    }


}
