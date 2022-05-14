package com.epoch.loan.workshop.account.repayment.panda;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.account.repayment.BaseRepayment;
import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.constant.PaymentField;
import com.epoch.loan.workshop.common.dao.mysql.LoanOrderDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanUserInfoDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.common.params.params.request.PreRepaymentParams;
import com.epoch.loan.workshop.common.util.AppDomainUtil;
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
public class PandaPay extends BaseRepayment {
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

    public String speiRepayment(LoanRepaymentPaymentRecordEntity record, LoanPaymentEntity payment) {
        String orderId = record.getOrderId();
        LoanUserInfoEntity loanUserInfo = loanUserInfoDao.findUserInfoByOrderId(orderId);

        String clabe = "";
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(payment.getConfig());
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);
        String url = paymentConfig.getString(PaymentField.PANDAPAY_PAY_URL);

        // 参数封装
        PandaPayParams params = new PandaPayParams();
        params.setExternalId(record.getId());
        params.setName(record.getName());
        params.setEmail(record.getEmail());
        params.setPhone(record.getPhone());
        params.setRfc(loanUserInfo.getRfc());
        params.setCurp(loanUserInfo.getPapersId());
        Map<String,String> header = new HashMap<>();
        header.put("Encoding","UTF-8");
        header.put("Content-Type","application/json");
        header.put("Authorization", sign(JSONObject.toJSONString(params), key));
        header.put("AppId", appId);
        // 发起请求
        String result;
        try {
            result = HttpUtils.simplePostInvoke(url, JSONObject.toJSONString(params), header);
            LogUtil.sysInfo("result : {}", result);
        } catch (Exception e) {
            LogUtil.sysError("[PandaPay]", e);
            // 请求失败
            updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
            return null;
        }

        // 更新请求响应数据
        updatePaymentRecordRequestAndResponse(record.getId(), JSONObject.toJSONString(params), result);

        // 结果集判空
        if (StringUtils.isEmpty(result)) {
            // 请求失败
            updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
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
                updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                return null;
            } else{
                // 发起成功 修改状态
                updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.PROCESS);
                // 存储支付方订单号
                updatePamentRecordBussinesId(record.getId(), payOrderId);
                // 存储clabe和条形码
                updatePaymentRecordClabeAndBarCode(record.getId(),clabe,null);
                // 发送到队列
                RepaymentParams repaymentParams = new RepaymentParams();
                repaymentParams.setId(record.getId());
                repaymentMQManager.sendMessage(repaymentParams, payment.getName());

                // 拼接H5路径
                LoanOrderEntity order = loanOrderDao.findOrder(record.getOrderId());
                return AppDomainUtil.splicingPandapaySPEIRepaymentH5Url(order.getAppName(), record.getId());
            }
        } catch (Exception e) {
            LogUtil.sysError("[repayment pandaPay]", e);
        }

        return clabe;
    }

    public String oxxoRepayment(LoanRepaymentPaymentRecordEntity record, LoanPaymentEntity payment) {
        String barcodeUrl = "";
        // 获取渠道配置信息
        JSONObject paymentConfig = JSONObject.parseObject(payment.getConfig());
        String appId = paymentConfig.getString(PaymentField.PANDAPAY_APPID);
        String key = paymentConfig.getString(PaymentField.PANDAPAY_KEY);
        String paymentSourcesType = paymentConfig.getString(PaymentField.PANDAPAY_PAYMENT_SOURCES_TYPE);
        String cpmpanyId = paymentConfig.getString(PaymentField.PANDAPAY_PAYMENT_COMPANY_ID);
        String url = paymentConfig.getString(PaymentField.PANDAPAY_OXXO_PAY_URL);

        // 参数封装
        OxxoPandaPayParams params = new OxxoPandaPayParams();
        List<JSONObject> paymentSources = new ArrayList<>();
        JSONObject metadata = new JSONObject();
        metadata.put("company_id", cpmpanyId);
        metadata.put("external_id", record.getId());
        JSONObject source = new JSONObject();
        source.put("type",paymentSourcesType);
        paymentSources.add(source);
        params.setName(record.getName());
        params.setEmail(record.getEmail());
        params.setPhone(record.getPhone());
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
//            {"transactionId":"0208603cabda44789a8cf3c53753d1dd","resultado":{"result":{"metadata":{"company_id":"P001","external_id":"P0016215e5265cf805ef0bd1e615"},"livemode":false,"phone":"9012000002","corporate":false,"name":"Mr  GAJENDRA RAVINDR","created_at":1651202265,"id":"cus_2ripD7xN3ob3kY7Vo","email":"cvvb@qq.com","custom_reference":"","payment_sources":{"total":1,"data":[{"reference":"99000003834928","barcode_url":"https://s3.amazonaws.com/cash_payment_barcodes/sandbox_reference.png","expires_at":0,"provider":"Oxxo","parent_id":"cus_2ripD7xN3ob3kY7Vo","created_at":1651202265,"id":"off_ref_2ripD7xN3ob3kY7Vp","type":"oxxo_recurrent","barcode":"99000003834928","object":"payment_source"}],"has_more":false,"object":"list"},"object":"customer"}}}
            LogUtil.sysInfo("result : {}", result);
        } catch (Exception e) {
            LogUtil.sysError("[oxxoPandaPay]", e);
            // 请求失败
            updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
            return null;
        }

        // 更新请求响应数据
        updatePaymentRecordRequestAndResponse(record.getId(), JSONObject.toJSONString(params), result);

        // 结果集判空
        if (StringUtils.isEmpty(result)) {
            // 请求失败
            updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
            return null;
        }

        try {
            // 转换为Json
            JSONObject returnObject = JSONObject.parseObject(result);
            String descripcionError = returnObject.getJSONObject("resultado").getString(PaymentField.PANDAPAY_DESCRIPCION_ERROR);
            String payOrderId = returnObject.getString(PaymentField.PANDAPAY_TRANSACTIONID);
            if (ObjectUtils.isNotEmpty(descripcionError)) {
                // 请求失败
                updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.FAILED);
                return null;
            } else{
                JSONArray data = returnObject.getJSONObject("resultado").getJSONObject("result").getJSONObject("payment_sources").getJSONArray("data");
                List<JSONObject> dataJson = data.toJavaList(JSONObject.class);
                barcodeUrl = dataJson.get(0).getString("barcode_url");
                String clabe = dataJson.get(0).getString("barcode");
                // 发起成功 修改状态
                updatePaymentRecordStatus(record.getId(), LoanRepaymentPaymentRecordStatus.PROCESS);
                // 存储支付方订单号
                updatePamentRecordBussinesId(record.getId(), payOrderId);
                // 存储clabe和条形码
                updatePaymentRecordClabeAndBarCode(record.getId(), clabe, barcodeUrl);
                // 发送到队列
                RepaymentParams repaymentParams = new RepaymentParams();
                repaymentParams.setId(record.getId());
                repaymentMQManager.sendMessage(repaymentParams, payment.getName());

                // 拼接H5路径
                LoanOrderEntity order = loanOrderDao.findOrder(record.getOrderId());
                return AppDomainUtil.splicingPandapayOXXORepaymentH5Url(order.getAppName(), record.getId());
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