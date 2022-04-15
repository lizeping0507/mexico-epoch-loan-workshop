package com.epoch.loan.workshop.account.repayment.yeah;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.account.repayment.BaseRepayment;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.repayment;
 * @className : YeahPay1
 * @createTime : 2022/3/7 19:04
 * @description : Yeah代收发起
 */
@Component("YeahPay1")
public class YeahPay1 extends BaseRepayment {

    private YeahPayToken token;

    /**
     * 发起代收
     *
     * @param loanRepaymentPaymentRecordEntity 支付详情
     * @param payment                          支付渠道
     * @return 付款页面
     */
    @Override
    public String startRepayment(LoanRepaymentPaymentRecordEntity loanRepaymentPaymentRecordEntity, LoanPaymentEntity payment) {
        String payUrl = null;
        DecimalFormat df = new DecimalFormat("0.0000");
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(payment.getConfig());
        String appId = paymentConfig.getString(PaymentField.YEAHPAY_APP_ID);
        String appKey = paymentConfig.getString(PaymentField.YEAHPAY_APP_KEY);
        String url = paymentConfig.getString(PaymentField.YEAHPAY_PAY_URL);
        String tokenUrl = paymentConfig.getString(PaymentField.YEAHPAY_TOKENURL);
        String notifyUrl = paymentConfig.getString(PaymentField.YEAHPAY_NOTIFYURL);
        String merchantId = paymentConfig.getString(PaymentField.YEAHPAY_MERCHANTID);

        // 参数封装
        YeahPayParams params = new YeahPayParams();
        JSONObject ext = new JSONObject();
        ext.put(PaymentField.YEAHPAY_ADDRCITY, PaymentField.YEAHPAY_ADDRCITY);
        ext.put(PaymentField.YEAHPAY_ADDRSTREET, PaymentField.YEAHPAY_ADDRSTREET);
        ext.put(PaymentField.YEAHPAY_ADDRNUMBER, PaymentField.YEAHPAY_ADDRNUMBER);
        params.setExt(ext);
        double amount = loanRepaymentPaymentRecordEntity.getAmount();
        params.setAmount(df.format(amount));
        params.setMerchantID(merchantId);
        params.setPayType("6");
        params.setMerchantOrderId(loanRepaymentPaymentRecordEntity.getId());
        params.setProductName(PaymentField.YEAHPAY_PRODUCT);
        params.setProductDescription(PaymentField.YEAHPAY_PRODUCT);
        params.setMerchantUserId(loanRepaymentPaymentRecordEntity.getPhone());
        params.setMerchantUserName(loanRepaymentPaymentRecordEntity.getName());
        params.setMerchantUserEmail(loanRepaymentPaymentRecordEntity.getEmail());
        params.setMerchantUserCitizenId(loanRepaymentPaymentRecordEntity.getPhone());
        params.setMerchantUserDeviceId(loanRepaymentPaymentRecordEntity.getPhone());
        params.setMerchantUserIp("");
        params.setCountryCode(PaymentField.YEAHPAY_COUNTRY_CODE);
        params.setCurrency(PaymentField.YEAHPAY_CURRENCY);
        params.setMerchantUserPhone(loanRepaymentPaymentRecordEntity.getPhone());
        params.setRedirectUrl(notifyUrl);
        params.setSign(sign(params, appKey));

        // 发起请求
        String result;
        try {
            // 获取token
            String token = getToken(appId, appKey, tokenUrl);
            LogUtil.sysInfo("token : {}", token);

            // 请求头
            Map<String, String> headers = new HashMap<>(2);
            headers.put(PaymentField.YEAHPAY_AUTHORIZATION, PaymentField.YEAHPAY_BEARER + token);

            result = HttpUtils.POST_WITH_HEADER(url, JSONObject.toJSONString(params), headers);
            LogUtil.sysInfo("result : {}", result);
        } catch (Exception e) {
            LogUtil.sysError("[YeahPay1]", e);
            // 请求失败
            updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
            return null;
        }

        // 更新请求响应数据
        updatePaymentRecordRequestAndResponse(loanRepaymentPaymentRecordEntity.getId(), JSONObject.toJSONString(params), result);

        // 结果集判空
        if (StringUtils.isEmpty(result)) {
            // 请求失败
            updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
            return null;
        }

        try {
            // 转换为Json
            JSONObject returnObject = JSONObject.parseObject(result);
            String code = returnObject.getString(PaymentField.YEAHPAY_ERRORCODE);
            if (!"1000".equals(code)) {
                // 请求失败
                updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                return null;
            }

            // 返回数据
            JSONObject data = returnObject.getJSONObject(PaymentField.YEAHPAY_ORDERPAYMENTLOAD);
            String payOrderId = data.getJSONObject("payorder").getString("id");
            payUrl = data.getString(PaymentField.YEAHPAY_CHECKPAGEURL);

            if (StringUtils.isNotEmpty(payUrl)) {
                // 发起成功 修改状态
                updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.PROCESS);

                // 存储支付方订单号
                updatePamentRecordBussinesId(loanRepaymentPaymentRecordEntity.getId(), payOrderId);

                // 发送到队列
                RepaymentParams repaymentParams = new RepaymentParams();
                repaymentParams.setId(loanRepaymentPaymentRecordEntity.getId());
                repaymentMQManager.sendMessage(repaymentParams, payment.getName());
            }
        } catch (Exception e) {
            LogUtil.sysError("[repayment yeahpay1]", e);
        }

        return payUrl;
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

    /**
     * 数据签名
     *
     * @param yeahPayParams JSONObject
     * @param signKey       apiKey
     */
    public static String sign(YeahPayParams yeahPayParams, String signKey) {
        String JSONString = JSONObject.toJSONString(yeahPayParams);
        JSONObject params = JSONObject.parseObject(JSONString);


        LinkedList<String> paramNameList = new LinkedList<>();
        for (String key : params.keySet()) {
            if (!"sign".equals(key) && !"ext".equals(key)) {
                paramNameList.add(key);
            }
        }
        Collections.sort(paramNameList);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < paramNameList.size(); i++) {
            String name = paramNameList.get(i);
            stringBuilder.append(params.get(name));
        }
        try {
            return HMACSHA256(stringBuilder.toString() + signKey, signKey, true);
        } catch (Exception e) {
            LogUtil.sysError("", e);
            return null;
        }
    }

    /**
     * HMACSHA256算法生成校验信息
     */
    public static String HMACSHA256(String data, String key, Boolean toUpperCase) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        if (toUpperCase) {
            return sb.toString().toUpperCase();
        } else {
            return sb.toString().toLowerCase();
        }
    }
}
