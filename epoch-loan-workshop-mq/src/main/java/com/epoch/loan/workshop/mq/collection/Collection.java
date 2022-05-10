package com.epoch.loan.workshop.mq.collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyun.oss.model.OSSObject;
import com.epoch.loan.workshop.common.constant.*;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.util.*;
import com.epoch.loan.workshop.mq.collection.order.*;
import com.epoch.loan.workshop.mq.collection.overdue.CollectionOverdueParam;
import com.epoch.loan.workshop.mq.collection.repay.CollectionOrderInfoParam;
import com.epoch.loan.workshop.mq.collection.repay.CollectionRepayParam;
import com.epoch.loan.workshop.mq.collection.repay.CollectionRepayRecordParam;
import com.epoch.loan.workshop.mq.collection.repay.CollectionRepaymentPlanParam;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.collection
 * @className : Collection
 * @createTime : 2022/2/27 16:47
 * @description : 订单催收处理
 */
@RefreshScope
@Component
@Data
public class Collection extends BaseCollectionMQ implements MessageListenerConcurrently {

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 提还推送地址
     */
    @Value("${react.remindUrl}")
    private String remindUrl;

    /**
     * 催收推送地址
     */
    @Value("${react.reactUrl}")
    private String reactUrl;

    /**
     * minio存储用户 照片桶名
     */
    @Value("${react.oss.bucketName.user}")
    private String userFileBucketName;


    /**
     * 阿里云oSS存储用户 照片桶名
     */
    @Value("${oss.bucketName.user}")
    private String userOssBucketName;


    /**
     * 消费任务
     *
     * @param msgs
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        // 循环处理消息
        for (Message msg : msgs) {
            // 消息对象
            CollectionParams collectionParams = null;

            try {
                // 获取消息对象
                collectionParams = getMessage(msg);
                if (ObjectUtils.isEmpty(collectionParams)) {
                    continue;
                }

                // 订单id
                String orderId = collectionParams.getOrderId();

                // 订单账单id
                String orderBillId = collectionParams.getOrderBillId();

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 根据订单账单id查询账单
                LoanOrderBillEntity loanOrderBillEntity = loanOrderBillDao.findOrderBill(orderBillId);
                if (ObjectUtils.isEmpty(loanOrderBillEntity)) {
                    continue;
                }

                // 产品过滤
                LoanProductEntity product = loanProductDao.findProduct(loanOrderEntity.getProductId());
                if (ObjectUtils.isEmpty(product) || ObjectUtils.isEmpty(product.getReactType())
                        || CollectionField.NO_PUSH == product.getReactType()) {
                    continue;
                }

                // 判断订单事件
                if (collectionParams.getCollectionEvent() == CollectionField.EVENT_CREATE) {

                    // 订单成功放款-在催收提环系统创建案件
                    int pushRes = pushOrder(loanOrderEntity, product);
                    if (CollectionField.PUSH_SUCCESS != pushRes) {
                        retryCollection(collectionParams, subExpression());
                    }
                } else if (collectionParams.getCollectionEvent() == CollectionField.EVENT_COMPLETE) {

                    // 订单还款-在催收提环系统更新案件
                    int pushRes = pushRepay(loanOrderEntity, product, loanOrderBillEntity);
                    if (CollectionField.PUSH_SUCCESS != pushRes) {
                        retryCollection(collectionParams, subExpression());
                    }
                } else if (collectionParams.getCollectionEvent() == CollectionField.EVENT_DUE) {

                    // 订单逾期-在催收提环系统更新案件
                    if (CollectionField.PUSH_REMIND == product.getReactType()) {
                        continue;
                    }
                    int pushRes = rePushOverdue(loanOrderEntity, product);
                    if (CollectionField.PUSH_SUCCESS != pushRes) {
                       retryCollection(collectionParams, subExpression());
                    }
                }
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retryCollection(collectionParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[Collection]", exception);
                }
                LogUtil.sysError("[Collection]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 还款推送
     *
     * @param orderEntity         订单实体类
     * @param product             产品类
     * @param loanOrderBillEntity 订单账单
     * @return 1--推送成功  0--推送失败
     */
    public Integer pushRepay(LoanOrderEntity orderEntity, LoanProductEntity product, LoanOrderBillEntity loanOrderBillEntity) {

        LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderEntity.getId());
        CollectionRepayParam repayParam = new CollectionRepayParam();

        // 订单信息填充
        CollectionOrderInfoParam orderInfo = new CollectionOrderInfoParam();
        orderInfo.setOrderNo(orderEntity.getId());
        orderInfo.setOrderStatus((orderEntity.getStatus() == OrderStatus.COMPLETE || orderEntity.getStatus() == OrderStatus.DUE_COMPLETE) ? 1 : 0);
        orderInfo.setThirdUserId(orderEntity.getUserId());
        orderInfo.setPenaltyDays(DateUtil.getIntervalDays(DateUtil.getStartForDay(lastOrderBill.getActualRepaymentTime()), DateUtil.getStartForDay(lastOrderBill.getRepaymentTime())));
        Double penaltyAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        orderInfo.setPenaltyAmount(penaltyAmount);
        orderInfo.setSettledTime(lastOrderBill.getActualRepaymentTime());
        orderInfo.setUserType(orderEntity.getUserType());

        repayParam.setOrder(orderInfo);
        repayParam.setRepaymentPlan(getRepaymentPlan(orderEntity));

        // 封装还款信息
        List<CollectionRepayRecordParam> repayRecord = new ArrayList<>();
        List<LoanRepaymentPaymentRecordEntity> successRecordList = loanRepaymentPaymentRecordDao.findListRecordDTOByOrderIdAndStatus(orderEntity.getId(), LoanRepaymentPaymentRecordStatus.SUCCESS);
        if (CollectionUtils.isNotEmpty(successRecordList)) {
            successRecordList.stream().forEach(successRecord -> {
                CollectionRepayRecordParam collectionRepayRecordParam = new CollectionRepayRecordParam();
                collectionRepayRecordParam.setOrderNo(orderEntity.getId());
                collectionRepayRecordParam.setThirdRepayRecordId(successRecord.getId());
                collectionRepayRecordParam.setRepayAmount(successRecord.getAmount());
                collectionRepayRecordParam.setReturnedBillAmount(successRecord.getAmount());
                collectionRepayRecordParam.setReturnedPentaltyAmount(orderEntity.getPenaltyInterest());
                collectionRepayRecordParam.setFundType(1);
                collectionRepayRecordParam.setRepayTime(successRecord.getUpdateTime());
                Double reductionAmount = loanOrderBillEntity.getReductionAmount();
                if (ObjectUtils.isNotEmpty(reductionAmount) && new BigDecimal(reductionAmount).compareTo(BigDecimal.ZERO) > 0) {
                    collectionRepayRecordParam.setRepayType(2);
                } else {
                    collectionRepayRecordParam.setRepayType(1);
                }
                collectionRepayRecordParam.setPayStatus(3);

                repayRecord.add(collectionRepayRecordParam);
            });
        }
        repayParam.setRepayRecord(repayRecord);
        String requestParam = getRequestParam(product, CollectionField.PUSH_REPAY, repayParam);

        return push(requestParam, product.getReactType());
    }

    /**
     * 同步逾期费用
     *
     * @param orderEntity 订单类
     * @param product     产品类
     * @return 1--推送成功  0--推送失败
     */
    public Integer rePushOverdue(LoanOrderEntity orderEntity, LoanProductEntity product) {

        LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderEntity.getId());
        CollectionOverdueParam param = new CollectionOverdueParam();
        param.setOrderNo(orderEntity.getId());

        // 总利息
        Double interestAmount = loanOrderBillDao.sumOrderInterestAmount(orderEntity.getId());
        param.setInterest(interestAmount);

        // 总罚息
        Double punishmentAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        param.setPenaltyAmount(punishmentAmount);

        // 逾期天数
        int intervalDays = DateUtil.getIntervalDays(DateUtil.getStartForDay(lastOrderBill.getActualRepaymentTime()), DateUtil.getStartForDay(lastOrderBill.getRepaymentTime()));
        param.setPenaltyDay(intervalDays);

        param.setUserType(orderEntity.getUserType());
        param.setFundType(1);

        String requestParam = getRequestParam(product, CollectionField.PUSH_SYNC_OVERDUE, param);
        return push(requestParam, CollectionField.PUSH_REACT);
    }

    /**
     * 放款订单推送
     *
     * @param orderEntity 订单信息
     * @param product     产品信息
     */
    public Integer pushOrder(LoanOrderEntity orderEntity, LoanProductEntity product) throws Exception {

        LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderEntity.getId());
        if (lastOrderBill.getStatus() == OrderBillStatus.COMPLETE || lastOrderBill.getStatus() == OrderBillStatus.DUE_COMPLETE) {
            return CollectionField.PUSH_SUCCESS;
        }
        LoanUserInfoEntity userInfoEntity = loanUserInfoDao.findUserInfoById(orderEntity.getUserId());

        // 封装请求信息
        CollectionPushOrderParam pushOrderParam = new CollectionPushOrderParam();
        pushOrderParam.setOrderInfo(getOrderParam(orderEntity, lastOrderBill, userInfoEntity));
        pushOrderParam.setRepaymentPlan(getRepaymentPlan(orderEntity));
        pushOrderParam.setUserInfo(getUserInfoParam(orderEntity, userInfoEntity));
        pushOrderParam.setAuthInfo(getAuthParam(userInfoEntity));
        pushOrderParam.setEmerContact(getEmergencyContact(userInfoEntity));
        pushOrderParam.setContact(getContact(orderEntity));

        String requestParam = getRequestParam(product, CollectionField.PUSH_ORDER, pushOrderParam);
        return push(requestParam, product.getReactType());
    }

    /**
     * 封装请求参数
     *
     * @param product  产品信息
     * @param pushType push-order--推送订单   push-repay--推送还款  sync-overdue-fee--同步逾期
     * @param argMaps  请求携带参数 推送订单--CollectionPushOrderParam  还款--CollectionRepayParam 同步逾期--CollectionOverdueParam
     * @return 请求参数JSONString
     */
    public String getRequestParam(LoanProductEntity product, String pushType, Object argMaps) {

        // 参数封装
        CollectionBaseParam<String> param = new CollectionBaseParam<>();
        String argParam = JSON.toJSONString(argMaps, SerializerFeature.DisableCircularReferenceDetect);
        param.setArgs(argParam);

        param.setCall(pushType);

        String productName = getProductName(product.getProductName());
        param.setOrgId(productName);

        String sign = getSign(productName, product.getProductKey(), pushType, argParam);
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
                result = HttpUtils.simplePostInvoke(reactUrl, requestParam, HttpUtils.CONTENT_CHARSET);
                JSONObject resultObject = JSONObject.parseObject(result);

                // 结果解析
                if (resultObject.getInteger(CollectionField.RES_CODE) != HttpStatus.SC_OK) {
                    res = CollectionField.PUSH_FAILED;
                    return res;
                }
            }

            if (reactType == CollectionField.PUSH_REMIND || reactType == CollectionField.PUSH_ALL) {
                result = HttpUtils.simplePostInvoke(remindUrl, requestParam, HttpUtils.CONTENT_CHARSET);
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
     * 封装推送订单时的 订单信息
     *
     * @param orderEntity   订单信息
     * @param lastOrderBill 订单最后一笔账单
     * @param userInfoEntity  用户信息
     * @return 封装的订单信息
     */
    public CollectionOrderParam getOrderParam(LoanOrderEntity orderEntity, LoanOrderBillEntity lastOrderBill, LoanUserInfoEntity userInfoEntity) {
        CollectionOrderParam orderParam = new CollectionOrderParam();
        orderParam.setOrderNo(orderEntity.getId());
        orderParam.setLoanTime(orderEntity.getLoanTime());
        orderParam.setOrderType(orderEntity.getReloan() + 1);
        orderParam.setBillTime(lastOrderBill.getRepaymentTime());
        orderParam.setApplyTime(orderEntity.getApplyTime());
        orderParam.setLoanAmount(orderEntity.getApprovalAmount());
        orderParam.setActualLoanAmount(orderEntity.getActualAmount());
        orderParam.setShouldRepayAmount(orderEntity.getEstimatedRepaymentAmount());
        orderParam.setThirdUserId(Long.valueOf(orderEntity.getUserId()));
        orderParam.setSettledTime(lastOrderBill.getActualRepaymentTime());

        if (ObjectUtils.isNotEmpty(orderEntity.getActualRepaymentAmount())) {
            orderParam.setReturnedAmount(orderEntity.getActualRepaymentAmount());
        } else {
            orderParam.setReturnedAmount(0.0);
        }

        String appName = orderEntity.getAppName();
        if (appName == null) {
            appName = "cashMap";
        }
        orderParam.setAppName(appName);

        // 借款期限
        Integer status = orderEntity.getStatus();
        Integer stagesDay = orderEntity.getStagesDay();
        orderParam.setTerm(status * stagesDay);

        // 期限类型，1-天，2-月
        orderParam.setTermType(1);

        // 订单状态，0-未结清，1-已结清
        if (orderEntity.getStatus() == OrderStatus.COMPLETE || orderEntity.getStatus() == OrderStatus.DUE_COMPLETE) {
            orderParam.setOrderStatus(1);
        } else {
            orderParam.setOrderStatus(0);
        }

        // 服务费
        Double serviceAmount = loanOrderBillDao.sumIncidentalAmount(orderEntity.getId());
        orderParam.setServiceAmount(serviceAmount);

        // 逾期天数
        Integer intervalDays = DateUtil.getIntervalDays(DateUtil.getStartForDay(new Date()), DateUtil.getStartForDay(lastOrderBill.getRepaymentTime()));
        orderParam.setPenaltyDays(intervalDays);

        // 逾期费
        Double penaltyAmount = loanOrderBillDao.sumOrderPunishmentAmount(orderEntity.getId());
        orderParam.setPenaltyAmount(penaltyAmount);

        // 获取订单申请时的地址
        orderParam.setApplyAddress(userInfoEntity.getGpsAddress());

        // 邦地址
        orderParam.setApplyProvince(getProvinceByList(userInfoEntity.getGpsAddress()));
        orderParam.setIdProvince(getProvinceByList(userInfoEntity.getPapersAddress()));
        orderParam.setRegProvince(getProvinceByList(userInfoEntity.getRegisterAddress()));
        orderParam.setUserType(orderEntity.getUserType() + "");

        return orderParam;
    }

    /**
     * 封装紧急联系人
     *
     * @param userInfoEntity 用户其他信息
     * @return 封装的紧急联系人
     */
    public List<CollectionEmerContact> getEmergencyContact(LoanUserInfoEntity userInfoEntity) {
        List<CollectionEmerContact> emerContactList = new ArrayList<>();

        if (StringUtils.isNotBlank(userInfoEntity.getContacts())) {
            String contacts = userInfoEntity.getContacts();
            List<JSONObject> jsonObjects = JSONArray.parseArray(contacts, JSONObject.class);
            jsonObjects.stream().forEach(jsonObject -> {
                CollectionEmerContact emergencyContact = new CollectionEmerContact();
                emergencyContact.setName(jsonObject.getString("name"));
                emergencyContact.setPhone(jsonObject.getString("mobile"));
                emergencyContact.setRelation(jsonObject.getString("relation"));
                emerContactList.add(emergencyContact);
            });
        }

        return emerContactList;
    }

    /**
     * 封装用户验证信息
     *
     * @param userInfoEntity 用户信息
     * @return 封装的用户验证信息
     */
    public CollectionAuthInfoParam getAuthParam(LoanUserInfoEntity userInfoEntity) {
        CollectionAuthInfoParam authInfoParam = new CollectionAuthInfoParam();
        authInfoParam.setBankAuthResult(1);

        // ocr认证状况
        if (StringUtils.isNotEmpty(userInfoEntity.getPapersId())){
            authInfoParam.setIdAuthResult(1);
        } else {
            authInfoParam.setIdAuthResult(0);
        }
        // 用户信息认证状况
        if (StringUtils.isNotEmpty(userInfoEntity.getChildrenNumber())){
            authInfoParam.setContactAuthResult(1);
        } else {
            authInfoParam.setContactAuthResult(0);
        }
        // 基本信息认证状况
        if (StringUtils.isNotEmpty(userInfoEntity.getMonthlyIncome())){
            authInfoParam.setPersonalAuthResult(1);
        } else {
            authInfoParam.setPersonalAuthResult(0);
        }

        return authInfoParam;
    }

    /**
     * 封装用户基本信息
     *
     * @param orderEntity      订单信息
     * @param userInfoEntity       用户信息
     * @return 封装的用户基本信息
     */
    public CollectionUserInfoParam getUserInfoParam(LoanOrderEntity orderEntity, LoanUserInfoEntity userInfoEntity) throws Exception {
        LoanRemittanceAccountEntity remittanceAccount = loanRemittanceAccountDao.findRemittanceAccount(orderEntity.getBankCardId());

        CollectionUserInfoParam userInfoParam = new CollectionUserInfoParam();
        userInfoParam.setThirdUserId(Long.valueOf(orderEntity.getUserId()));
        userInfoParam.setName(userInfoEntity.getPapersFullName());
        userInfoParam.setAadharrNo(userInfoEntity.getPapersId());
        userInfoParam.setPhone(userInfoEntity.getMobile());
        userInfoParam.setPhoneType("NA");
        userInfoParam.setAge(userInfoEntity.getPapersAge());
        userInfoParam.setEmail(userInfoEntity.getEmail());
        userInfoParam.setBankName(remittanceAccount.getBank());
        userInfoParam.setBankAccount(remittanceAccount.getAccountNumber());
        userInfoParam.setBankAccountName(remittanceAccount.getName());
        userInfoParam.setAddress(userInfoEntity.getPapersAddress());
        userInfoParam.setSysAddress(userInfoEntity.getGpsAddress());
        userInfoParam.setCompanyName("NA");
        userInfoParam.setPosition(userInfoEntity.getOccupation());
        userInfoParam.setSalary(userInfoEntity.getMonthlyIncome());
        userInfoParam.setLanguage(getLanguage(userInfoEntity.getRegisterAddress(), userInfoEntity.getGpsAddress()));
        userInfoParam.setRegisterTime(userInfoEntity.getCreateTime());

        // 性别
        String gender = userInfoEntity.getCustomGenter();
        if (StringUtils.isNotBlank(gender) && gender.equalsIgnoreCase(CollectionField.SEX_FEMALE)) {
            userInfoParam.setGender(2);
        } else {
            userInfoParam.setGender(1);
        }

        String gpsLocation = userInfoEntity.getGps();
        String split = ",";
        if (StringUtils.isNotBlank(gpsLocation) && gpsLocation.contains(split)) {
            userInfoParam.setLongitude(gpsLocation.split(split)[0]);
            userInfoParam.setLatitude(gpsLocation.split(split)[1]);
        }

        // 先获取贷超照片信息
        OSSObject frontImgInfo = alibabaOssClient.getFileInfo(userOssBucketName, userInfoEntity.getFrontPath());
        OSSObject backImgInfo = alibabaOssClient.getFileInfo(userOssBucketName, userInfoEntity.getBackPath());
        OSSObject livingImgInfo = alibabaOssClient.getFileInfo(userOssBucketName, userInfoEntity.getFacePath());

        // 将照片上传至minio上
        String adFrontUrl = loanMiNioClient.upload(userFileBucketName, userInfoEntity.getFrontPath(), frontImgInfo);
        String adBackUrl = loanMiNioClient.upload(userFileBucketName, userInfoEntity.getBackPath(), backImgInfo);
        String livingUrl = loanMiNioClient.upload(userFileBucketName, userInfoEntity.getFacePath(), livingImgInfo);

        // 添加minio图片链接
        userInfoParam.setAadFrontImg(adFrontUrl);
        userInfoParam.setAadBackImg(adBackUrl);
        userInfoParam.setLivingImg(livingUrl);

        return userInfoParam;
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
     * 封装推送订单时的 通讯录信息
     *
     * @param orderEntity 订单信息类
     * @return 通讯录信息
     */
    public List<CollectionContact> getContact(LoanOrderEntity orderEntity) {
        List<CollectionContact> contactList = new ArrayList<>();

        try {

            // 封装请求参数
            Map<String, String> params = new HashMap<>();
            params.put(Field.METHOD, CollectionField.COLLECTION_METHOD);
            params.put(Field.APP_ID, riskConfig.getAppId());
            params.put(Field.VERSION, "1.0");
            params.put(Field.SIGN_TYPE, "RSA");
            params.put(Field.FORMAT, "json");
            params.put(Field.TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));

            JSONObject bizData = new JSONObject();
            bizData.put(Field.BORROW_ID, orderEntity.getId());
            bizData.put(Field.TRANSACTION_ID, orderEntity.getUserId());
            params.put(Field.BIZ_DATA, bizData.toJSONString());

            // 生成签名
            String paramsStr = RSAUtils.getSortParams(params);
            String sign = RSAUtils.addSign(riskConfig.getPrivateKey(), paramsStr);
            params.put(Field.SIGN, sign);

            // 请求参数
            String requestParams = JSONObject.toJSONString(params);

            // 发送请求
            String result = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
            if (StringUtils.isEmpty(result)) {
                return null;
            }

            JSONObject jsonObject = JSONObject.parseObject(result);
            if (ObjectUtils.isNotEmpty(jsonObject) && HttpStatus.SC_OK == (jsonObject.getInteger(Field.ERROR))) {
                JSONObject data = jsonObject.getJSONObject(Field.DATA);
                JSONArray originalContact = data.getJSONArray(Field.ORIGINALCONTACT);
                if (CollectionUtils.isNotEmpty(originalContact)) {
                    contactList = JSONObject.parseArray(originalContact.toJSONString(), CollectionContact.class);
                }
            }
        } catch (Exception e) {
            LogUtil.sysError("[Collection originalContact]", e);
        }

        return contactList;
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
     * 获取邦地址
     *
     * @param str 全地址
     * @return 邦地址
     */
    private static String getProvinceByList(String str) {
        String reslut = "unkonw";
        if (StringUtils.isNotBlank(str)) {
            for (String proviceStr : CollectionField.province) {
                if (str.contains(proviceStr)) {
                    reslut = proviceStr.replaceAll(", ", "");
                    break;
                }
            }
        }
        return reslut;
    }

    /**
     * 根据地区获取语音
     *
     * @param address     地址
     * @param currentCity 城市
     * @return 语言
     */
    public Integer getLanguage(String address, String currentCity) {
        // 设置语言
        String idAddr = StringUtils.isNotBlank(address) ? address.replace("\r\n", "") : "";
        String liveDetailAddr = StringUtils.isNotBlank(currentCity) ? currentCity.replace("\r\n", "") : "";
        int language = 1;
        if (idAddr.contains(CollectionField.TAMIL)) {
            language = 2;
        } else if (idAddr.contains(CollectionField.TELANGANA) || idAddr.contains(CollectionField.TELUGU)
                || idAddr.contains(CollectionField.ANDHRAPRADESH)) {
            language = 3;
        } else if (idAddr.contains(CollectionField.KARNATAKA) || idAddr.contains(CollectionField.KANNAD)) {
            language = 5;
        } else if (idAddr.contains(CollectionField.BENGAL) || idAddr.contains(CollectionField.BANGALI)) {
            language = 4;
        } else if (idAddr.contains(CollectionField.MALAYALAM) || idAddr.contains(CollectionField.KERALA)) {
            language = 6;
        } else if (liveDetailAddr.contains(CollectionField.TELANGANA) || liveDetailAddr.contains(CollectionField.TELUGU)
                || liveDetailAddr.contains(CollectionField.ANDHRAPRADESH)) {
            language = 3;
        } else if (liveDetailAddr.contains(CollectionField.BENGAL) || liveDetailAddr.contains(CollectionField.BANGALI)) {
            language = 4;
        } else if (liveDetailAddr.contains(CollectionField.KARNATAKA) || liveDetailAddr.contains(CollectionField.KANNAD)) {
            language = 5;
        } else if (liveDetailAddr.contains(CollectionField.MALAYALAM) || liveDetailAddr.contains(CollectionField.KERALA)) {
            language = 6;
        }
        return language;
    }

}
