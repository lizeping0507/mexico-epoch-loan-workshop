package com.epoch.loan.workshop.mq.order.screen;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderExamineEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order
 * @className : RiskModelV1
 * @createTime : 2021/11/16 18:02
 * @description : 风控V3
 */
@RefreshScope
@Component
@Data
public class RiskModelV1 extends BaseOrderMQListener implements MessageListenerConcurrently {
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

                // 请求风控获取结果
                JSONObject result = sendRiskV1Request(loanOrderEntity);
                if (ObjectUtils.isEmpty(result)) {
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.FAIL);

                    // 错误，重试
                    retry(orderParams, subExpression());
                    continue;
                }

                // 返回码
                Integer code = result.getInteger(Field.ERROR);

                // 验证状态码
                if (code == 4009) {
                    /* 查询失败-风控处理中*/
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.WAIT);

                    // 如果超过创建时间3小时 不进队列 走拒绝逻辑
                    LoanOrderExamineEntity loanOrderExamine = loanOrderExamineDao.findByModelNameAndOrderId(orderId, subExpression());
                    if (DateUtil.getIntervalMinute(new Date(), loanOrderExamine.getCreateTime()) > 60 * 3) {
                        // 更新对应模型审核状态 ：拒绝
                        updateModeExamine(orderId, subExpression(), OrderExamineStatus.REFUSE);

                        // 更改订单状态 FIXME 新老表
                        this.updateOrderStatus(orderId, OrderStatus.EXAMINE_FAIL);
                        platformOrderDao.updateOrderStatus(orderId, 110, new Date());
                        continue;
                    }

                    // 更新对应模型审核状态 :等待处理
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.WAIT);

                    // 等待,重试
                    retry(orderParams, subExpression());
                    continue;
                } else if (code == 200) {
                    /* 查询成功*/
                    // 成功
                    JSONObject data = result.getJSONObject(Field.DATA);

                    // 是否通过
                    int pass = data.getInteger(Field.PASS);

                    // 通过
                    if (pass == 1) {
                        // 额度
                        double quota = data.getDouble(Field.QUOTA);

                        // 更新订单批准额度 FIXME 新老表
                        loanOrderDao.updateOrderApprovalAmount(orderId, quota, new Date());
                        platformOrderDao.updateOrderApprovalAmount(orderId, quota, new Date());

                        // 更新对应模型审核状态
                        updateModeExamine(orderId, subExpression(), OrderExamineStatus.PASS);

                        // 发送下一模型
                        sendNextModel(orderParams, subExpression());
                        continue;
                    } else {
                        // 不通过
                        // 更新对应模型审核状态
                        updateModeExamine(orderId, subExpression(), OrderExamineStatus.REFUSE);

                        // 更改订单状态 FIXME 新老表
                        this.updateOrderStatus(orderId, OrderStatus.EXAMINE_FAIL);
                        platformOrderDao.updateOrderStatus(orderId, 110, new Date());
                        continue;
                    }
                } else {
                    /* 查询失败*/
                    // 更新对应模型审核状态
                    updateModeExamine(orderId, subExpression(), OrderExamineStatus.FAIL);

                    // 异常,重试
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
                    LogUtil.sysError("[RiskModelV1]", exception);
                }

                LogUtil.sysError("[RiskModelV1]", e);
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
    private JSONObject sendRiskV1Request(LoanOrderEntity loanOrderEntity) {
        try {
            // 用户Id
            String userId = loanOrderEntity.getUserId();

            // 订单id
            String orderId = loanOrderEntity.getId();

            // 是否复贷
            Integer reloan = loanOrderEntity.getReloan();

            // app名称
            String appName = loanOrderEntity.getAppName();

            // 渠道标识
            Integer userChannelId = loanOrderEntity.getUserChannelId();

            // 查询
            LoanUserInfoEntity loanUserInfoEntity = loanUserInfoDao.findUserInfoById(userId);

            // 年龄
            Integer age = loanUserInfoEntity.getAge();

            // 手机号
            String mobile = loanUserInfoEntity.getMobile();

            // 封装请求参数
            Map<String, String> params = new HashMap<>();
            params.put(Field.METHOD, "riskmanagement.mexico.decision.model.dc");
            params.put(Field.APP_ID, riskConfig.getAppId());
            params.put(Field.VERSION, "1.0");
            params.put(Field.SIGN_TYPE, "RSA");
            params.put(Field.FORMAT, "json");
            params.put(Field.TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
            JSONObject bizData = new JSONObject();
            bizData.put("transactionId", userId);
            bizData.put("borrowId", orderId);
            bizData.put("age", age);
            bizData.put("isReloan", reloan);
            bizData.put("phone", mobile);
            bizData.put("appName", appName);
            bizData.put("channelCode", String.valueOf(userChannelId));
            params.put(Field.BIZ_DATA, bizData.toJSONString());

            // 生成签名
            String paramsStr = RSAUtils.getSortParams(params);
            String sign = RSAUtils.addSign(riskConfig.getPrivateKey(), paramsStr);
            params.put(Field.SIGN, sign);

            // 请求参数
            String requestParams = JSONObject.toJSONString(params);

            // 更新节点请求数据
            loanOrderExamineDao.updateOrderExamineRequest(loanOrderEntity.getId(), subExpression(), requestParams, new Date());

            // 发送请求
            String result = HttpUtils.POST_FORM(riskConfig.getRiskUrl(), requestParams);
            if (StringUtils.isEmpty(result)) {
                return null;
            }

            // 更新节点响应数据
            loanOrderExamineDao.updateOrderExamineResponse(loanOrderEntity.getId(), subExpression(), result, new Date());

            // 返回响应参数
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            LogUtil.sysError("[RiskModelV1 sendRiskV1Request]", e);
            return null;
        }
    }

}
