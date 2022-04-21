package com.epoch.loan.workshop.mq.order.screen;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.mysql.*;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.HttpUtils;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.mq.order.BaseOrderMQListener;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order.push
 * @className : PublicCloudPush
 * @createTime : 2021/11/16 18:03
 * @description : 公有云信息推送
 */
@RefreshScope
@Component
@Data
public class PublicCloudInfoPush extends BaseOrderMQListener implements MessageListenerConcurrently {
    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 消费任务
     *
     * @param msgs    消息列表
     * @param context 消息轨迹对象
     * @return
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
                if (intercept(orderParams.getGroupName(), subExpression())) {
                    // 等待重试
                    retry(orderParams, subExpression());
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

                // 请求基本数据推送接口
                JSONObject result = sendPublicCloudInfoPush(loanOrderEntity);
                if (ObjectUtils.isEmpty(result)) {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression(), OrderExamineStatus.FAIL);

                    // 错误，重试
                    retry(orderParams, subExpression());
                    continue;
                }

                // 返回码
                Integer code = result.getInteger(Field.CODE);
                if (code == 200) {
                    /* 推送成功*/
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.PASS);

                    // 发送下一模型
                    sendNextModel(orderParams, subExpression());
                    continue;
                } else {
                    /* 推送失败*/
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.FAIL);

                    // 推送失败 等待,重试
                    retry(orderParams, subExpression());
                    continue;
                }
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression(), OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[PublicCloudInfoPush]", exception);
                }

                LogUtil.sysError("[PublicCloudInfoPush]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 发送撞库请求
     *
     * @param loanOrderEntity
     * @return
     */
    public JSONObject sendPublicCloudInfoPush(LoanOrderEntity loanOrderEntity) {
        try {
            // 获取请求参数
            String requestParams = formatJson(loanOrderEntity);

            // 更新节点请求数据
            loanOrderExamineDao.updateOrderExamineRequest(loanOrderEntity.getId(), subExpression(), requestParams, new Date());

            // 请求三方
            String result = HttpUtils.POST(riskConfig.getCloudPushUrl(), requestParams);
            if (StringUtils.isEmpty(result)) {
                return null;
            }

            // 更新节点响应数据
            loanOrderExamineDao.updateOrderExamineResponse(loanOrderEntity.getId(), subExpression(), result, new Date());

            // 返回响应参数
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            LogUtil.sysError("[PublicCloudInfoPush sendPublicCloudInfoPush]", e);
            return null;
        }
    }

    /**
     * 封装JSON
     *
     * @param loanOrderEntity
     */
    public String formatJson(LoanOrderEntity loanOrderEntity) {
        // 产品id
        String productId = loanOrderEntity.getProductId();

        // 用户Id
        String userId = loanOrderEntity.getUserId();

        // 查询产品信息 FIXME 老表需表合并
        PlatformProductEntity platformProductEntity = platformProductDao.findProduct(productId);

        // 查询用户银行卡
        PlatformUserBankCardEntity platformUserBankCardEntity = null; // TODO platformUserBankCardDao.findUserBankCardById(loanOrderEntity.getBankCardId());

        // 封装数据
        JSONObject params = new JSONObject();
        params.put("is_reloan", loanOrderEntity.getReloan());
        params.put("merchantId", platformProductEntity.getMerchantId());
        params.put("order_no", loanOrderEntity.getId());
        params.put("application_amount", loanOrderEntity.getApprovalAmount());
        params.put("application_term", platformProductEntity.getApprovalTerm());
        params.put("term_unit", platformProductEntity.getTermUnit());
        params.put("channelId", loanOrderEntity.getUserChannelId());
        params.put("orderInfo", orderInfo(loanOrderEntity));
        params.put("applyDetail", applyDetail(loanOrderEntity));
        params.put("applyInfo", applyInfo(loanOrderEntity));
        params.put("contacts", contacts(loanOrderEntity));
        params.put("bankCard", platformUserBankCardEntity);
        return params.toJSONString();
    }

    public JSONObject contacts(LoanOrderEntity loanOrderEntity) {
        // 用户Id
        String userId = loanOrderEntity.getUserId();

        // 查询用户信息 FIXME 老表需表合并
        PlatformUserEntity platformUserEntity = platformUserDao.findUser(userId);

        JSONObject contacts = new JSONObject();
        JSONObject appLocation = new JSONObject();
        if (StringUtils.isNotEmpty(platformUserEntity.getGpsLocation())) {
            appLocation.put("lat", platformUserEntity.getGpsLocation().split(",")[0]);
            appLocation.put("lon", platformUserEntity.getGpsLocation().split(",")[1]);
        }
        appLocation.put("address", platformUserEntity.getRegisterAddr());
        contacts.put("app_location", appLocation);
        return contacts;
    }

    public JSONObject orderInfo(LoanOrderEntity loanOrderEntity) {
        // 用户Id
        String userId = loanOrderEntity.getUserId();

        // 产品id
        String productId = loanOrderEntity.getProductId();

        // 查询用户Ocr信息 FIXME 老表需表合并
        PlatformUserOcrBasicInfoEntity platformUserOcrBasicInfoEntity = platformUserOcrBasicInfoDao.findUserOcrBasicInfo(userId);

        // 查询用户信息 FIXME 老表需表合并
        PlatformUserEntity platformUserEntity = platformUserDao.findUser(userId);

        // 查询产品信息 FIXME 老表需表合并
        PlatformProductEntity platformProductEntity = platformProductDao.findProduct(productId);

        // 查询机构详情 FIXME 老表需表合并
        PlatformMerchantInfoEntity platformMerchantInfoEntity = platformMerchantInfoDao.findMerchantInfo(platformProductEntity.getMerchantId());

        JSONObject orderInfo = new JSONObject();
        orderInfo.put("order_no", loanOrderEntity.getId());
        orderInfo.put("appName", loanOrderEntity.getAppName());
        orderInfo.put("user_name", platformUserOcrBasicInfoEntity.getRealName());
        orderInfo.put("user_id", loanOrderEntity.getUserId());
        orderInfo.put("user_mobile", platformUserEntity.getPhoneNumber());
        orderInfo.put("order_time", Long.valueOf(loanOrderEntity.getCreateTime().getTime() / 1000));
        orderInfo.put("status", 90);
        orderInfo.put("city", platformUserEntity.getRegisterAddr());
        orderInfo.put("bank", platformMerchantInfoEntity.getName());
        orderInfo.put("product", platformProductEntity.getName());
        orderInfo.put("product_id", productId);
        return orderInfo;
    }

    public JSONObject applyDetail(LoanOrderEntity loanOrderEntity) {
        // 用户Id
        String userId = loanOrderEntity.getUserId();

        // 查询用户Ocr信息 FIXME 老表需表合并
        PlatformUserOcrBasicInfoEntity platformUserOcrBasicInfoEntity = platformUserOcrBasicInfoDao.findUserOcrBasicInfo(userId);

        // 查询用户信息 FIXME 老表需表合并
        PlatformUserEntity platformUserEntity = platformUserDao.findUser(userId);

        JSONObject applyDetail = new JSONObject();
        applyDetail.put("real_name", platformUserOcrBasicInfoEntity.getRealName());
        applyDetail.put("aad_no", platformUserOcrBasicInfoEntity.getAadNo());
        applyDetail.put("pan_no", platformUserOcrBasicInfoEntity.getPanNo());
        applyDetail.put("ip_address", platformUserEntity.getRegisterIp());
        applyDetail.put("gps_location", platformUserEntity.getGpsLocation());
        applyDetail.put("gps_address", platformUserEntity.getRegisterAddr());
        applyDetail.put("pan_info", panInfo(loanOrderEntity));
        applyDetail.put("aadhar_info", aadharInfo(loanOrderEntity));
        return applyDetail;
    }

    public JSONObject panInfo(LoanOrderEntity loanOrderEntity) {
        // 用户Id
        String userId = loanOrderEntity.getUserId();

        // 查询用户ocr识别pan卡日志 FIXME 老表需表合并
        PlatformUserOcrPanFrontLogEntity platformUserOcrPanFrontLogEntity = platformUserOcrPanFrontLogDao.findUserOcrPanFrontLog(userId);

        // 查询用户 Pan卡识别信息表 FIXME 老表需表合并
        PlatformUserPanDistinguishInfoEntity platformUserPanDistinguishInfoEntity = platformUserPanDistinguishInfoDao.findUserPanDistinguishInfo(userId);

        JSONObject panInfo = new JSONObject();
        panInfo.put("pan_no", platformUserPanDistinguishInfoEntity.getPanNo());
        panInfo.put("pan_no_conf", platformUserPanDistinguishInfoEntity.getPanNoConf());
        panInfo.put("father", platformUserPanDistinguishInfoEntity.getFather());
        panInfo.put("father_conf", platformUserPanDistinguishInfoEntity.getFatherConf());
        panInfo.put("name", platformUserPanDistinguishInfoEntity.getName());
        panInfo.put("name_conf", platformUserPanDistinguishInfoEntity.getNameConf());
        panInfo.put("date", platformUserPanDistinguishInfoEntity.getDate());
        panInfo.put("date_conf", platformUserPanDistinguishInfoEntity.getDateConf());
        panInfo.put("date_of_issue", platformUserPanDistinguishInfoEntity.getDateOfIssue());
        panInfo.put("date_of_issueConf", platformUserPanDistinguishInfoEntity.getDateOfIssueConf());
        panInfo.put("pan_ocr_origin", platformUserOcrPanFrontLogEntity.getName());
        return panInfo;
    }

    public JSONObject aadharInfo(LoanOrderEntity loanOrderEntity) {
        // 用户Id
        String userId = loanOrderEntity.getUserId();

        // 查询用户aadhar卡识别信息 FIXME 老表需表合并
        PlatformUserAadharDistinguishInfoEntity platformUserAadharDistinguishInfoEntity = platformUserAadharDistinguishInfoDao.findUserAadharDistinguishInfo(userId);

        // 查询用户aadhar卡正面识别信息 FIXME 老表需表合并
        PlatformUserOcrAadharFrontLogEntity platformUserOcrAadharFrontLogEntity = platformUserOcrAadharFrontLogDao.findPlatformUserOcrAadharFrontLog(userId);

        JSONObject aadharInfo = new JSONObject();
        aadharInfo.put("name", platformUserAadharDistinguishInfoEntity.getName());
        aadharInfo.put("name_conf", platformUserAadharDistinguishInfoEntity.getNameConf());
        aadharInfo.put("dob", platformUserAadharDistinguishInfoEntity.getDob());
        aadharInfo.put("dob_conf", platformUserAadharDistinguishInfoEntity.getDobConf());
        aadharInfo.put("gender", platformUserAadharDistinguishInfoEntity.getGender());
        aadharInfo.put("gender_conf", platformUserAadharDistinguishInfoEntity.getGenderConf());
        aadharInfo.put("aadhaar", platformUserAadharDistinguishInfoEntity.getAadhaar());
        aadharInfo.put("aadhaar_conf", platformUserAadharDistinguishInfoEntity.getAadhaarConf());
        aadharInfo.put("father", platformUserAadharDistinguishInfoEntity.getFather());
        aadharInfo.put("father_conf", platformUserAadharDistinguishInfoEntity.getFatherConf());
        aadharInfo.put("pin", platformUserAadharDistinguishInfoEntity.getPin());
        aadharInfo.put("pin_conf", platformUserAadharDistinguishInfoEntity.getPinConf());
        aadharInfo.put("address", platformUserAadharDistinguishInfoEntity.getAddress());
        aadharInfo.put("address_conf", platformUserAadharDistinguishInfoEntity.getAddressConf());
        aadharInfo.put("address_split", platformUserAadharDistinguishInfoEntity.getAddressSplit());
        aadharInfo.put("address_split_conf", platformUserAadharDistinguishInfoEntity.getAddressSplitConf());
        aadharInfo.put("aadhar_back", platformUserAadharDistinguishInfoEntity.getAadharBack());
        aadharInfo.put("aadhar_back_conf", platformUserAadharDistinguishInfoEntity.getAadharBackConf());
        aadharInfo.put("ad_ocr_origin", platformUserOcrAadharFrontLogEntity.getName());
        return aadharInfo;
    }

    public JSONObject applyInfo(LoanOrderEntity loanOrderEntity) {
        // 用户Id
        String userId = loanOrderEntity.getUserId();

        // 查询用户照片
        PlatformUserIdImgEntity platformUserIdImgEntity = platformUserIdImgDao.findUserIdImg(userId);

        // 查询用户基本信息
        PlatformUserBasicInfoEntity platformUserBasicInfoEntity = platformUserBasicInfoDao.findUserBasicInfo(userId);

        // 用户个人信息
        PlatformUserPersonalInfoEntity platformUserPersonalInfoEntity = platformUserPersonalInfoDao.findUserPersonalInfo(userId);

        // 查询用户OCR认证信息
        PlatformUserOcrBasicInfoEntity platformUserOcrBasicInfoEntity = platformUserOcrBasicInfoDao.findUserOcrBasicInfo(userId);

        // 活体信息
        String[] paths = new String[4];
        paths[0] = platformUserIdImgEntity.getAadPositiveBucket() + ":" + platformUserIdImgEntity.getAadPositiveFilename();
        paths[1] = platformUserIdImgEntity.getAadNegativeBucket() + ":" + platformUserIdImgEntity.getAadNegativeFilename();
        paths[2] = platformUserIdImgEntity.getLivingBucket() + ":" + platformUserIdImgEntity.getLivingFilename();
        paths[3] = platformUserIdImgEntity.getPanBucket() + ":" + platformUserIdImgEntity.getPanFilename();

        // 紧急联系人
        String replace = platformUserPersonalInfoEntity.getEmergencyContactPersonAPhone().replace("-", "").replaceAll("\\s*", "");
        String replaceb = platformUserPersonalInfoEntity.getEmergencyContactPersonBPhone().replace("-", "").replaceAll("\\s*", "");

        JSONObject applyInfo = new JSONObject();
        applyInfo.put("first_name", platformUserBasicInfoEntity.getFirstName());
        applyInfo.put("last_name", platformUserBasicInfoEntity.getLastName());
        applyInfo.put("middle_name", platformUserBasicInfoEntity.getMiddleName());
        applyInfo.put("marital", platformUserBasicInfoEntity.getMarital());
        applyInfo.put("email", platformUserBasicInfoEntity.getEmail());
        applyInfo.put("occupation", platformUserBasicInfoEntity.getOccupation());
        applyInfo.put("salary", platformUserBasicInfoEntity.getSalary());
        applyInfo.put("education", platformUserBasicInfoEntity.getEducation());
        applyInfo.put("loan_purpose", platformUserBasicInfoEntity.getLoanPurpose());
        applyInfo.put("current_pin_code", platformUserPersonalInfoEntity.getCurrentPinCode());
        applyInfo.put("current_city", platformUserPersonalInfoEntity.getCurrentCity());
        applyInfo.put("current_address", platformUserPersonalInfoEntity.getCurrentAddress());
        applyInfo.put("company_name", platformUserPersonalInfoEntity.getCompanyName());
        applyInfo.put("designation", platformUserPersonalInfoEntity.getDesignation());
        applyInfo.put("income_way", platformUserPersonalInfoEntity.getIncomeWay());
        applyInfo.put("familys_name", platformUserPersonalInfoEntity.getEmergencyContactPersonAName());
        applyInfo.put("familys_phone", replace);
        applyInfo.put("familys_relationship", platformUserPersonalInfoEntity.getEmergencyContactPersonARelationship());
        applyInfo.put("friends_name", platformUserPersonalInfoEntity.getEmergencyContactPersonBName());
        applyInfo.put("friends_phone", replaceb);
        applyInfo.put("children_num", platformUserPersonalInfoEntity.getChildrenNum());
        applyInfo.put("accommodation_type", platformUserPersonalInfoEntity.getAccommodationType());
        applyInfo.put("aad_positive", Arrays.asList("token://" + loanOrderEntity.getId() + "img0"));
        applyInfo.put("aad_negative", Arrays.asList("token://" + loanOrderEntity.getId() + "img1"));
        applyInfo.put("photo_assay", Arrays.asList("token://" + loanOrderEntity.getId() + "img2"));
        applyInfo.put("pan_positive", Arrays.asList("token://" + loanOrderEntity.getId() + "img3"));
        applyInfo.put("date_of_birth", platformUserOcrBasicInfoEntity.getDateOfBirth());
        applyInfo.put("gender", "female".equals(platformUserOcrBasicInfoEntity.getGender().toLowerCase()) ? 0 : 1);
        applyInfo.put("pin_code", platformUserOcrBasicInfoEntity.getPinCode());
        applyInfo.put("aad_addr", platformUserOcrBasicInfoEntity.getAddress());
        applyInfo.put("age", platformUserOcrBasicInfoEntity.getAge());
        return applyInfo;
    }
}
