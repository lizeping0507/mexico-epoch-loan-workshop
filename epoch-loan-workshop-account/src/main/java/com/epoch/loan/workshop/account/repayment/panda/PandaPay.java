package com.epoch.loan.workshop.account.repayment.panda;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.account.repayment.BaseRepayment;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.common.params.params.request.PreRepaymentParams;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.panda;
 * @className : PandaPay
 * @createTime : 2022/4/25
 * @description : PandaPay代收发起
 */
@Component("PandaPay")
public class PandaPay extends BaseRepayment{

    /**
     * 发起代收
     *
     * @param loanRepaymentPaymentRecordEntity 支付详情
     * @param payment                          支付渠道
     * @param params
     * @return 付款页面
     */
    @Override
    public String startRepayment(LoanRepaymentPaymentRecordEntity loanRepaymentPaymentRecordEntity,
                                 LoanPaymentEntity payment,
                                 PreRepaymentParams params) {
        // 获取支付方式
        String paymentType = params.getPayType();
        String result = null;

        if(PaymentField.PAY_TYPE_SPEI.equals(paymentType)){
            result = speiRepayment(loanRepaymentPaymentRecordEntity, payment);
        }else if(PaymentField.PAY_TYPE_OXXO.equals(paymentType)){
            result = oxxoRepayment(loanRepaymentPaymentRecordEntity, payment);
        }

        return result;
    }

    public String speiRepayment(LoanRepaymentPaymentRecordEntity loanRepaymentPaymentRecordEntity, LoanPaymentEntity payment) {
        String clabe = "";
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(payment.getConfig());
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);
        String url = paymentConfig.getString(PaymentField.PANDAPAY_PAY_URL);
        String notifyUrl = paymentConfig.getString(PaymentField.PANDAPAY_NOTIFY_URL);

        // 参数封装
        PandaPayParams params = new PandaPayParams();
        params.setExternalId(loanRepaymentPaymentRecordEntity.getId());
        params.setName(loanRepaymentPaymentRecordEntity.getName());
        params.setEmail(loanRepaymentPaymentRecordEntity.getEmail());
        params.setPhone(loanRepaymentPaymentRecordEntity.getPhone());
        params.setRfc("LSI210621Q16");
        params.setCurp("TOVM740205HDFRLR01");
        Map<String,String> header = new HashMap<>();
        header.put("Encoding","UTF-8");
        header.put("Content-Type","application/json");
        header.put("Authorization", sign(JSONObject.toJSONString(params), key));
        header.put("AppId", appId);
        // 发起请求
        String result;
        try {
            result = HttpUtils.simplePostInvoke(url, JSONObject.toJSONString(params), header);
//            {"transactionId":"f89846d1a618416c9442f5c7864777bf","resultado":{"clabe":"646180130900000011","externalId":"P0016215e5265cf805ef0bd1e614"}}
//            {"transactionId":"9c87a5716b204a6fab7f323ddec7d0b5","resultado":{"clabe":"646180130900000011","externalId":"P0016215e5265cf805ef0bd1e614"}}
            LogUtil.sysInfo("result : {}", result);
        } catch (Exception e) {
            LogUtil.sysError("[PandaPay]", e);
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
            JSONObject resultado = returnObject.getJSONObject("resultado");
            clabe = resultado.getString(PaymentField.PANDAPAY_PAY_CLABE);
            String payOrderId = returnObject.getString(PaymentField.PANDAPAY_TRANSACTIONID);
            if (ObjectUtils.isEmpty(clabe)) {
                // 请求失败
                updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                return null;
            } else{
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
            LogUtil.sysError("[repayment pandaPay]", e);
        }

        return clabe;
    }

    public String oxxoRepayment(LoanRepaymentPaymentRecordEntity loanRepaymentPaymentRecordEntity, LoanPaymentEntity payment) {
        String barcodeUrl = "";
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(payment.getConfig());
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);
        String paymentSourcesType = paymentConfig.getString(PaymentField.PANDAPAY_PAYMENT_SOURCES_TYPE);
        String cpmpanyId = paymentConfig.getString(PaymentField.PANDAPAY_PAYMENT_COMPANY_ID);
        String url = paymentConfig.getString(PaymentField.PANDAPAY_OXXO_PAY_URL);
        String notifyUrl = paymentConfig.getString(PaymentField.PANDAPAY_NOTIFY_URL);

        // 参数封装
        OxxoPandaPayParams params = new OxxoPandaPayParams();
        List<JSONObject> paymentSources = new ArrayList<>();
        JSONObject metadata = new JSONObject();
        metadata.put("company_id", cpmpanyId);
        metadata.put("external_id", loanRepaymentPaymentRecordEntity.getId());
        JSONObject source = new JSONObject();
        source.put("type",paymentSourcesType);
        paymentSources.add(source);
        params.setName(loanRepaymentPaymentRecordEntity.getName());
        params.setEmail(loanRepaymentPaymentRecordEntity.getEmail());
        params.setPhone(loanRepaymentPaymentRecordEntity.getPhone());
        params.setPayment_sources(paymentSources);
        params.setMetadata(metadata);
        Map<String,String> header = new HashMap<>();
        header.put("Encoding","UTF-8");
        header.put("Content-Type","application/json");
        header.put("Authorization", sign(JSONObject.toJSONString(params), key));
        header.put("AppId", appId);
        // 发起请求
        String result;
        try {
            result = HttpUtils.simplePostInvoke(url, JSONObject.toJSONString(params), header);
//            {"transactionId":"7813700adad843b199808e3b1b1a8bb3","resultado":{"id":-1,"descripcionError":"invalid token"}}
            LogUtil.sysInfo("result : {}", result);
        } catch (Exception e) {
            LogUtil.sysError("[oxxoPandaPay]", e);
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
            String descripcionError = returnObject.getJSONObject("resultado").getString(PaymentField.PANDAPAY_DESCRIPCION_ERROR);
            String payOrderId = returnObject.getString(PaymentField.PANDAPAY_TRANSACTIONID);
            if (ObjectUtils.isNotEmpty(descripcionError)) {
                // 请求失败
                updatePaymentRecordStatus(loanRepaymentPaymentRecordEntity.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                return null;
            } else{
                JSONArray data = returnObject.getJSONObject("resultado").getJSONObject("result").getJSONObject("payment_sources").getJSONArray("data");
                List<JSONObject> dataJson = data.toJavaList(JSONObject.class);
                barcodeUrl = dataJson.get(0).getString("barcode_url");
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
            LogUtil.sysError("[repayment oxxoPandaPay]", e);
        }

        return barcodeUrl;
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