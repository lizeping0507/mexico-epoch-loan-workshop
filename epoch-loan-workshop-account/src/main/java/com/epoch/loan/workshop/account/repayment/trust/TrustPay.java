package com.epoch.loan.workshop.account.repayment.trust;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.account.repayment.BaseRepayment;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.trust;
 * @className : InPay
 * @createTime : 2022/4/12 14:05
 * @description : TrustPay代收发起
 */
@Component("TrustPay")
public class TrustPay extends BaseRepayment {

    /**
     * 参数签名
     * 非空参数值的参数按照参数名ASCII码从小到大排序
     * 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串
     * 在上述字符串最后拼接上key(即stringA&key=value)并进行MD5运算，再将得到的字符串所有字符转换为小写
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

    /**
     * 发起代收
     *
     * @param loanRepaymentPaymentRecordEntity 支付详情
     * @param payment                          支付渠道
     * @return 付款页面
     */
    @Override
    public String startRepayment(LoanRepaymentPaymentRecordEntity loanRepaymentPaymentRecordEntity, LoanPaymentEntity payment) {
        String payUrl = "";
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(payment.getConfig());
        String merchantId = paymentConfig.getString(PaymentField.TRUSTPAY_MERCHANT_ID);
        String key = paymentConfig.getString(PaymentField.TRUSTPAY_KEY);
        String url = paymentConfig.getString(PaymentField.TRUSTPAY_PAY_URL);
        String notifyUrl = paymentConfig.getString(PaymentField.TRUSTPAY_NOTIFY_URL);
        String callbackUrl = paymentConfig.getString(PaymentField.TRUSTPAY_CALLBACK_URL);
        // 参数封装
        TrustPayParams params = new TrustPayParams();
        params.setMerchant(merchantId);
        params.setOrderId(loanRepaymentPaymentRecordEntity.getId());
        params.setAmount(NumberUtil.roundStr(loanRepaymentPaymentRecordEntity.getAmount(), 2));
        params.setCustomName(loanRepaymentPaymentRecordEntity.getName());
        params.setCustomMobile(loanRepaymentPaymentRecordEntity.getPhone());
        params.setCustomEmail(loanRepaymentPaymentRecordEntity.getEmail());
        params.setNotifyUrl(notifyUrl);
        params.setCallbackUrl(callbackUrl);
        params.setSign(sign(params, key));
        // 发起请求
        String result;
        try {
            result = HttpUtils.POST_FORM(url, JSONObject.toJSONString(params));
            LogUtil.sysInfo("result : {}", result);
        } catch (Exception e) {
            LogUtil.sysError("[TrustPay]", e);
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
            Integer code = returnObject.getInteger(PaymentField.TRUSTPAY_CODE);
            if (!PaymentField.TRUSTPAY_SUCCESS_CODE_VAL.equals(code)) {
                // 请求失败
                updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                return null;
            }

            // 返回数据
            JSONObject data = returnObject.getJSONObject(PaymentField.TRUSTPAY_DATA);
            String payOrderId = data.getString(PaymentField.TRUSTPAY_ORDER_NUMBER);
            payUrl = data.getString(PaymentField.TRUSTPAY_URL);

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
            LogUtil.sysError("[repayment trustPay]", e);
        }

        return payUrl;
    }
}