package com.epoch.loan.workshop.mq.repayment.yeah;

import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.mq.remittance.payment.yeah.YeahPayToken;
import com.epoch.loan.workshop.mq.repayment.BaseRepaymentMQListener;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.repayment.yeah;
 * @className : YeahPay1
 * @createTime : 2022/3/9 10:51
 * @description : YeahPay1放款查询队列
 */
@RefreshScope
@Component("RepaymentYeahPay1")
@Data
public class YeahPay1 extends BaseRepaymentMQListener implements MessageListenerConcurrently {

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * token缓存
     */
    private YeahPayToken token;

    /**
     * 消费任务
     *
     * @param msgs
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (Message msg : msgs) {
            // 消息对象
            RepaymentParams repaymentParams = null;

            try {
                // 获取消息对象
                repaymentParams = getMessage(msg, RepaymentParams.class);
                if (ObjectUtils.isEmpty(repaymentParams)) {
                    continue;
                }

                String id = repaymentParams.getId();

                // 查询放款详情记录
                LoanRepaymentPaymentRecordEntity paymentRecord = loanRepaymentPaymentRecordDao.findRepaymentPaymentRecordById(id);
                if (ObjectUtils.isEmpty(paymentRecord)) {
                    continue;
                }

                // 放款状态已结束 不在处理
                if (paymentRecord.getStatus().equals(LoanRepaymentPaymentRecordStatus.SUCCESS)
                        || paymentRecord.getStatus().equals(LoanRepaymentPaymentRecordStatus.FAILED)) {
                    continue;
                }

                // 查询渠道信息
                LoanPaymentEntity loanPayment = loanPaymentDao.getById(paymentRecord.getPaymentId());

                // 判断当前放款订单状态
                if (paymentRecord.getStatus().equals(LoanRepaymentPaymentRecordStatus.PROCESS)) {
                    // 进行中状态表示发起成功 需要查询支付状态
                    Integer queryRes = queryOrder(paymentRecord, loanPayment);
                    LogUtil.sysInfo("queryRes : {}", JSONObject.toJSONString(queryRes));

                    // TODO 模拟成功
                    queryRes = 1;

                    if (queryRes.equals(PaymentField.PAY_SUCCESS)) {
                        // 还款成功 修改状态
                        updateRepaymentPaymentRecordStatus(paymentRecord.getId(), LoanRepaymentPaymentRecordStatus.SUCCESS);
                        // 还款成功 修改实际支付金额
                        updateRepaymentPaymentRecordActualAmount(paymentRecord.getId(), paymentRecord.getAmount());

                        //发送到订单完结队列
                        OrderParams orderParams = new OrderParams();
                        orderParams.setOrderId(paymentRecord.getOrderId());
                        orderParams.setGroupName("SYSTEM");
                        orderParams.setOrderBillId(paymentRecord.getOrderBillId());
                        orderParams.setAmount(paymentRecord.getAmount());
                        sendToOrderCompleteQueue(orderParams, orderMQManager.getOrderCompleteSubExpression());
                    } else if (queryRes.equals(PaymentField.PAY_FAILED)) {
                        // 放款失败 修改状态 (已经成功过不修改)
                        updateRepaymentPaymentRecordStatus(paymentRecord.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                    } else {
                        // 时间判断 该笔订单创建半个小时仍然没有明确支付结果 抛出队列 等定时任务兜底
                        Date createTime = paymentRecord.getCreateTime();
                        int intervalMinute = DateUtil.getIntervalMinute(new Date(), createTime);
                        if (intervalMinute < 30) {
                            // 半小时内订单 重回队列
                            retryRepayment(repaymentParams, subExpression());
                        }
                    }
                }
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retryRepayment(repaymentParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[YeahPay1]", exception);
                }
                LogUtil.sysError("[YeahPay1]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }


    /**
     * 查询代收结果
     *
     * @param paymentRecord 还款记录详情
     * @param loanPayment   渠道信息
     * @return 查询结果
     */
    private int queryOrder(LoanRepaymentPaymentRecordEntity paymentRecord, LoanPaymentEntity loanPayment) {
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(loanPayment.getConfig());
        String appId = paymentConfig.getString(PaymentField.YEAHPAY_APP_ID);
        String appKey = paymentConfig.getString(PaymentField.YEAHPAY_APP_KEY);
        String queryUrl = paymentConfig.getString(PaymentField.YEAHPAY_QUERY_URL);
        String tokenUrl = paymentConfig.getString(PaymentField.YEAHPAY_TOKENURL);
        String notifyUrl = paymentConfig.getString(PaymentField.YEAHPAY_NOTIFYURL);
        String merchantId = paymentConfig.getString(PaymentField.YEAHPAY_MERCHANTID);

        // 获取记录Id作为查询依据
        String businessId = paymentRecord.getBusinessId();

        // 发起请求
        String result;
        try {
            // 获取token
            String token = getToken(appId, appKey, tokenUrl);

            // 请求头
            Map<String, String> headers = new HashMap<>(2);
            headers.put("Authorization", "Bearer " + token);

            result = HttpUtils.GET_WITH_HEADER(queryUrl + businessId, null, headers);

            LogUtil.sysInfo("queryUrl: {}  token: {} result : {}", queryUrl, token, result);
            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), businessId, result);
        } catch (Exception e) {
            LogUtil.sysError("[YeahPay1 repayment queryError]", e);
            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), businessId, "Error");
            // 请求失败
            return PaymentField.PAY_QUERY_ERROR;
        }


        // 解析结果
        try {
            JSONObject resJsonObj = JSONObject.parseObject(result);
            String code = resJsonObj.getString(PaymentField.YEAHPAY_CODE);
            if (!code.equals(PaymentField.YEAHPAY_SUCCESS_CODE_VAL)) {
                // 请求异常
                return PaymentField.PAY_QUERY_ERROR;
            }
            Integer status = resJsonObj.getJSONObject("result").getInteger("status");
            if (status == 1) {
                // 支付成功
                return PaymentField.PAY_PROCESS;
            } else if (status == 2) {
                // 支付失败
                return PaymentField.PAY_FAILED;
            } else {
                // 进行中
                return PaymentField.PAY_PROCESS;
            }
        } catch (Exception e) {
            LogUtil.sysError("[YeahPay1 repayment queryParseError]", e);
            // 查询解析异常
            return PaymentField.PAY_QUERY_ERROR;
        }

    }


    /**
     * 获取 Yeahpay token
     *
     * @param appId  appId
     * @param appKey appKey
     * @return token
     */
    public String getToken(String appId, String appKey, String url) {
        if (ObjectUtils.isEmpty(token) || System.currentTimeMillis() >= token.getExpirationTimestmp()) {


            // 请求
            String response;
            try {
                // 请求头
                Map<String, String> headers = new HashMap<>();
                String authorization = PaymentField.YEAHPAY_BASIC + Base64Encoder.encode(appId + ":" + appKey);
                headers.put(PaymentField.YEAHPAY_AUTHORIZATION, authorization);
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                // 请求参数 固定的
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "client_credentials");

                response = HttpUtils.POST_WITH_HEADER(url, params, headers);

                JSONObject jsonObject = JSONObject.parseObject(response);
                String accessToken = jsonObject.getString("access_token");
                Long expiresIn = jsonObject.getLong("expires_in");

                token = new YeahPayToken();
                token.setAccessToken(accessToken);
                token.setExpirationTimestmp(System.currentTimeMillis() + expiresIn - 1000 * 60 * 60 * 24);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return token.getAccessToken();
    }

}
