package com.epoch.loan.workshop.mq.order.screen;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.*;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.RSAUtils;
import com.epoch.loan.workshop.mq.order.BaseOrderMQListener;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order
 * @className : RiskModelV2
 * @createTime : 2021/11/16 18:02
 * @description : 风控V2
 */
@RefreshScope
@Component
@Data
public class RiskModelV2 extends BaseOrderMQListener implements MessageListenerConcurrently {
    /**
     * 标签
     */
    @Value("${rocket.order.riskModelV2.subExpression}")
    private String subExpression = "";

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 消费任务
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        // 循环处理消息
        for (Message msg : msgs) {
            // 消息对象
            OrderParams orderParams = null;

            try {
                // 获取消息对象
                orderParams = getMessage(msg);
                if (ObjectUtils.isEmpty(orderParams)) {
                    continue;
                }

                // 队列拦截
                if (intercept(orderParams.getGroupName(), subExpression)) {
                    // 等待重试
                    retry(orderParams, subExpression);
                    continue;
                }

                // 订单id
                String orderId = orderParams.getOrderId();

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 判断订单状态是否为废弃
                if (loanOrderEntity.getStatus() == OrderStatus.ABANDONED) {
                    continue;
                }

                // 请求风控获取结果
                JSONObject result = sendRiskV2Request(loanOrderEntity);
                if (ObjectUtils.isEmpty(result)) {
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression, OrderExamineStatus.FAIL);

                    // 错误，重试
                    retry(orderParams, subExpression);
                    continue;
                }

                // 返回码
                Integer code = result.getInteger(Field.ERROR);

                // 验证状态码
                if (code == 4009) {
                    /* 查询失败-风控处理中*/
                    // 如果超过创建时间3小时 不进队列 走拒绝逻辑
                    LoanOrderExamineEntity loanOrderExamine = loanOrderExamineDao.findByModelNameAndOrderId(orderId, subExpression);
                    if (DateUtil.getIntervalMinute(new Date(), loanOrderExamine.getCreateTime()) > 60 * 3){
                        // 更新对应模型审核状态 ：拒绝
                        updateModeExamine(orderId, subExpression, OrderExamineStatus.REFUSE);

                        // 更改订单状态 FIXME 新老表
                        this.updateOrderStatus(orderId, OrderStatus.EXAMINE_FAIL);
                        platformOrderDao.updateOrderStatus(orderId, 110, new Date());
                        continue;
                    }

                    // 更新对应模型审核状态 :等待处理
                    updateModeExamine(orderId, subExpression, OrderExamineStatus.WAIT);

                    // 等待,重试
                    retry(orderParams, subExpression);
                    continue;
                } else if (code == 200) {
                    /* 查询成功*/
                    // 成功
                    JSONObject data = result.getJSONObject(Field.DATA);

                    // 是否通过
                    int pass = data.getInteger(Field.PASS);

                    // 拒绝原因
                    String reason = data.getString(Field.REASON);

                    // 查询用户信息 FIXME 老表需表合并
                    PlatformUserEntity platformUserEntity = platformUserDao.findUser(loanOrderEntity.getUserId());
                    PlatformProductEntity platformProductEntity = platformProductDao.findProduct(loanOrderEntity.getProductId());
                    Long appId = platformOrderDao.findAppIdByOrderNo(orderId);


                    // 新增风控监控表 FIXME 新老表
                    PlatformRiskManagementRefuseReasonEntity platformRiskManagementRefuseReasonEntity = new PlatformRiskManagementRefuseReasonEntity();
                    platformRiskManagementRefuseReasonEntity.setOrderNo(orderId);
                    platformRiskManagementRefuseReasonEntity.setUserId(Long.valueOf(loanOrderEntity.getUserId()));
                    platformRiskManagementRefuseReasonEntity.setUserType(loanOrderEntity.getUserType());
                    platformRiskManagementRefuseReasonEntity.setLoginName(platformUserEntity.getLoginName());
                    platformRiskManagementRefuseReasonEntity.setSource(3);
                    if (pass == 0) {
                        platformRiskManagementRefuseReasonEntity.setSource(2);
                    }
                    platformRiskManagementRefuseReasonEntity.setMerchantId(Long.valueOf(platformProductEntity.getMerchantId()));
                    platformRiskManagementRefuseReasonEntity.setProductId(Long.valueOf(loanOrderEntity.getProductId()));
                    platformRiskManagementRefuseReasonEntity.setPass(pass);
                    platformRiskManagementRefuseReasonEntity.setRefuseReason(reason);
                    platformRiskManagementRefuseReasonEntity.setAppId(Integer.valueOf(appId.toString()));
                    platformRiskManagementRefuseReasonEntity.setChannelId(Integer.valueOf(loanOrderEntity.getUserChannelId()));
                    platformRiskManagementRefuseReasonEntity.setCreateTime(new Date());
                    platformRiskManagementRefuseReasonEntity.setMoudleTag(loanOrderEntity.getType());
                    platformRiskManagementRefuseReasonDao.insert(platformRiskManagementRefuseReasonEntity);

                    // 通过
                    if (pass == 1) {
                        // 额度
                        double quota = data.getDouble(Field.QUOTA);

                        // 更新订单批准额度 FIXME 新老表
                        loanOrderDao.updateOrderApprovalAmount(orderId, quota, new Date());
                        platformOrderDao.updateOrderApprovalAmount(orderId, quota, new Date());

                        // 更新对应模型审核状态
                        updateModeExamine(orderId, subExpression, OrderExamineStatus.PASS);

                        // 发送下一模型
                        sendNextModel(orderParams, subExpression);
                        continue;
                    } else {
                        // 不通过
                        // 更新对应模型审核状态
                        updateModeExamine(orderId, subExpression, OrderExamineStatus.REFUSE);

                        // 更改订单状态 FIXME 新老表
                        this.updateOrderStatus(orderId, OrderStatus.EXAMINE_FAIL);
                        platformOrderDao.updateOrderStatus(orderId, 110, new Date());
                        continue;
                    }
                } else {
                    /* 查询失败*/
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression, OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression);
                    continue;
                }
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression);
                } catch (Exception exception) {
                    LogUtil.sysError("[RiskModelV2]", exception);
                }

                LogUtil.sysError("[RiskModelV2]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

    }


    /**
     * 请求风控
     *
     * @param loanOrderEntity
     * @return
     */
    private JSONObject sendRiskV2Request(LoanOrderEntity loanOrderEntity) {
        try {
            // 用户Id
            String userId = loanOrderEntity.getUserId();

            // 查询用户Ocr信息 FIXME 老表需表合并
            PlatformUserOcrBasicInfoEntity platformUserOcrBasicInfoEntity = platformUserOcrBasicInfoDao.findUserOcrBasicInfo(userId);

            // 查询用户基本信息 FIXME 老表需表合并
            PlatformUserBasicInfoEntity platformUserBasicInfoEntity = platformUserBasicInfoDao.findUserBasicInfo(userId);

            // 查询用户信息 FIXME 老表需表合并
            PlatformUserEntity platformUserEntity = platformUserDao.findUser(userId);

            // 查询用户aadhar卡识别信息 FIXME 老表需表合并
            PlatformUserAadharDistinguishInfoEntity platformUserAadharDistinguishInfoEntity = platformUserAadharDistinguishInfoDao.findUserAadharDistinguishInfo(userId);

            // 查询用户aadhar卡正面识别信息 FIXME 老表需表合并
            PlatformUserOcrAadharFrontLogEntity platformUserOcrAadharFrontLogEntity = platformUserOcrAadharFrontLogDao.findPlatformUserOcrAadharFrontLog(userId);

            // 查询用户ocr识别pan卡日志 FIXME 老表需表合并
            PlatformUserOcrPanFrontLogEntity platformUserOcrPanFrontLogEntity = platformUserOcrPanFrontLogDao.findUserOcrPanFrontLog(userId);

            // 查询用户 Pan卡识别信息表 FIXME 老表需表合并
            PlatformUserPanDistinguishInfoEntity platformUserPanDistinguishInfoEntity = platformUserPanDistinguishInfoDao.findUserPanDistinguishInfo(userId);

            // 查询用户银行卡
            PlatformUserBankCardEntity platformUserBankCardEntity = platformUserBankCardDao.findUserBankCardById(loanOrderEntity.getBankCardId());

            // 封装请求参数
            Map<String, String> params = new HashMap<>();
            params.put(Field.METHOD, "riskmanagement.decision.model.dc.v2.0");
            params.put(Field.APP_ID, riskConfig.getAppId());
            params.put(Field.VERSION, "1.0");
            params.put(Field.SIGN_TYPE, "RSA");
            params.put(Field.FORMAT, "json");
            params.put(Field.TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
            JSONObject bizData = new JSONObject();
            bizData.put(Field.RENOVATE, 1);
            bizData.put(Field.PIN_CODE, platformUserOcrBasicInfoEntity.getPinCode());
            bizData.put(Field.FIRST_NAME, platformUserBasicInfoEntity.getFirstName());
            bizData.put(Field.LAST_NAME, platformUserBasicInfoEntity.getLastName());
            bizData.put(Field.BANK_NAME, platformUserBankCardEntity.getUserName());
            bizData.put(Field.GENDER, "1");
            if ("female".equalsIgnoreCase(platformUserOcrBasicInfoEntity.getGender())) {
                bizData.put(Field.GENDER, "2");
            }
            bizData.put(Field.USER_PHONE, platformUserEntity.getPhoneNumber());
            bizData.put(Field.AADHAAR, platformUserOcrBasicInfoEntity.getAadNo());
            bizData.put(Field.BORROW_ID, loanOrderEntity.getId());
            bizData.put(Field.DATE_OF_BIRTH, platformUserOcrBasicInfoEntity.getDateOfBirth());
            bizData.put(Field.PAN, platformUserOcrBasicInfoEntity.getPanNo());
            bizData.put(Field.AGE, platformUserOcrBasicInfoEntity.getAge());
            bizData.put(Field.TRANSACTION_ID, userId);
            bizData.put(Field.APPLY_TIME, DateUtil.getDefault7());
            bizData.put(Field.FILLED_NAME, platformUserOcrBasicInfoEntity.getRealName());
            bizData.put(Field.AD_OCR_ORIGIN, "");
            if (ObjectUtils.isNotEmpty(platformUserAadharDistinguishInfoEntity)) {
                bizData.put(Field.AD_OCR_ORIGIN, platformUserAadharDistinguishInfoEntity.getName());
            }
            bizData.put(Field.AD_OCR_AMEND, "");
            if (ObjectUtils.isNotEmpty(platformUserOcrAadharFrontLogEntity)) {
                bizData.put(Field.AD_OCR_AMEND, platformUserOcrAadharFrontLogEntity.getName());
            }
            bizData.put(Field.PAN_OCR_AMEND, "");
            if (ObjectUtils.isNotEmpty(platformUserOcrPanFrontLogEntity)) {
                bizData.put(Field.PAN_OCR_AMEND, platformUserOcrPanFrontLogEntity.getName());
            }
            bizData.put(Field.PAN_OCR_ORIGIN, "");
            if (ObjectUtils.isNotEmpty(platformUserPanDistinguishInfoEntity)) {
                bizData.put(Field.PAN_OCR_ORIGIN, platformUserPanDistinguishInfoEntity.getName());
            }
            params.put(Field.BIZ_DATA, bizData.toJSONString());

            // 生成签名
            String paramsStr = RSAUtils.getSortParams(params);
            String sign = RSAUtils.addSign(riskConfig.getPrivateKey(), paramsStr);
            params.put(Field.SIGN, sign);

            // 请求参数
            String requestParams = JSONObject.toJSONString(params);

            // 更新节点请求数据
            loanOrderExamineDao.updateOrderExamineRequest(loanOrderEntity.getId(), subExpression, requestParams, new Date());

            // 发送请求
            String result = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
            if (StringUtils.isEmpty(result)) {
                return null;
            }

            // 更新节点响应数据
            loanOrderExamineDao.updateOrderExamineResponse(loanOrderEntity.getId(), subExpression, result, new Date());

            // 返回响应参数
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            LogUtil.sysError("[RiskModelV2 sendRiskV2Request]", e);
            return null;
        }
    }

}
