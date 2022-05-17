package com.epoch.loan.workshop.mq.repayment.panda;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.common.params.params.request.PreRepaymentParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
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

import java.util.*;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.repayment.panda;
 * @className : PandaPay
 * @createTime : 2022/4/25
 * @description : PandaPAY还款查询队列
 */
@RefreshScope
@Component("RepaymentPandaPay")
@Data
public class PandaPay extends BaseRepaymentMQListener implements MessageListenerConcurrently {

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

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
                    LogUtil.sysInfo("queryRes : {}",JSONObject.toJSONString(queryRes));

                    if (queryRes.equals(PaymentField.PAY_SUCCESS)) {
                        // 还款成功 修改状态
                        updateRepaymentPaymentRecordStatus(paymentRecord.getId(), LoanRepaymentPaymentRecordStatus.SUCCESS);

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
                        if (intervalMinute > 30) {
                            // 半小时外订单 抛出队列
                            continue;
                        }
                        // 半小时内订单 重回队列
                        retryRepayment(repaymentParams, subExpression());
                    }
                }
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retryRepayment(repaymentParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[PandaPay]", exception);
                }
                LogUtil.sysError("[PandaPay]", e);
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
        Integer type = paymentRecord.getType();
        int result = 0;
        if(type == 1){
            result = speiQueryOrder(paymentRecord, loanPayment);
        }else if(type == 0){
            result = oxxoQueryOrder(paymentRecord, loanPayment);
        }

        return result;
    }

    public int speiQueryOrder(LoanRepaymentPaymentRecordEntity paymentRecord, LoanPaymentEntity loanPayment) {

        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(loanPayment.getConfig());
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);
        String queryUrl = paymentConfig.getString(PaymentField.PANDAPAY_IN_QUERY_URL);

        // 获取记录Id作为查询依据
        String businessId = paymentRecord.getBusinessId();
        // 参数封装
        PandaPayQueryParams params = new PandaPayQueryParams();
        params.setClabe(paymentRecord.getClabe());
        Map<String,String> header = new HashMap<>();
        header.put("Encoding","UTF-8");
        header.put("Content-Type","application/json");
        header.put("Authorization", sign(JSONObject.toJSONString(params), key));
        header.put("AppId", appId);
        // 发起请求
        String result;
        try {
            result = HttpUtils.simplePostInvoke(queryUrl, JSONObject.toJSONString(params), header);
//            {"resultado":{"result":{"abono":{"claveRastreo":"Ras00032","institucionOrdenante":"90646","callbackTime":"2021-08-11 14:56:27","monto":"100.00","cuentaBeneficiario":"646180130900000011","nombreBeneficiario":"beneficiario","nombreOrdenante":"BRANDME CROWDMARKETING, SAPI DE CV","cuentaOrdenante":"646180110400000007","id":"41794243","referenciaNumerica":"9999999","institucionBeneficiaria":"90646"}}}}
            LogUtil.sysInfo("queryUrl: {} result : {}", queryUrl, result);
            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), JSONObject.toJSONString(params), result);
        } catch (Exception e) {
            LogUtil.sysError("[PandaPay repayment queryError]", e);
            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), JSONObject.toJSONString(params), "Error");
            // 请求失败
            return PaymentField.PAY_QUERY_ERROR;
        }

        // 解析结果
        try {
            JSONObject resJsonObj = JSONObject.parseObject(result);
            if (ObjectUtils.isEmpty(resJsonObj)) {
                // 请求异常
                return PaymentField.PAY_QUERY_ERROR;
            }
            JSONObject resultado = resJsonObj.getJSONObject("resultado");
            if (ObjectUtils.isEmpty(resultado.getString(PaymentField.PANDAPAY_DESCRIPCION_ERROR))) {
                // 支付成功
                String monto = resultado.getJSONObject("result").getJSONObject("abono").getString("monto");

                // 还款成功 修改实际支付金额
                paymentRecord.setActualAmount(Double.parseDouble(monto));
                updateRepaymentPaymentRecordActualAmount(paymentRecord.getId(), Double.parseDouble(monto));

                return PaymentField.PAY_SUCCESS;
            } else {
                // 支付中
                return PaymentField.PAY_PROCESS;
            }
        } catch (Exception e) {
            LogUtil.sysError("[PandaPay repayment queryParseError]", e);
            // 查询解析异常
            return PaymentField.PAY_QUERY_ERROR;
        }
    }

    public int oxxoQueryOrder(LoanRepaymentPaymentRecordEntity paymentRecord, LoanPaymentEntity loanPayment) {
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(loanPayment.getConfig());
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);
        String oxxPayQueryUrl = paymentConfig.getString(PaymentField.PANDAPAY_IN_OXXO_QUERY_URL);

        // 获取记录Id作为查询依据
        String businessId = paymentRecord.getBusinessId();

        // 参数封装
        OxxoPandaPayQueryParams params = new OxxoPandaPayQueryParams();
        params.setReference(paymentRecord.getClabe());
        Map<String,String> header = new HashMap<>();
        header.put("Encoding","UTF-8");
        header.put("Content-Type","application/json");
        header.put("Authorization", sign(JSONObject.toJSONString(params), key));
        header.put("AppId", appId);

        // 发起请求
        String result;
        try {
            result = HttpUtils.simplePostInvoke(oxxPayQueryUrl, JSONObject.toJSONString(params), header);
            LogUtil.sysInfo("queryUrl: {} result : {}", oxxPayQueryUrl, result);

            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), JSONObject.toJSONString(params), result);
        } catch (Exception e) {
            LogUtil.sysError("[OxxoPandaPay repayment queryError]", e);

            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), JSONObject.toJSONString(params), "Error");

            // 请求失败
            return PaymentField.PAY_QUERY_ERROR;
        }

        // 解析结果
        try {
            JSONObject resJsonObj = JSONObject.parseObject(result);
            if (ObjectUtils.isEmpty(resJsonObj)) {
                // 请求异常
                return PaymentField.PAY_QUERY_ERROR;
            }
            JSONObject resultado = resJsonObj.getJSONObject("resultado");
            if (ObjectUtils.isEmpty(resultado.getString(PaymentField.PANDAPAY_DESCRIPCION_ERROR))) {

                // 支付成功
                return PaymentField.PAY_SUCCESS;
            } else {

                // 支付中
                return PaymentField.PAY_PROCESS;
            }
        } catch (Exception e) {
            LogUtil.sysError("[PandaPay repayment queryParseError]", e);
            // 查询解析异常
            return PaymentField.PAY_QUERY_ERROR;
        }
    }

    /**
     * 参数签名
     */
    public static String sign(String param, String key) {
        String firstMd5 = SecureUtil.md5(param);
        String authorization = SecureUtil.md5(firstMd5 + key);
        return authorization;
    }
}