package com.epoch.loan.workshop.mq.remittance.payment.hr;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.hr;
 * @className : HrPay
 * @createTime : 2022/3/29 18:08
 * @description : HrPay 放款队列消费
 */
@RefreshScope
@Component
@Data
public class HrPay extends BaseRemittancePaymentMQListener implements MessageListenerConcurrently {
    /**
     * 放款队列标签标签
     */
    @Value("${rocket.remittance.hrPay.subExpression}")
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

                    if (payoutResult.equals(PaymentField.PAYOUT_SUCCESS)) {
                        // 发起成功 修改状态
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
                    if (res == PaymentField.PAYOUT_SUCCESS) {
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
                    LogUtil.sysError("[hrPay]", exception);
                }
                LogUtil.sysError("[hrPay]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 放款
     */
    public Integer payout(LoanPaymentEntity paymentEntity, LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord) {

        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String merchantId = paymentConfig.getString(PaymentField.HRPAY_MERCHANT_ID);
        String key = paymentConfig.getString(PaymentField.HRPAY_KEY);
        String payoutUrl = paymentConfig.getString(PaymentField.HRPAY_PAYOUT_URL);
        String notifyUrl = paymentConfig.getString(PaymentField.HRPAY_NOTIFY_URL);
        String bankCode = paymentConfig.getString(PaymentField.HRPAY_BANK_CODE);

        // 封装请求参数
        HrPayPayoutParam param = new HrPayPayoutParam();
        param.setMemberid(merchantId);
        param.setOrderid(paymentRecord.getId());
        param.setBankcode(bankCode);
        param.setNotifyurl(notifyUrl);
        param.setAmount(NumberUtil.round(orderRecord.getAmount(), 2) + "");
        param.setMobile(orderRecord.getPhone());
        param.setEmail(orderRecord.getEmail());
        param.setBankname("bank");
        param.setCardnumber(orderRecord.getBankCard());
        param.setAccountname(orderRecord.getName());
        param.setIfsc(orderRecord.getIfsc());
        param.setSign(sign(param, key));

        /* 第三方请求处理 */
        String returnResult;
        try {
            // 发送第三方代付请求
            returnResult = HttpUtils.POST_FORM(payoutUrl, JSONObject.toJSONString(param));
            // 存储请求信息 和响应信息
            updateLoanRemittancePaymentRecordLog(
                    paymentRecord.getId(),
                    JSONObject.toJSONString(param),
                    returnResult);
        } catch (Exception e) {
            updateLoanRemittancePaymentRecordLog(paymentRecord.getId(), JSONObject.toJSONString(param), "异常");
            LogUtil.sysError("HRPAY发起放款异常", e);
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(returnResult)) {
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(returnResult);

        // 处理接口返回信息
        Integer code = returnObject.getInteger(PaymentField.HRPAY_STATUS);

        // 判断接口状态
        if (PaymentField.HRPAY_SUCCESS_CODE_VAL.equals(code) && PaymentField.HRPAY_SUCCESS_VAL.equals(returnObject.getJSONObject("msg").getString(PaymentField.HRPAY_TRADE_STATE))) {
            return PaymentField.PAYOUT_SUCCESS;
        } else {
            return PaymentField.PAYOUT_FAILED;
        }
    }

    /**
     * 订单查询
     */
    private int queryOrder(LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord, LoanPaymentEntity paymentEntity) {
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String queryUrl = paymentConfig.getString(PaymentField.HRPAY_QUERY_URL);
        String merchantId = paymentConfig.getString(PaymentField.HRPAY_MERCHANT_ID);
        String key = paymentConfig.getString(PaymentField.HRPAY_KEY);

        HrPayPayOutQueryParam param = new HrPayPayOutQueryParam();
        param.setMemberid(merchantId);
        param.setOrderid(paymentRecord.getId());
        param.setSign(sign(param, key));

        // 三方请求处理
        String returnResult;
        try {
            // 发送第三方提现请求
            returnResult = HttpUtils.POST_FORM(queryUrl, JSONObject.toJSONString(param));
        } catch (Exception e) {
            // 请求异常
            LogUtil.sysError("HrPay查询代付异常:", e);
            updateLoanRemittancePaymentRecordSearchLog(JSONObject.toJSONString(param), paymentRecord.getId(), "异常");
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 更新查询请求响应报文
        updateLoanRemittancePaymentRecordSearchLog(paymentRecord.getId(), paymentRecord.getId(), returnResult);

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(returnResult)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(returnResult);

        // 处理接口返回信息
        String code = returnObject.getString(PaymentField.HRPAY_CODE);

        // 判断接口状态
        if (!PaymentField.HRRAY_QUERY_SUCCESS_CODE_VAL.equals(code)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 返回数据
        if (PaymentField.HRPAY_SUCCESS_VAL.equals(returnObject.getString(PaymentField.HRPAY_TRADE_STATE))) {
            // 放款成功
            return PaymentField.PAYOUT_SUCCESS;
        } else if (PaymentField.HRPAY_FAILED_VAL.equals(returnObject.getString(PaymentField.HRPAY_TRADE_STATE))) {
            // 放款失败
            return PaymentField.PAYOUT_FAILED;
        } else {
            // 其他状态 进行中
            return PaymentField.PAYOUT_PROCESS;
        }
    }

    /*
     1.将所有需要签名的字段按照 ASCII 码从小到大进行排序，并按照按照 k=v&k=v 的格式拼接字符串，并在字符串后面拼接商户私钥用 &key=x 进行拼接，生成待签名 queryString 字符串。
     2.对生成的 queryString 字符串进行 MD5 签名，得到小写签名串。
     3.除了sign和sign_type以外不为空的参数都需要参与签名(注意：回调以及同步响应的签名方法参数名称为signType)
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
        return SecureUtil.md5(tempSign.toString()).toUpperCase();
    }
}
