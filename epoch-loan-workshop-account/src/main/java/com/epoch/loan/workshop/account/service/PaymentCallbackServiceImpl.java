package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.params.params.request.*;
import com.epoch.loan.workshop.common.service.PaymentCallbackService;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
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
    public String pandaPay(PandaPayCallBackParams params) throws Exception {
        // 获取Id
        String id = params.getId();

        // 返库订单详情
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getById(id);
        // LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getByBusinessId(id);

        // 发送队列
        String queueParam = paymentRecord.getQueueParam();
        RemittanceParams remittanceParams;
        if (StringUtils.isNotEmpty(queueParam)) {
            remittanceParams = JSONObject.parseObject(queueParam, RemittanceParams.class);
        } else {
            remittanceParams = new RemittanceParams();
            remittanceParams.setGroupName("Weight");
            remittanceParams.setId(paymentRecord.getId());
        }
        remittanceMQManagerProduct.sendMessage(remittanceParams, remittanceMQManagerProduct.getInPaySubExpression());

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
        if (null != paymentRecord) {
            return true;
        }
        return false;
    }
}
