package com.epoch.loan.workshop.mq.remittance.payment.panda;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.constant.LoanRemittancePaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.UUIDUtils;
import com.epoch.loan.workshop.mq.remittance.payment.BaseRemittancePaymentMQListener;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.json.JSONString;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.panda;
 * @className : PandaPay
 * @createTime : 2022/4/25
 * @description : PandaPay 放款队列消费
 */
@RefreshScope
@Component
@Data
public class PandaPay extends BaseRemittancePaymentMQListener implements MessageListenerConcurrently {
    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 参数签名
     */
    public static String sign(String param, String key) {
        String firstMd5 = SecureUtil.md5(param);
        String authorization = SecureUtil.md5(firstMd5 + key);
        return authorization;
    }

    /**
     * 消费任务
     *
     * @param msgs
     * @param context
     * @return
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

                // 订单支付记录ID
                String orderRecordId = orderRecord.getId();

                // 更新放款记录状态为 "进行中"
                updateRemittanceOrderRecordStatus(paymentRecord.getRemittanceOrderRecordId(), LoanRemittanceOrderRecordStatus.PROCESS);

                // 判断当前放款订单状态
                if (paymentRecord.getStatus().equals(LoanRemittancePaymentRecordStatus.CREATE)) {
                    // 创建状态 发起放款
                    Integer payoutResult = payout(loanPayment, orderRecord, paymentRecord);

                    // 判断支付状态
                    if (payoutResult.equals(PaymentField.PAYOUT_SUCCESS)) {
                        // 发起成功 修改状态
                        updateLoanRemittancePaymentRecordStatus(id, LoanRemittancePaymentRecordStatus.PROCESS);
                        retryRemittance(remittanceParams, subExpression());
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

                    // 判断支付状态
                    if (res == PaymentField.PAYOUT_SUCCESS) {
                        // 成功 修改支付记录状态及支付详情记录状态
                        updateRemittanceOrderRecordStatus(orderRecord.getId(), LoanRemittanceOrderRecordStatus.SUCCESS);
                        updateLoanRemittancePaymentRecordStatus(paymentRecord.getId(), LoanRemittancePaymentRecordStatus.SUCCESS);
                        updateProcessRemittancePaymentRecordId(orderRecordId, "");
                        updateSuccessRemittancePaymentRecordId(orderRecordId, paymentRecord.getId());
                        continue;
                    } else if (res == PaymentField.PAYOUT_PROCESS || res == PaymentField.PAYOUT_QUERY_ERROR) {
                        //  进行中 重回放款队列
                        retryRemittance(remittanceParams, subExpression());
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
                    retryRemittance(remittanceParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[InPay]", exception);
                }
                LogUtil.sysError("[InPay]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 放款
     *
     * @param paymentEntity
     * @param orderRecord
     * @param paymentRecord
     * @return
     */
    public Integer payout(LoanPaymentEntity paymentEntity, LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord) {
        // 格式化数字
        DecimalFormat df = new DecimalFormat("0.00");

        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);
        String paymentBankAccount = paymentConfig.getString(PaymentField.PANDAPAY_PAYMENT_BANK_ACCOUNT);
        Integer paymentBankCode = paymentConfig.getInteger(PaymentField.PANDAPAY_PAYMENT_BANK_CODE);
        String rfcCurpOrdenante = paymentConfig.getString(PaymentField.PANDAPAY_PAYMENT_RFC);
        Integer paymentAccountType = paymentConfig.getInteger(PaymentField.PANDAPAY_PAYMENT_ACCOUNT_TYPE);
        Integer paymentType = paymentConfig.getInteger(PaymentField.PANDAPAY_PAYMENT_TYPE);
        String payoutUrl = paymentConfig.getString(PaymentField.PANDAPAY_PAYOUT_URL);
        // 封装请求参数
        PandaPayPayoutParam param = new PandaPayPayoutParam();
        param.setClaveRastreo(paymentRecord.getId());
        param.setConceptoPago(orderRecord.getRemarks());
        param.setCuentaBeneficiario(orderRecord.getRemittanceAccount());
        param.setCuentaOrdenante(paymentBankAccount);
        param.setInstitucionContraparte(orderRecord.getBank());
        param.setInstitucionOperante(paymentBankCode);
        param.setMonto(df.format(orderRecord.getAmount()));
        param.setNombreBeneficiario(orderRecord.getName());
        param.setReferenciaNumerica(UUIDUtils.uuid().indexOf(7));
        if(ObjectUtils.isNotEmpty(orderRecord.getCurp())){
            param.setRfcCurpBeneficiario(orderRecord.getCurp());
        } else if(ObjectUtils.isNotEmpty(orderRecord.getRfc())){
            param.setRfcCurpBeneficiario(orderRecord.getRfc());
        } else{
            param.setRfcCurpBeneficiario("ND");
        }
        param.setRfcCurpOrdenante(rfcCurpOrdenante);
        param.setTipoCuentaBeneficiario(orderRecord.getType() == 1 ? 40 : 3);
        param.setTipoCuentaOrdenante(paymentAccountType);
        param.setTipoPago(paymentType);
        Map<String,String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", sign(JSONObject.toJSONString(param), key));
        header.put("AppId", appId);
        /* 第三方请求处理 */
        String returnResult;
        try {
            // 发送第三方代付请求
            returnResult = HttpUtils.simplePostInvoke(payoutUrl, JSONObject.toJSONString(param), header);
//            {"transactionId":"e7fa49e596c8455c9f4d199444980daa","resultado":{"id":110499733}}
        } catch (Exception e) {
            updateLoanRemittancePaymentRecordLog(paymentRecord.getId(), JSONObject.toJSONString(param), "异常");
            LogUtil.sysError("PANDAPAY发起放款异常", e);
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 存储请求信息 和响应信息
        updateLoanRemittancePaymentRecordLog(
                paymentRecord.getId(),
                JSONObject.toJSONString(param),
                returnResult);

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(returnResult)) {
            System.out.println("请求失败");
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(returnResult);

        // 处理接口返回信息
        JSONObject resultado = returnObject.getJSONObject("resultado");
        String descripcionError = resultado.getString(PaymentField.PANDAPAY_DESCRIPCION_ERROR);
        // 判断接口状态
        if (ObjectUtils.isEmpty(descripcionError)) {
            System.out.println("支付成功");
            return PaymentField.PAYOUT_SUCCESS;
        } else {
            System.out.println("支付失败");
            return PaymentField.PAYOUT_FAILED;
        }
    }

    /**
     * 订单查询
     *
     * @param orderRecord
     * @param paymentRecord
     * @param paymentEntity
     * @return
     */
    private int queryOrder(LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord, LoanPaymentEntity paymentEntity) {
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        String queryUrl = paymentConfig.getString(PaymentField.PANDAPAY_QUERY_URL);
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);

        PandaPayPayOutQueryParam param = new PandaPayPayOutQueryParam();
        param.setClaveRastreo(paymentRecord.getId());
        // 三方请求处理
        Map<String,String> header = new HashMap<>();
        header.put("Encoding","UTF-8");
        header.put("Content-Type","application/json");
        header.put("Authorization", sign(JSONObject.toJSONString(param), key));
        header.put("AppId", appId);
        String returnResult;
        try {
            // 发送第三方提现请求
            returnResult = HttpUtils.simplePostInvoke(queryUrl, JSONObject.toJSONString(param), header);
//            {"resultado":{"result":{"estado":"Cancel","folioOrigen":"P0016215e5265cf805ef0bd1e614","id":"110499733","empresa":"TRANSFER_TO"}}}
        } catch (Exception e) {
            // 请求异常
            LogUtil.sysError("PandaPay查询代付异常:", e);
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
        JSONObject resultado = returnObject.getJSONObject("resultado");
        JSONObject result = resultado.getJSONObject("result");
        // 判断接口状态
        if (ObjectUtils.isEmpty(result)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 返回数据

        if (PaymentField.PANDAPAY_SUCCESS_VAL.equals(result.getString(PaymentField.PANDAPAY_STATUS))) {
            // 放款成功
            return PaymentField.PAYOUT_SUCCESS;
        } else if (PaymentField.PANDAPAY_FAILED_VAL_1.equals(result.getString(PaymentField.PANDAPAY_STATUS)) || PaymentField.PANDAPAY_FAILED_VAL_2.equals(result.getString(PaymentField.PANDAPAY_STATUS))) {
            // 放款失败
            return PaymentField.PAYOUT_FAILED;
        } else {
            // 其他状态 进行中
            return PaymentField.PAYOUT_PROCESS;
        }
    }
}
