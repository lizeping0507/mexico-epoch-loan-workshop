package com.epoch.loan.workshop.timing.task;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.LoanRemittancePaymentRecordStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.SendDingDingUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.timing.task
 * @className : MinotorRayMentTask
 * @createTime : 2022/03/25 15:16
 * @Description: 监控半小时放款情况
 */
@DisallowConcurrentExecution
@Component
public class MonitorLoanDataTask extends BaseTask implements Job {

    /**
     * 方法主体
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String params = this.getParams(context);
        if (StringUtils.isNotEmpty(params)) {
            JSONObject jsonObject = JSONObject.parseObject(params);
            String webHook = jsonObject.getString("webHook");
            String secret = jsonObject.getString("secret");

            Date endTime = new Date();
            Date starTime = DateUtil.addMinute(endTime, -30);
            Date yesterday = DateUtil.addDay(endTime, -1);

            // 发送的消息
            StringBuffer sb = new StringBuffer();

            // 查询所有聚道
            List<LoanPaymentEntity> list = loanPaymentDao.selectAll();
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(paymentEntity -> {
                    boolean isHead = true;

                    // 前30m发起聚道总数
                    Integer paySum = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), null, starTime, endTime);
                    if (ObjectUtils.isNotEmpty(paySum) && paySum != 0) {
                        isHead = false;

                        // 前30m发起聚道成功数
                        Integer success = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), LoanRemittancePaymentRecordStatus.SUCCESS, starTime, endTime);
                        double v = BigDecimal.valueOf(success).divide(BigDecimal.valueOf(paySum), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)).doubleValue();
                        Integer fail = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), LoanRemittancePaymentRecordStatus.FAILED, starTime, endTime);
                        Integer process = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), LoanRemittancePaymentRecordStatus.PROCESS, starTime, endTime);
                        sb.append("渠道：").append(paymentEntity.getName())
                                .append("\n\n30m-放款发起数：").append(paySum)
                                .append("\n\n30m-放款成功数：").append(success)
                                .append("\n\n30m-放款成功率：").append(v).append("%\n")
                                .append("\n\n30m-正在进行数：").append(process)
                                .append("\n\n30m-放款失败数：").append(fail);
                    }

                    // 前24H发起聚道总数
                    Integer oneDaySum = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), null, yesterday, endTime);
                    if (ObjectUtils.isNotEmpty(oneDaySum) && oneDaySum != 0) {

                        // 前24H发起聚道成功数
                        Integer oneDaySuccess = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), LoanRemittancePaymentRecordStatus.SUCCESS, yesterday, endTime);
                        double v = BigDecimal.valueOf(oneDaySuccess).divide(BigDecimal.valueOf(oneDaySum), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)).doubleValue();
                        Integer oneDayFail = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), LoanRemittancePaymentRecordStatus.FAILED, yesterday, endTime);
                        Integer oneDayProcess = loanRemittancePaymentRecordDao.countByTime(paymentEntity.getId(), LoanRemittancePaymentRecordStatus.PROCESS, yesterday, endTime);

                        if (isHead) {
                            sb.append("渠道：").append(paymentEntity.getName());
                        }
                        sb.append("\n\n24H-放款发起数：").append(oneDaySum)
                                .append("\n\n24H-放款成功数：").append(oneDaySuccess)
                                .append("\n\n24H-放款成功率：").append(v).append("%\n")
                                .append("\n\n24H-正在进行数：").append(oneDayProcess)
                                .append("\n\n24H-放款失败数：").append(oneDayFail)
                                .append("\n\n----------- \n\n");
                    }
                });
            }

            String content = sb.toString();

            if (StringUtils.isNotBlank(content)) {
                try {
                    String allWebHook = SendDingDingUtils.getAllWebHook(webHook, secret);
                    SendDingDingUtils.sendMarkToDingDing("[贷超V2放款信息]", sb.toString(), false, null, allWebHook);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
