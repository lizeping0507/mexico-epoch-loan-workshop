package com.epoch.loan.workshop.mq.repayment.incash;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
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
 * @packageName : com.epoch.loan.workshop.mq.repayment.incash;
 * @className : InCashPay
 * @createTime : 2022/4/12 14:38
 * @description : InCashPay放款查询队列
 */
@RefreshScope
@Component("RepaymentInCashPay")
@Data
public class InCashPay extends BaseRepaymentMQListener implements MessageListenerConcurrently {

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
                    LogUtil.sysError("[InCashPay]", exception);
                }
                LogUtil.sysError("[InCashPay]", e);
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
        String merchantId = paymentConfig.getString(PaymentField.INCASHPAY_MERCHANT_ID);
        String key = paymentConfig.getString(PaymentField.INCASHPAY_KEY);
        String queryUrl = paymentConfig.getString(PaymentField.INCASHPAY_IN_QUERY_URL);
        String notifyUrl = paymentConfig.getString(PaymentField.INCASHPAY_NOTIFY_URL);

        // 获取记录Id作为查询依据
        String businessId = paymentRecord.getBusinessId();
        // 参数封装
        InCashPayQueryParams params = new InCashPayQueryParams();
        params.setMerchant(merchantId);
        params.setOrderId(paymentRecord.getId());
        params.setSign(sign(params,key));
        // 发起请求
        String result;
        try {

            result = HttpUtils.POST(queryUrl, JSONObject.toJSONString(params));

            LogUtil.sysInfo("queryUrl: {} result : {}", queryUrl, result);
            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), businessId, result);
        } catch (Exception e) {
            LogUtil.sysError("[InCashPay repayment queryError]", e);
            // 更新请求响应数据
            updateSearchRequestAndResponse(paymentRecord.getId(), businessId, "Error");
            // 请求失败
            return PaymentField.PAY_QUERY_ERROR;
        }

        // 解析结果
        try {
            JSONObject resJsonObj = JSONObject.parseObject(result);
            String code = resJsonObj.getString(PaymentField.INCASHPAY_CODE);
            if (!code.equals(PaymentField.INCASHPAY_SUCCESS_CODE_VAL)) {
                // 请求异常
                return PaymentField.PAY_QUERY_ERROR;
            }
            JSONObject data = resJsonObj.getJSONObject(PaymentField.INCASHPAY_DATA);
            String status = data.getString(PaymentField.INCASHPAY_STATUS);
            if (PaymentField.INCASHPAY_IN_SUCCESS_VAL.equals(status)) {
                // 支付成功
                return PaymentField.PAY_SUCCESS;
            } else if (PaymentField.INCASHPAY_IN_FAILED_VAL.equals(status)) {
                // 支付失败
                return PaymentField.PAY_FAILED;
            } else {
                // 进行中
                return PaymentField.PAY_PROCESS;
            }
        } catch (Exception e) {
            LogUtil.sysError("[InCashPay repayment queryParseError]", e);
            // 查询解析异常
            return PaymentField.PAY_QUERY_ERROR;
        }

    }

    /**
     * 第一步，设所有发送或者接收到的数据为集合M，去掉sign参数、去掉空值参数得到的集合N，将集合N内所有参数按照参数名ASCII码从小到大排序（字典序），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
     * <p>
     * 第二步，在stringA最后拼接上&key=bHXJGsw6CsxkSb…得到stringSignTemp字符串，stringSignTemp=”amount=amount&callbackUrl=callbackUrl&customEmail=email&customMobile=mobile&customName=name&merchant=merchant¬ifyUrl=notifyUrl&key=bHXJGsw6CsxkSb…”
     * （PS 若显示文档出现“¬ifyUrl”等异常，请参照：）
     * <p>
     * 第三步，对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为小写，得到sign值signValue， sign=MD5(stringSignTemp).toLowerCase()
     *
     * @param param
     * @param key
     * @return
     */
    public static String sign(Object param, String key) {
        StringBuilder tempSign = new StringBuilder();

        // Bean转Map
        Map<String, Object> map = BeanUtil.beanToMap(param);

        // 取所有字段名并排序
        List<String> filedList = new ArrayList<>(map.keySet());
        Collections.sort(filedList);

        // 拼接kv
        for (String filed : filedList) {
            Object value = map.get(filed);
            if (value != null) {
                tempSign.append(filed).append("=").append(value).append("&");
            }
        }

        // 拼接key
        tempSign.append("key=").append(key);

        // md5并转小写
        return SecureUtil.md5(tempSign.toString()).toLowerCase();
    }
}