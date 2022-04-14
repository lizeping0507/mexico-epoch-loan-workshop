package com.epoch.loan.workshop.mq.remittance.payment.yeah;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.constant.LoanRemittancePaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment
 * @className : YeahPay
 * @createTime : 2021/12/16 11:00
 * @description : 放款发起及主动查询结果
 */
@RefreshScope
@Component
@Data
public class YeahPay extends BaseRemittancePaymentMQListener implements MessageListenerConcurrently {

    private YeahPayToken token;

    /**
     * 放款队列标签标签
     */
    @Value("${rocket.remittance.yeahPay.subExpression}")
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
                String paymentId = paymentRecord.getPaymentId();
                LoanPaymentEntity loanPayment = loanPaymentDao.getById(paymentId);
                if (ObjectUtils.isEmpty(loanPayment)) {
                    continue;
                }

                // 账号标记校验
                if (StringUtils.isEmpty(paymentRecord.getConfigTag())) {
                    String configTag = chooseAccountByWeight(JSONObject.parseObject(loanPayment.getConfig()));
                    paymentRecord.setConfigTag(configTag);
                    updateRemittancePaymentRecordConfigTag(paymentRecord.getId(), configTag);
                }

                // 更新放款记录状态为 "进行中"
                updateRemittanceOrderRecordStatus(paymentRecord.getRemittanceOrderRecordId(), LoanRemittanceOrderRecordStatus.PROCESS);

                // 判断当前放款订单状态
                if (paymentRecord.getStatus().equals(LoanRemittancePaymentRecordStatus.CREATE)) {
                    // 创建状态 发起放款
                    Integer payoutResult = payout(loanPayment, orderRecord, paymentRecord);

                    // 判断放款状态
                    if (payoutResult.equals(PaymentField.PAYOUT_REQUEST_SUCCESS)) {
                        // 发起成功 修改状态
                        updateLoanRemittancePaymentRecordStatus(id, LoanRemittancePaymentRecordStatus.PROCESS);
                        retryRemittance(remittanceParams, subExpression);
                        continue;
                    } else if (payoutResult.equals(PaymentField.PAYOUT_REQUEST_SPECIAL)) {
                        // 次数限制导致发起失败 标记过滤渠道并重回分配队列
                        updateLoanRemittancePaymentRecordStatus(id, LoanRemittancePaymentRecordStatus.FAILED);
                        remittanceRetryDistribution(remittanceParams, orderRecord);
                        continue;
                    } else {
                        // 根据支付记录和支付渠道查询记录的的configTag
                        updateLoanRemittancePaymentRecordStatus(id, LoanRemittancePaymentRecordStatus.FAILED);
                        List<String> configTags = loanRemittancePaymentRecordDao.findByRemittanceOrderRecordIdAndPaymentId(paymentRecord.getRemittanceOrderRecordId(), loanPayment.getId());

                        // 去除已存在的账号并重新根据账号权重挑选账号
                        String configTag = chooseAccountByWeight(JSONObject.parseObject(loanPayment.getConfig()), configTags);

                        // 没有账号则认为该笔支付已尝试过其他所有账号
                        if (StringUtils.isEmpty(configTag)) {
                            // 其他账号已尝试 ,yeahPay整渠道失败 标记渠道 重回分配队列
                            remittanceRetryDistribution(remittanceParams, orderRecord);
                        } else {
                            // 其他账号未尝试 更换账号并重回放款队列
                            // 创建放款详情记录
                            String paymentLogId = insertRemittancePaymentRecord(orderRecord.getId(), loanPayment.getId(), configTag);

                            // 更新放款记录进行中订单号
                            updateProcessRemittancePaymentRecordId(orderRecord.getId(), paymentLogId);

                            // 重入放款队列
                            remittanceParams.setId(paymentLogId);
                            retryRemittance(remittanceParams, subExpression);
                        }
                    }
                } else if (paymentRecord.getStatus().equals(LoanRemittancePaymentRecordStatus.PROCESS)) {
                    // 进行中状态 查询放款结果
                    int res = queryOrder(orderRecord, paymentRecord, loanPayment);

                    // 判断查询状态
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
                    LogUtil.sysError("[YeahPay]", exception);
                }
                LogUtil.sysError("[YeahPay]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * yeahpay 放款
     */
    public Integer payout(LoanPaymentEntity paymentEntity, LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord) throws Exception {
        // 格式化数字
        DecimalFormat df = new DecimalFormat("0.00");

        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        paymentConfig = paymentConfig.getJSONObject(paymentRecord.getConfigTag());
        String appId = paymentConfig.getString(PaymentField.YEAHPAY_APP_ID);
        String appKey = paymentConfig.getString(PaymentField.YEAHPAY_APP_KEY);
        String url = paymentConfig.getString(PaymentField.YEAHPAY_PAYOUT_URL);
        String tokenUrl = paymentConfig.getString(PaymentField.YEAHPAY_TOKENURL);
        String redirectUrl = paymentConfig.getString(PaymentField.YEAHPAY_QUERY_URL);
        String notifyUrl = paymentConfig.getString(PaymentField.YEAHPAY_NOTIFYURL);

        // 封装请求参数
        YeahPayParam param = new YeahPayParam();
        param.setCountryCode("IN");
        param.setCurrency("INR");
        param.setPayType("card");
        param.setPayoutId(paymentRecord.getId());
        param.setCallBackUrl(notifyUrl);
        List<YeahPayDetailParam> detailParams = new ArrayList<>();
        YeahPayDetailParam yeahPayDetailParam = new YeahPayDetailParam();
        detailParams.add(yeahPayDetailParam);
        yeahPayDetailParam.setAmount(df.format(orderRecord.getAmount()));
        yeahPayDetailParam.setPhone(orderRecord.getPhone());
        yeahPayDetailParam.setEmail(orderRecord.getEmail());
        yeahPayDetailParam.setPayeeAccount(orderRecord.getBankCard());
        yeahPayDetailParam.setPayeeName(orderRecord.getName());
        yeahPayDetailParam.setIfsc(orderRecord.getIfsc());
        yeahPayDetailParam.setIdCard(orderRecord.getAddCard());
        param.setDetails(detailParams);

        // 第三方请求处理
        String returnResult;

        try {
            // 获取token
            String token = getToken(appId, appKey, tokenUrl);

            // 发送第三方提现请求
            returnResult = request(param, url, token);

            // 存储请求信息 和响应信息
            updateLoanRemittancePaymentRecordLog(
                    orderRecord.getProcessRemittancePaymentRecordId(),
                    JSONObject.toJSONString(param),
                    returnResult);
        } catch (Exception e) {
            updateLoanRemittancePaymentRecordLog(paymentRecord.getId(), JSONObject.toJSONString(param), "异常");
            LogUtil.sysError("[YeahPay]", e);
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(returnResult)) {
            return PaymentField.PAYOUT_REQUEST_FAILED;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(returnResult);

        // 处理接口返回信息
        String code = returnObject.getString(PaymentField.YEAHPAY_CODE);

        // 判断接口状态
        if (PaymentField.YEAHPAY_SUCCESS_CODE_VAL.equals(code)) {
            // 发起成功
            return PaymentField.PAYOUT_REQUEST_SUCCESS;
        } else {
            // 发起失败
            if (returnResult.contains(PaymentField.YEAHPAY_MAXIMUM_MSG_VAL)) {
                // YeahPay次数限制导致失败 则直接认为YeahPay整个渠道失败
                return PaymentField.PAYOUT_REQUEST_SPECIAL;
            } else {
                // 其他情况导致失败 换账号重试
                return PaymentField.PAYOUT_REQUEST_FAILED;
            }
        }
    }

    /**
     * 订单查询
     *
     * @return 查询结果
     */
    private int queryOrder(LoanRemittanceOrderRecordEntity orderRecord, LoanRemittancePaymentRecordEntity paymentRecord, LoanPaymentEntity paymentEntity) {

        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(paymentEntity.getConfig());
        paymentConfig = paymentConfig.getJSONObject(paymentRecord.getConfigTag());
        String appId = paymentConfig.getString(PaymentField.YEAHPAY_APP_ID);
        String appKey = paymentConfig.getString(PaymentField.YEAHPAY_APP_KEY);
        String tokenUrl = paymentConfig.getString(PaymentField.YEAHPAY_TOKENURL);
        String redirectUrl = paymentConfig.getString(PaymentField.YEAHPAY_QUERY_URL);

        // 请求结果
        String response;

        try {
            // 获取token
            String token = getToken(appId, appKey, tokenUrl);

            // 拼接Get请求url
            redirectUrl += (redirectUrl.endsWith("/") ? "" : "/") + paymentRecord.getId();

            // 请求
            response = HttpRequest.get(redirectUrl)
                    .header(PaymentField.YEAHPAY_AUTHORIZATION, PaymentField.YEAHPAY_BEARER + token)
                    .header("Content-Type", "application/json")
                    .execute()
                    .body();

            // 更新查询请求响应报文
            updateLoanRemittancePaymentRecordSearchLog(paymentRecord.getId(), paymentRecord.getId(), response);
        } catch (Exception e) {
            // 请求异常
            LogUtil.sysError("[YeahPay]", e);
            updateLoanRemittancePaymentRecordSearchLog(paymentRecord.getId(), paymentRecord.getId(), "异常");
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 判断是结果集是否为空
        if (StringUtils.isEmpty(response)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 转换为Json
        JSONObject returnObject = JSONObject.parseObject(response);

        // 处理接口返回信息
        String code = returnObject.getString(Field.CODE);

        // 判断接口状态
        if (!PaymentField.YEAHPAY_SUCCESS_CODE_VAL.equals(code)) {
            // 请求异常
            return PaymentField.PAYOUT_QUERY_ERROR;
        }

        // 返回数据
        JSONObject data = returnObject.getJSONObject(PaymentField.YEAHPAY_RESULT);

        // 失败
        if (PaymentField.YEAHPAY_FAILED_VAL1.equals(data.getString(PaymentField.YEAHPAY_RESULT_STATUS)) || PaymentField.YEAHPAY_FAILED_VAL2.equals(data.getString(PaymentField.YEAHPAY_RESULT_STATUS))) {
            // 放款失败
            return PaymentField.PAYOUT_FAILED;
        } else if (PaymentField.YEAHPAY_SUCCESS_VAL.equals(data.getString(PaymentField.YEAHPAY_RESULT_STATUS))) {
            // 放款成功
            return PaymentField.PAYOUT_SUCCESS;
        } else {
            // 其他状态 进行中
            return PaymentField.PAYOUT_PROCESS;
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
            // Authorization
            String authorization = PaymentField.YEAHPAY_BASIC + Base64Encoder.encode(appId + ":" + appKey);

            // 请求
            String response;
            try {
                response = HttpRequest.post(url)
                        .header(PaymentField.YEAHPAY_AUTHORIZATION, authorization)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .body(PaymentField.YEAHPAY_TOKEN_PARAM)
                        .execute()
                        .body();
                JSONObject jsonObject = JSONObject.parseObject(response);
                String accessToken = jsonObject.getString("access_token");
                Long expiresIn = jsonObject.getLong("expires_in");

                token = new YeahPayToken();
                token.setAccessToken(accessToken);
                token.setExpirationTimestmp(System.currentTimeMillis() +  1000 * 60 * 60 * 24);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return token.getAccessToken();
    }
    /**
     * 发起代付
     */
    public String request(YeahPayParam yeahPayParam, String url, String token) {
        // 请求
        String response = null;
        try {
            response = HttpRequest.post(url)
                    .header(PaymentField.YEAHPAY_AUTHORIZATION, PaymentField.YEAHPAY_BEARER + token)
                    .header("Content-Type", "application/json")
                    .body(JSONObject.toJSONString(yeahPayParam))
                    .execute()
                    .body();
        } catch (Exception e) {
            LogUtil.sysError("[YeahPay]", e);
        }

        return response;
    }

    /**
     * 根据权重挑选YeahPay账号
     *
     * @param config     yeahPay渠道配置信息
     * @param removeTags 不参与挑选的key
     */
    private String chooseAccountByWeight(JSONObject config, List<String> removeTags) {
        // 配置解析
        Set<String> strings = config.keySet();
        List<JSONObject> list = new ArrayList<>();
        strings.forEach(key -> {
            JSONObject jsonObject = config.getJSONObject(key);
            if (!removeTags.contains(jsonObject.getString(PaymentField.YEAHPAY_NAME))) {
                list.add(jsonObject);
            }
        });

        // 权重和
        String res = null;

        // 查询渠道列表
        // 随机范围 = 账号权重和
        int range = list.stream().mapToInt(jsonObject -> jsonObject.getInteger("weight")).sum();
        if (range == 0) {
            return null;
        }
        // 取随机数
        Random random = new Random();
        int randomNum = random.nextInt(range) + 1;

        // 选择渠道
        int start = 0;
        for (JSONObject entity : list) {
            Integer weight = entity.getInteger("weight");
            if (randomNum > start && randomNum <= (start + weight)) {
                res = entity.getString("name");
                break;
            } else {
                start += weight;
            }
        }
        return res;
    }

    /**
     * 根据权重挑选YeahPay账号
     *
     * @param config yeahPay渠道配置信息
     */
    private String chooseAccountByWeight(JSONObject config) {
        return chooseAccountByWeight(config, new ArrayList<>());
    }
}