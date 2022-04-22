package com.epoch.loan.workshop.account.repayment.incash;

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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.incash;
 * @className : InCashPay
 * @createTime : 2022/4/12 14:05
 * @description : InCashPay代收发起
 */
@Component("InCashPay")
public class InCashPay extends BaseRepayment {

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
            if (ObjectUtils.isNotEmpty(value)) {
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
        String merchantId = paymentConfig.getString(PaymentField.INCASHPAY_MERCHANT_ID);
        String key = paymentConfig.getString(PaymentField.INCASHPAY_KEY);
        String url = paymentConfig.getString(PaymentField.INCASHPAY_PAYIN_URL);
        String notifyUrl = paymentConfig.getString(PaymentField.INCASHPAY_NOTIFY_URL);
        String callbackUrl = paymentConfig.getString(PaymentField.CALLBACK_URL);

        // 参数封装
        InCashPayParams params = new InCashPayParams();
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
            result = HttpUtils.POST(url, JSONObject.toJSONString(params));
            LogUtil.sysInfo("result : {}", result);
        } catch (Exception e) {
            LogUtil.sysError("[InCashPay]", e);
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
            String code = returnObject.getString(PaymentField.INCASHPAY_CODE);
            if (!PaymentField.INCASHPAY_SUCCESS_CODE_VAL.equals(code)) {
                // 请求失败
                updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                return null;
            }

            // 返回数据
            JSONObject data = returnObject.getJSONObject(PaymentField.INCASHPAY_DATA);
            String payOrderId = data.getString(PaymentField.INCASGPAY_ORDER_NUMBER);
            payUrl = data.getString(PaymentField.INCASHPAY_URL);

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
            LogUtil.sysError("[repayment incashPay]", e);
        }

        return payUrl;
    }
}