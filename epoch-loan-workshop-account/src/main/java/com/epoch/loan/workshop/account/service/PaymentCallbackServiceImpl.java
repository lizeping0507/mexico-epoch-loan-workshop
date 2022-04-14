package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.entity.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.service.PaymentCallbackService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.account.service;
 * @className : TestServiceImpl
 * @createTime : 2022/2/10 16:42
 * @description : 支付回调处理
 */
@DubboService(timeout = 5000)
public class PaymentCallbackServiceImpl extends BaseService implements PaymentCallbackService {
    /**
     * yeahPay支付回调处理
     *
     * @param params yeahPay支付回调参数
     * @return String
     */
    @Override
    public String yeahPay(YeahPayCallBackParams params) throws Exception {
        // 获取Id
        String id = params.getMerchantPayoutId();

        // 判断队列 (YeahPay多个队列)
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);
        LoanPaymentEntity loanPayment = loanPaymentDao.getById(paymentRecord.getPaymentId());
        String name = loanPayment.getName();

        // 发送到yeahPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, name);

        return "success";
    }

    /**
     * inPay支付回调处理
     *
     * @param params inPay支付回调参数
     * @return String
     */
    @Override
    public String inPay(InPayCallBackParams params) throws Exception {
        // 获取Id
        String id = params.getOut_trade_no();

        // 发送到inPay队列
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到yeahPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getInPaySubExpression());

        return "success";
    }

    /**
     * sunFlowerPay支付回调处理
     *
     * @param params sunFlowerPay支付回调参数
     * @return String
     */
    @Override
    public String sunFlowerPay(SunFlowerPayCallBackParams params) throws Exception {
        // 获取商户订单号
        String id = params.getMerchant_trade_no();

        // 发送到sunFlowerPay队列
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到sunFlowerPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getSunFlowerPaySubExpression());

        return "SUCCESS";
    }

    /**
     * oceanPay支付回调处理
     *
     * @param params oceanPay支付回调参数
     * @return String
     */
    @Override
    public String oceanPay(OceanPayCallBackParams params) throws Exception {
        // 获取商户订单号
        String id = params.getMerissuingcode();

        // 发送到oceanPay队列
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到oceanPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getOceanPaySubExpression());

        return "OK";
    }

    /**
     * acPay支付回调处理
     *
     * @param params acPay支付回调参数
     * @return String
     */
    @Override
    public String acPay(AcPayCallBackParams params) throws Exception {
        // 获取代付单号
        String id = params.getOrderId();

        // 发送到acPay队列 (AcPay回调不传商户订单号)
        // 这儿需要重新写sql获取订单支付记录
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到acPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getAcPaySubExpression());

        return "success";
    }

    /**
     * incashPay支付回调处理
     *
     * @param params incashPay支付回调参数
     * @return String
     */
    @Override
    public String incashPay(IncashPayCallBackParams params) throws Exception {
        // 获取代付单号
        String id = params.getOrderId();
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到incashPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getIncashPaySubExpression());
        return "ok";
    }

    /**
     * incashXjdPay支付回调处理
     *
     * @param params incashXjdPay支付回调参数
     * @return String
     */
    @Override
    public String incashXjdPay(IncashPayCallBackParams params) throws Exception {
        // 获取代付单号
        String id = params.getOrderId();
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到incashXjdPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getIncashXjdPaySubExpression());
        return "ok";
    }

    /**
     * trustPay支付回调处理
     *
     * @param params trustPay支付回调参数
     * @return String
     */
    @Override
    public JSONObject trustPay(TrustPayCallBackParams params) throws Exception {
        // 获取代付单号
        String id = params.getOrderId();
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到trustPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getTrustPaySubExpression());
        JSONObject object = new JSONObject();
        object.put("code", 200);
        object.put("message", "ok");
        return object;
    }
    /**
     * qePay支付回调处理
     *
     * @param params qePay支付回调参数
     * @return String
     */
    @Override
    public String qePay(QePayCallBackParams params) throws Exception {
        // 获取代付单号
        String id = params.getMerTransferId();
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到qePay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getQePaySubExpression());
        return "success";
    }
    /**
     * hrPay支付回调处理
     *
     * @param params hrPay支付回调参数
     * @return String
     */
    @Override
    public String hrPay(HrPayCallBackParams params) throws Exception {
        // 获取代付单号
        String id = params.getOrderid();
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到hrPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getQePaySubExpression());
        return "ok";
    }
    /**
     * globPay支付回调处理
     *
     * @param params globPay支付回调参数
     * @return String
     */
    @Override
    public String globPay(GlobPayCallBackParams params) throws Exception {
        // 获取代付单号
        String id = params.getMchOrderNo();
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);

        // 发送到hrPay队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getGlobPaySubExpression());
        return "success";
    }

    /**
     * 检查订单是否存在
     *
     * @param poutId
     * @return
     */
    @Override
    public boolean checkOrder(String poutId) {
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.findById(poutId);
        if (null != paymentRecord ){
            return true;
        }
        return false;
    }
}
