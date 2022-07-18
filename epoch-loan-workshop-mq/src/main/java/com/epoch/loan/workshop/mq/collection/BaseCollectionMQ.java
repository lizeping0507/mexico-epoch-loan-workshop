package com.epoch.loan.workshop.mq.collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.epoch.loan.workshop.common.config.ReactConfig;
import com.epoch.loan.workshop.common.config.RiskConfig;
import com.epoch.loan.workshop.common.constant.CollectionField;
import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanProductEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanProductExtEntity;
import com.epoch.loan.workshop.common.mq.collection.CollectionMQManager;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.oss.LoanOssClient;
import com.epoch.loan.workshop.common.minio.LoanMinIoClient;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.MD5Utils;
import com.epoch.loan.workshop.mq.collection.repay.CollectionRepaymentPlanParam;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.collection
 * @className : BaseCollectionMQ
 * @createTime : 2022/2/27 16:49
 * @description : 订单催收基类
 */
@RefreshScope
@Component
public abstract class BaseCollectionMQ {

    /**
     * 催收、提还队列管理
     */
    @Autowired
    private CollectionMQManager collectionMQManager;

    /**
     * 还款支付记录
     */
    @Autowired
    public LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;

    /**
     * 订单账单
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;

    /**
     * 订单
     */
    @Autowired
    public LoanOrderDao loanOrderDao;

    /**
     * 产品
     */
    @Autowired
    public LoanProductDao loanProductDao;

    /**
     * 产品扩展
     */
    @Autowired
    public LoanProductExtDao loanProductExtDao;

    /**
     * 风控配置
     */
    @Autowired
    public RiskConfig riskConfig;

    /**
     * 用户信息
     */
    @Autowired
    public LoanUserInfoDao loanUserInfoDao;

    /**
     * 用户银行卡信息
     */
    @Autowired
    public LoanRemittanceAccountDao loanRemittanceAccountDao;

    /**
     * OSS
     */
    @Autowired
    public LoanOssClient loanOssClient;

    /**
     * minio
     */
    @Autowired
    public LoanMinIoClient loanMiNioClient;
    /**
     * 催收提还配置
     */
    @Autowired
    public ReactConfig reactConfig;

    /**
     * 获取子类消息监听
     */
    protected abstract MessageListenerConcurrently getMessageListener();

    /**
     * 获取标签
     *
     * @return
     */
    public String subExpression() {
        return getMessageListener().getClass().getSimpleName();
    }


    /**
     * 消费任务启动
     */
    public void start() throws Exception {
        // 获取子类
        MessageListenerConcurrently messageListenerConcurrently = getMessageListener();

        // 启动队列
        collectionMQManager.consumer(messageListenerConcurrently, subExpression());
    }


    /**
     * 重回催收队列
     *
     * @param params 队列参数
     * @param tag    标签
     * @throws Exception E
     */
    public void retryCollection(CollectionParams params, String tag) throws Exception {
        // 重入放款队列
        collectionMQManager.sendMessage(params, tag, 60);
    }

    /**
     * 获取消息内容
     *
     * @param message
     * @return
     */
    public CollectionParams getMessage(Message message) throws Exception {
        // 获取消息
        byte[] body = message.getBody();
        if (ObjectUtils.isEmpty(body)) {
            throw new Exception("Message is Null");
        }

        // 转为String
        String res = new String(body);
        try {
            // 将JSON解析为对象
            return JSONObject.parseObject(res, CollectionParams.class);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 封装订单的还款计划
     *
     * @param orderEntity 订单实体类
     * @return 还款计划
     */
    public List<CollectionRepaymentPlanParam> getRepaymentPlan(LoanOrderEntity orderEntity) {
        List<LoanOrderBillEntity> orderBillEntityList = loanOrderBillDao.findOrderBillByOrderId(orderEntity.getId());
        List<CollectionRepaymentPlanParam> repaymentPlanList = new ArrayList<>();

        orderBillEntityList.forEach(orderBill -> {
            CollectionRepaymentPlanParam repaymentPlan = new CollectionRepaymentPlanParam();
            repaymentPlan.setOrderNo(orderBill.getOrderId());
            repaymentPlan.setThirdRepayPlanId(orderBill.getId());
            repaymentPlan.setFundType(1);
            repaymentPlan.setBillAmount(orderBill.getRepaymentAmount());
            if (ObjectUtils.isNotEmpty(orderEntity.getActualRepaymentAmount())) {
                repaymentPlan.setReturnedAmount(orderEntity.getActualRepaymentAmount());
            } else {
                repaymentPlan.setReturnedAmount(0.0);
            }

            if (orderBill.getStatus() == OrderBillStatus.COMPLETE
                    || orderBill.getStatus() == OrderBillStatus.DUE_COMPLETE) {
                repaymentPlan.setRepaymentPlanStatus(10);
            } else {
                repaymentPlan.setRepaymentPlanStatus(20);
            }
            repaymentPlan.setShouldRepayTime(orderBill.getRepaymentTime());
            repaymentPlan.setRepayTime(orderBill.getActualRepaymentTime());
            repaymentPlanList.add(repaymentPlan);
        });
        return repaymentPlanList;
    }

    /**
     * 封装请求参数
     *
     * @param product  产品信息
     * @param productExt  产品扩展信息
     * @param pushType push-order--推送订单   push-repay--推送还款  sync-overdue-fee--同步逾期
     * @param argMaps  请求携带参数 推送订单--CollectionPushOrderParam  还款--CollectionRepayParam 同步逾期--CollectionOverdueParam
     * @return 请求参数JSONString
     */
    public String getRequestParam(LoanProductEntity product, LoanProductExtEntity productExt, String pushType, Object argMaps) {

        // 参数封装
        CollectionBaseParam<String> param = new CollectionBaseParam<>();
        String argParam = JSON.toJSONString(argMaps, SerializerFeature.DisableCircularReferenceDetect);
        param.setArgs(argParam);

        param.setCall(pushType);

        String productName = getProductName(product.getProductName());
        param.setOrgId(productName);

        String sign = getSign(productName, productExt.getProductKey(), pushType, argParam);
        param.setSign(sign);

        return JSON.toJSONString(param, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 发送请求
     *
     * @param requestParam 请求参数
     * @param reactType    0--不推送  1--只推送提还系统    2-- 只推送催收系统  3--推送提还和催收
     * @return 1--推送成功  0--推送失败
     */
    public Integer push(String requestParam, Integer reactType) {

        // 推送结果
        int res = CollectionField.PUSH_SUCCESS;

        String result;
        try {
            if (reactType == CollectionField.PUSH_REACT || reactType == CollectionField.PUSH_ALL) {
                String reactUrl = reactConfig.getReactUrl();
                result = HttpUtils.simplePostInvoke(reactUrl, requestParam, HttpUtils.CONTENT_CHARSET);
                JSONObject resultObject = JSONObject.parseObject(result);
                if (StringUtils.isBlank(result)) {
                    res = CollectionField.PUSH_FAILED;
                    return res;
                }
                // 结果解析
                if (resultObject.getInteger(CollectionField.RES_CODE) != HttpStatus.SC_OK) {
                    res = CollectionField.PUSH_FAILED;
                    return res;
                }
            }

            if (reactType == CollectionField.PUSH_REMIND || reactType == CollectionField.PUSH_ALL) {
                String remindUrl = reactConfig.getRemindUrl();
                result = HttpUtils.simplePostInvoke(remindUrl, requestParam, HttpUtils.CONTENT_CHARSET);
                if (StringUtils.isBlank(result)) {
                    res = CollectionField.PUSH_FAILED;
                    return res;
                }
                JSONObject resultRemind = JSONObject.parseObject(result);
                if (resultRemind.getInteger(CollectionField.RES_CODE) != HttpStatus.SC_OK) {
                    res = CollectionField.PUSH_FAILED;
                }
            }
        } catch (Exception e) {
            res = CollectionField.PUSH_FAILED;
            LogUtil.sysError("[Collection push]", e);
        }
        return res;
    }



    /**
     * 获取催收、提还系统的验签码
     *
     * @param productName 产品名称
     * @param key         产品验签码(每个产品对应的均不相同)
     * @param call        push-order--推送订单   push-repay--推送还款  sync-overdue-fee--同步逾期
     * @param paramJson   验签参数
     * @return 验签值
     */
    public static String getSign(String productName, String key, String call, String paramJson) {
        String signKey = productName + key + productName;
        String plain = signKey + call + signKey
                + paramJson + signKey;
        return MD5Utils.SunMD5(plain);
    }

    /**
     * 产品名称变换
     *
     * @param productName 产品名称
     * @return 变化后的产品名称
     */
    public static String getProductName(String productName) {
        if (CollectionField.SUPERCOIN.equalsIgnoreCase(productName)
                || CollectionField.SUPERBOX.equalsIgnoreCase(productName)
                || CollectionField.FORTUNETREE.equalsIgnoreCase(productName)) {
            productName = productName.toLowerCase();
        } else if (CollectionField.AC_LOAN.equalsIgnoreCase(productName)) {
            productName = CollectionField.ACLOAN;
        }
        return productName;
    }

}
