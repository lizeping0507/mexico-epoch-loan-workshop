package com.epoch.loan.workshop.mq.collection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.OSSObject;
import com.epoch.loan.workshop.common.constant.CollectionField;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.mq.collection.params.CollectionParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.RSAUtils;
import com.epoch.loan.workshop.mq.collection.order.*;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.mq.collection
 * @className : CollectionOrder
 * @createTime : 2022/07/15 16:34
 * @Description: 推送放款数据
 */
@RefreshScope
@Component
@Data
public class CollectionOrder  extends BaseCollectionMQ implements MessageListenerConcurrently {

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 线程池
     */
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(8, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(200), new ThreadPoolExecutor.AbortPolicy());

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

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 根据订单账单id查询账单
                LoanOrderBillEntity lastOrderBill = loanOrderBillDao.findLastOrderBill(orderId);
                if (ObjectUtils.isEmpty(lastOrderBill)) {
                    continue;
                }

                // 产品过滤
                LoanProductEntity product = loanProductDao.findProduct(loanOrderEntity.getProductId());
                if (ObjectUtils.isEmpty(product)) {
                    continue;
                }

                // 产品过滤
                LoanProductExtEntity productExt= loanProductExtDao.findByProductId(loanOrderEntity.getProductId());
                if (ObjectUtils.isEmpty(productExt) || ObjectUtils.isEmpty(productExt.getReactType())
                        || CollectionField.NO_PUSH == productExt.getReactType()) {
                    continue;
                }

                // 订单成功放款-在催收提环系统创建案件
                int pushRes = pushOrder(loanOrderEntity, product, lastOrderBill,productExt);
                if (CollectionField.PUSH_SUCCESS != pushRes) {
                    retryCollection(collectionParams, subExpression());
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
     * 放款订单推送
     *
     * @param orderEntity   订单信息
     * @param product       产品信息
     * @param lastOrderBill 最后一期账单信息
     * @param productExt 产品扩展信息
     */
    public Integer pushOrder(LoanOrderEntity orderEntity, LoanProductEntity product, LoanOrderBillEntity lastOrderBill,LoanProductExtEntity productExt) throws Exception {
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
        pushOrderParam.setContact(new ArrayList<CollectionContact>());

        String requestParam = getRequestParam(product,productExt, CollectionField.PUSH_ORDER, pushOrderParam);
        return push(requestParam, productExt.getReactType());
    }

    /**
     * 封装推送订单时的 订单信息
     *
     * @param orderEntity    订单信息
     * @param lastOrderBill  订单最后一笔账单
     * @param userInfoEntity 用户信息
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
        orderParam.setThirdUserId(orderEntity.getUserId());
        orderParam.setSettledTime(lastOrderBill.getActualRepaymentTime());
        orderParam.setReductionAmount(0.0);
        orderParam.setRemainingRepaymentAmount(orderEntity.getEstimatedRepaymentAmount());

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
        if (StringUtils.isNotBlank(userInfoEntity.getPapersState())) {
            orderParam.setIdProvince(userInfoEntity.getPapersState());
        } else {
            orderParam.setIdProvince(getProvinceByList(userInfoEntity.getPapersAddress()));
        }
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
        if (StringUtils.isNotEmpty(userInfoEntity.getPapersId())) {
            authInfoParam.setIdAuthResult(1);
        } else {
            authInfoParam.setIdAuthResult(0);
        }
        // 用户信息认证状况
        if (StringUtils.isNotEmpty(userInfoEntity.getChildrenNumber())) {
            authInfoParam.setContactAuthResult(1);
        } else {
            authInfoParam.setContactAuthResult(0);
        }
        // 基本信息认证状况
        if (StringUtils.isNotEmpty(userInfoEntity.getMonthlyIncome())) {
            authInfoParam.setPersonalAuthResult(1);
        } else {
            authInfoParam.setPersonalAuthResult(0);
        }

        return authInfoParam;
    }

    /**
     * 封装用户基本信息
     *
     * @param orderEntity    订单信息
     * @param userInfoEntity 用户信息
     * @return 封装的用户基本信息
     */
    public CollectionUserInfoParam getUserInfoParam(LoanOrderEntity orderEntity, LoanUserInfoEntity userInfoEntity) throws Exception {
        LoanRemittanceAccountEntity remittanceAccount = loanRemittanceAccountDao.findRemittanceAccount(orderEntity.getBankCardId());

        CollectionUserInfoParam userInfoParam = new CollectionUserInfoParam();
        userInfoParam.setThirdUserId(orderEntity.getUserId());
        userInfoParam.setName(userInfoEntity.getPapersFullName());
        userInfoParam.setAadharrNo(userInfoEntity.getPapersId());
        userInfoParam.setPhone(userInfoEntity.getMobile());
        userInfoParam.setPhoneType("NA");
        if (ObjectUtils.isNotEmpty(userInfoEntity.getPapersAge())){
            userInfoParam.setAge(userInfoEntity.getPapersAge());
        } else if (ObjectUtils.isNotEmpty(userInfoEntity.getCustomAge())){
            userInfoParam.setAge(Integer.parseInt(userInfoEntity.getCustomAge()));
        }
        userInfoParam.setEmail(userInfoEntity.getEmail());
        userInfoParam.setBankName(remittanceAccount.getBank());
        userInfoParam.setBankAccount(remittanceAccount.getAccountNumber());
        userInfoParam.setBankAccountName(remittanceAccount.getName());
        userInfoParam.setAddress(userInfoEntity.getPapersAddress());
        userInfoParam.setSysAddress(userInfoEntity.getGpsAddress());
        userInfoParam.setCompanyName("NA");
        userInfoParam.setPosition(userInfoEntity.getOccupation());
        userInfoParam.setSalary(userInfoEntity.getMonthlyIncome());
        userInfoParam.setLanguage(1);
        userInfoParam.setRegisterTime(userInfoEntity.getCreateTime());

        // 性别
        String gender = userInfoEntity.getPapersGender();
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

        // 添加minio图片链接
        userInfoParam.setAadFrontImg("https://loan-india.epoch-api.com/api/user/getUserAadhaarPositiveImages?userId=" + orderEntity.getUserId());
        userInfoParam.setAadBackImg("https://loan-india.epoch-api.com/api/user/getUserAadhaarBackImages?userId=" + orderEntity.getUserId());
        userInfoParam.setLivingImg("https://loan-india.epoch-api.com/api/user/getUserLivingImages?userId=" + orderEntity.getUserId());

        return userInfoParam;
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
}
