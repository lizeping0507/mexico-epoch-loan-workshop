package com.epoch.loan.workshop.mq.remittance.payment.fast;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.constant.LoanRemittancePaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.mq.remittance.payment.BaseRemittancePaymentMQListener;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.fast;
 * @className : FastPay
 * @createTime : 2022/2/15 11:19
 * @description : FastPay 放款队列消费
 */
@RefreshScope
@Component
@Data
public class FastPay extends BaseRemittancePaymentMQListener implements MessageListenerConcurrently {
    /**
     * 分配队列标签
     */
    @Value("${rocket.remittance.distribution.subExpression}")
    protected String distributionSubExpression;

    /**
     * 放款队列标签标签
     */
    @Value("${rocket.remittance.fastPay.subExpression}")
    protected String subExpression;

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;


    /**
     * 消费任务
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        // 循环处理消息
        for (Message msg : msgs) {
            // 消息对象
            RemittanceParams remittanceParams = null;

            try {
                // 获取消息对象
                remittanceParams = getMessage(msg, RemittanceParams.class);
                if (ObjectUtils.isEmpty(remittanceParams)) {
                    continue;
                }

                // 放款详情id
                String id = remittanceParams.getId();

                // 查询放款详情记录
                LoanRemittancePaymentRecordEntity paymentRecord = loanRemittancePaymentRecordDao.getById(id);
                if (ObjectUtils.isEmpty(paymentRecord)) {
                    continue;
                }

                // 放款状态已结束 不在处理
                if (paymentRecord.getStatus().equals(LoanRemittancePaymentRecordStatus.SUCCESS)
                        || paymentRecord.getStatus().equals(LoanRemittancePaymentRecordStatus.FAILED)) {
                    continue;
                }

                // 查询放款记录
                LoanRemittanceOrderRecordEntity orderRecord = loanRemittanceOrderRecordDao.getById(paymentRecord.getRemittanceOrderRecordId());
                if (ObjectUtils.isEmpty(orderRecord)) {
                    continue;
                }

                // 查询渠道信息
                LoanPaymentEntity loanPayment = loanPaymentDao.getById(paymentRecord.getPaymentId());
                if (ObjectUtils.isEmpty(loanPayment)) {
                    continue;
                }

                // 更新放款记录状态为 "进行中"
                updateRemittanceOrderRecordStatus(paymentRecord.getRemittanceOrderRecordId(), LoanRemittanceOrderRecordStatus.PROCESS);

                // 判断当前放款订单状态
                if (paymentRecord.getStatus().equals(LoanRemittancePaymentRecordStatus.CREATE)) {
                    // 创建状态 发起放款
                    Integer payoutResult = payout(loanPayment, orderRecord, paymentRecord);

                    // 判断放款状态
                    if (payoutResult.equals(PaymentField.PAYOUT_REQUEST_SUCCESS)) {
                        updateLoanRemittancePaymentRecordStatus(id, LoanRemittancePaymentRecordStatus.PROCESS);
                        retryRemittance(remittanceParams, subExpression);
                        continue;
                    } else {
                        // 发起失败 标记渠道 重回分配队列
                        updateLoanRemittancePaymentRecordStatus(id, LoanRemittancePaymentRecordStatus.FAILED);
                        remittanceRetryDistribution(remittanceParams, orderRecord);
                        continue;
                    }
                } else if (paymentRecord.getStatus().equals(LoanRemittancePaymentRecordStatus.PROCESS)) {
                    // 进行中状态 查询放款结果
                    int res = queryOrder(orderRecord, paymentRecord, loanPayment);

                    // 判断查询状态
                    if (res == PaymentField.PAYOUT_REQUEST_SUCCESS) {
                        // 成功 修改支付记录状态及支付详情记录状态
                        updateRemittanceOrderRecordStatus(orderRecord.getId(), LoanRemittanceOrderRecordStatus.SUCCESS);
                        updateLoanRemittancePaymentRecordStatus(paymentRecord.getId(), LoanRemittancePaymentRecordStatus.SUCCESS);
                        updateSuccessRemittancePaymentRecordId(orderRecord.getId(), paymentRecord.getId());
                        continue;
                    } else if (res == PaymentField.PAYOUT_PROCESS || res == PaymentField.PAYOUT_QUERY_ERROR) {
                        //  进行中 重回放款队列
                        retryRemittance(remittanceParams, subExpression);
                        continue;
                    } else if (res == PaymentField.PAYOUT_FAILED) {
                        // 失败 标记过滤渠道 重回分配队列
                        updateLoanRemittancePaymentRecordStatus(id, LoanRemittancePaymentRecordStatus.FAILED);
                        remittanceRetryDistribution(remittanceParams, orderRecord);
                        continue;
                    }
                }
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retryRemittance(remittanceParams, subExpression);
                } catch (Exception exception) {
                    LogUtil.sysError("[FastPay]", exception);
                }
                LogUtil.sysError("[FastPay]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 放款
     */
    public Integer payout(LoanPaymentEntity paymentEntity, LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord) {
        // 格式化数字
        DecimalFormat df = new DecimalFormat("0.00");

        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String merchantNo = paymentConfig.getString(PaymentField.FASTPAY_MERCHANTNO);
        int type = paymentConfig.getInteger(PaymentField.FASTPAY_TYPE);
        String notifyUrl = paymentConfig.getString(PaymentField.FASTPAY_NOTIFYURL);
        String version = paymentConfig.getString(PaymentField.FASTPAY_VERSION);
        String payoutUrl = paymentConfig.getString(PaymentField.FASTPAY_PAYOUT_URL);
        String key = paymentConfig.getString(PaymentField.FASTPAY_KEY);

        // 封装请求参数
        FastPayPayoutParam param = new FastPayPayoutParam();
        param.setMerchantNo(merchantNo);
        param.setOrderNo(paymentRecord.getId());
        param.setAmount(df.format(orderRecord.getAmount()));
        param.setType(type);
        param.setNotifyUrl(notifyUrl);
        param.setName(orderRecord.getName());
        param.setAccount(orderRecord.getBankCard());
        param.setIfscCode(orderRecord.getIfsc());
        param.setExt(paymentRecord.getId());
        param.setVersion(version);
        param.setSign(sign(param, key));

        /* 第三方请求处理 */
        String returnResult;
        try {
            // 发送第三方代付请求
            returnResult = HttpUtils.POST(payoutUrl, JSONObject.toJSONString(param));

            // 存储请求信息 和响应信息
            updateLoanRemittancePaymentRecordLog(
                    paymentRecord.getId(),
                    JSONObject.toJSONString(param),
                    returnResult);
        } catch (Exception e) {
            LogUtil.sysError("[FastPay]", e);
            updateLoanRemittancePaymentRecordLog(paymentRecord.getId(), JSONObject.toJSONString(param), "异常");
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(returnResult)) {
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(returnResult);

        // 处理接口返回信息
        String code = returnObject.getString(PaymentField.FASTPAY_CODE);

        // 判断接口状态
        if (PaymentField.FASTPAY_SUCCESS_CODE_VAL.equals(code)) {
            return PaymentField.PAYOUT_REQUEST_SUCCESS;
        } else {
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }
    }

    /**
     * 订单查询
     */
    private int queryOrder(LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord, LoanPaymentEntity paymentEntity) {
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String queryUrl = paymentConfig.getString(PaymentField.FASTPAY_QUERY_URL);
        String merchantNo = paymentConfig.getString(PaymentField.FASTPAY_MERCHANTNO);

        // 请求参数
        HashMap<Object, Object> param = CollUtil.newHashMap();
        param.put(PaymentField.FASTPAY_MERCHANTNO, merchantNo);
        param.put(PaymentField.FASTPAY_ORDERNO, paymentRecord.getId());

        // 三方请求处理
        String returnResult;

        try {
            // 发送第三方提现请求
            returnResult = HttpUtils.POST(queryUrl, JSONObject.toJSONString(param));

            // 存储请求信息 和响应信息
            updateLoanRemittancePaymentRecordLog(
                    orderRecord.getProcessRemittancePaymentRecordId(),
                    JSONObject.toJSONString(param),
                    returnResult);

            // 更新查询请求响应报文
            updateLoanRemittancePaymentRecordSearchLog(paymentRecord.getId(), paymentRecord.getId(), returnResult);
        } catch (Exception e) {
            // 请求异常
            LogUtil.sysError("[FastPay]", e);
            updateLoanRemittancePaymentRecordSearchLog(JSONObject.toJSONString(param), paymentRecord.getId(), "异常");
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(returnResult)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(returnResult);

        // 处理接口返回信息
        String code = returnObject.getString(PaymentField.YEAHPAY_CODE);

        // 判断接口状态
        if (!PaymentField.FASTPAY_SUCCESS_CODE_VAL.equals(code)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 返回数据
        JSONObject data = returnObject.getJSONObject(PaymentField.YEAHPAY_RESULT);

        // 失败
        if (PaymentField.FASTPAY_SUCCESS_CODE_VAL.equals(data.getString(PaymentField.FASTPAY_PAOUT_STATUS))) {
            // 放款成功
            return PaymentField.PAYOUT_SUCCESS;
        } else if (PaymentField.FASTPAY_FAILED_VAL.equals(data.getString(PaymentField.FASTPAY_PAOUT_STATUS))) {
            // 放款失败
            return PaymentField.PAYOUT_FAILED;
        } else {
            // 其他状态 进行中
            return PaymentField.PAYOUT_PROCESS;
        }
    }

    /**
     * 参数签名
     *
     * @param param 请求参数
     * @return 签名字符串
     * 待签名数据按照以下方式进行签名：
     * 将除了参数 key 值按照 ASCII 升序排序
     * 将除了参数值按照 key=value&key=value...的形式拼接成字符串 stringA（空key=value 中 value 为空的不参与拼接， 不进行签名）
     * 将上述字符串使用平台的 key 进行 MD5 加密, MD5(stringA&key=密钥)
     * 最后将 MD5 加密后的字符串转成大写
     */
    public String sign(FastPayPayoutParam param, String key) {
        Map<String, Object> map = BeanUtil.beanToMap(param);

        if (StrUtil.isEmpty(key)) {
            throw new RuntimeException("签名key不能为空");
        }
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        List<String> values = Lists.newArrayList();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String k = String.valueOf(entry.getKey());
            String v = String.valueOf(entry.getValue());
            if (StrUtil.isNotEmpty(v) && entry.getValue() != null && !"sign".equals(k) && !"key".equals(k)) {
                values.add(k + "=" + v);
            }
        }
        values.add("key=" + key);
        String sign = StringUtils.join(values, "&");
        return SecureUtil.md5(sign).toUpperCase();
    }

}
