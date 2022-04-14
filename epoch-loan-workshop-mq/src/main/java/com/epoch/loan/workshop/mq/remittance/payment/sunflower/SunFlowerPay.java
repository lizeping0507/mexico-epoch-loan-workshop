package com.epoch.loan.workshop.mq.remittance.payment.sunflower;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.constant.LoanRemittancePaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.params.system.AccessLogParams;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.sunflower;
 * @className : SunFlowerPay
 * @createTime : 2022/3/01 11:22
 * @description : SunFlowerPay 放款队列消费
 */
@RefreshScope
@Component
@Data
public class SunFlowerPay extends BaseRemittancePaymentMQListener implements MessageListenerConcurrently {
    /**
     * 放款队列标签标签
     */
    @Value("${rocket.remittance.sunFlowerPay.subExpression}")
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
                    LogUtil.sysError("[SunFlowerPay]", exception);
                }
                LogUtil.sysError("[SunFlowerPay]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 放款
     */
    public Integer payout(LoanPaymentEntity paymentEntity, LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord) throws Exception {

        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String merchantId = paymentConfig.getString(PaymentField.SUNFLOWERPAY_MERCHANT_ID);
        String key = paymentConfig.getString(PaymentField.SUNFLOWERPAY_KEY);
        String notifyUrl = paymentConfig.getString(PaymentField.SUNFLOWERPAY_NOTIFYURL);
        String payoutUrl = paymentConfig.getString(PaymentField.SUNFLOWERPAY_PAYOUT_URL);
        // 封装请求参数
        SunFlowerPayPayoutParam param = new SunFlowerPayPayoutParam();
        param.setMerchant_id(merchantId);
        param.setMerchant_trade_no(paymentRecord.getId());
        param.setAmount(NumberUtil.round(orderRecord.getAmount(), 2) + "");
        param.setDescription("payout");
        param.setPay_type("BANK");
        param.setCustomer_name(orderRecord.getName());
        param.setCustomer_email(orderRecord.getEmail());
        param.setCustomer_mobile(orderRecord.getPhone());
        param.setAccount_no(orderRecord.getBankCard().trim());
        param.setIfsc(orderRecord.getIfsc());
        param.setNotify_url(notifyUrl);
        param.setTimestamp(DateUtil.now());
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
            updateLoanRemittancePaymentRecordLog(paymentRecord.getId(), JSONObject.toJSONString(param), "异常");
            LogUtil.sysError("SUNFLOWERPAY发起放款异常", e);
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(returnResult)) {
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(returnResult);

        // 处理接口返回信息
        String code = returnObject.getString(PaymentField.SUNFLOWERPAY_CODE);

        // 判断接口状态
        if (PaymentField.SUNFLOWERPAY_SUCCESS_CODE_VAL.equals(code)) {
            return PaymentField.PAYOUT_SUCCESS;
        } else {
            return PaymentField.PAYOUT_FAILED;
        }
    }

    /**
     * 订单查询
     */
    private int queryOrder(LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord, LoanPaymentEntity paymentEntity) throws Exception {
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String queryUrl = paymentConfig.getString(PaymentField.SUNFLOWERPAY_QUERY_URL);
        String merchantId = paymentConfig.getString(PaymentField.SUNFLOWERPAY_MERCHANT_ID);
        String key = paymentConfig.getString(PaymentField.SUNFLOWERPAY_KEY);

        SunFlowerPayPayOutQueryParam param = new SunFlowerPayPayOutQueryParam();
        param.setMerchant_id(merchantId);
        param.setMerchant_trade_no(paymentRecord.getId());
        param.setTrade_type("payout");
        param.setTimestamp(DateUtil.now());
        param.setSign(sign(param, key));

        // 三方请求处理
        String returnResult;
        try {
            // 发送第三方提现请求
            returnResult = HttpUtils.POST(queryUrl, JSONObject.toJSONString(param));

        } catch (Exception e) {
            // 请求异常
            LogUtil.sysError("SunFlowerPay查询代付异常:", e);
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
        String code = returnObject.getString(PaymentField.SUNFLOWERPAY_CODE);

        // 判断接口状态
        if (!PaymentField.SUNFLOWERPAY_SUCCESS_CODE_VAL.equals(code)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 返回数据
        String tradeStatus = returnObject.getString(PaymentField.SUNFLOWERPAY_STATUS);

        // 失败
        if (PaymentField.SUNFLOWERPAY_SUCCESS_VAL.equals(tradeStatus)) {
            // 放款成功
            return PaymentField.PAYOUT_SUCCESS;
        } else if (PaymentField.SUNFLOWERPAY_FAILED_VAL.equals(tradeStatus)) {
            // 放款失败
            return PaymentField.PAYOUT_FAILED;
        } else {
            // 其他状态 进行中
            return PaymentField.PAYOUT_PROCESS;
        }
    }

    /**
     * 参数签名
     * 第⼀步： 假设所有发送或者接收到的数据为集合M，将集合M内⾮空参数值的参数按照参数名ASCII
     * 码从⼩到⼤排序（字典序），使⽤URL键值对的格式（即key1=value1&key2=value2）拼接成字
     * 符串stringA。
     * 特别注意以下重要规则：
     * ◆ 参数名ASCII码从⼩到⼤排序（字典序）；
     * ◆ 如果参数的值为空不参与签名；
     * ◆ 参数名区分⼤⼩写；
     * ◆ 验证调⽤返回或⽀付平台主动通知签名时，传送的sign参数不参与签名，将⽣成的签名与该sign
     * 值作校验。
     * ◆ ⽀付平台接⼝可能增加字段，验证签名时必须⽀持增加的扩展字段
     *
     * 第⼆步： 对stringA进⾏RSA运算，得到sign值signValue。
     *
     * @param param 请求参数
     * @return 签名字符串
     */
    public static String sign(Object param, String key) {

        StringBuilder data = new StringBuilder();
        // Bean转Map
        Map<String, Object> map = BeanUtil.beanToMap(param);
        // 取所有字段名并排序
        List<String> filedList = new ArrayList<>(map.keySet());
        Collections.sort(filedList);
        // 拼接kv
        for (String filed : filedList) {
            Object value = map.get(filed);
            if (value != null && !"".equals(value)) {
                data.append(filed).append("=").append(value).append("&");
            }
        }
        try {
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(data.toString().getBytes(StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
        }
    }
}
