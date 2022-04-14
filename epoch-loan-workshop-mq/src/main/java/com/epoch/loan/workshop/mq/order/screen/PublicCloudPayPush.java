package com.epoch.loan.workshop.mq.order.screen;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.entity.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.PlatformProductEntity;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order.screen
 * @className : PublicCloudPayPush
 * @createTime : 2021/11/17 11:29
 * @description : 公有云放款推送
 */
@RefreshScope
@Component
@Data
public class PublicCloudPayPush extends BaseOrderMQListener implements MessageListenerConcurrently {

    /**
     * 标签
     */
    @Value("${rocket.order.publicCloudPayPush.subExpression}")
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

                // 查询当前模型处理状态
                int status = getModelStatus(orderId, subExpression);

                // 判断模型状态
                if (status == OrderExamineStatus.CREATE) {
                    // 发起放款
                    JSONObject result = sendPayPush(loanOrderEntity);
                    if (ObjectUtils.isEmpty(result)) {
                        // 错误，重试
                        retry(orderParams, subExpression);
                        continue;
                    }

                    // 返回码
                    Integer code = result.getInteger(Field.CODE);
                    if (code == 200) {
                        /* 推送成功*/
                        // 更新订单状态为等待放款 FIXME 新老表
                        updateOrderStatus(orderId, OrderStatus.WAIT_PAY);
                        platformOrderDao.updateOrderStatus(orderId, 115, new Date());

                        // 更新放款时间
                        loanOrderDao.updateOrderLoanTime(orderId, new Date(), new Date());

                        // 更改模型审核状态为等待
                        updateModeExamine(orderId, subExpression, OrderExamineStatus.WAIT);

                        // 放入队列等待放款成功
                        retry(orderParams, subExpression);
                        continue;
                    } else {
                        /* 推送失败*/
                        // 更新对应模型审核状态
                        updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                        // 异常,重试
                        retry(orderParams, subExpression);
                        continue;
                    }
                } else if (status == OrderExamineStatus.WAIT || status == OrderExamineStatus.FAIL) {
                    // 已经发起放款，查询放款进度
                    JSONObject result = sendPayStatusPush(loanOrderEntity);
                    if (ObjectUtils.isEmpty(result)) {
                        // 更新对应模型审核状态
                        updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                        // 错误，重试
                        retry(orderParams, subExpression);
                        continue;
                    }

                    // 返回码
                    Integer code = result.getInteger(Field.CODE);
                    if (code == 200) {
                        JSONObject data = result.getJSONObject(Field.DATA);

                        // 判断支付状态
                        if (data.getInteger(Field.STATE) == 30) {
                            // 还款金额
                            double amount = data.getDouble(Field.AMOUNT);

                            // 更新还款金额
                            loanOrderDao.updateOrderEstimatedRepaymentAmount(orderId, amount, new Date());

                            // 更新到账时间 FIXME 新老表
                            loanOrderDao.updateOrderArrivalTime(orderId, new Date(), new Date());
                            platformOrderDao.updateOrderLoanTime(orderId, new Date(), new Date());

                            // 更改审核状态为通过
                            updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.PASS);

                            // 发送下一模型
                            sendNextModel(orderParams, subExpression);
                            continue;
                        } else if (data.getInteger(Field.STATE) == 31) {
                            // 重试
                            retry(orderParams, subExpression);

//                                // 更改订单审核状态
//                                updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.REFUSE);
//
//                                // 失败 FIXME 新老表
//                                this.updateOrderStatus(orderId, OrderStatus.ABANDONED);
//                                loanOrderBillDao.updateOrderBillStatusByOrderId(orderId, OrderBillStatus.ABANDONED, new Date());
//                                platformOrderDao.updateOrderStatus(orderId, 169, new Date());
                            continue;
                        } else {
                            // 更新对应模型审核状态
                            updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                            // 重试
                            retry(orderParams, subExpression);
                            continue;
                        }
                    } else {
                        // 更新对应模型审核状态
                        updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                        // 重试
                        retry(orderParams, subExpression);
                        continue;
                    }
                } else {
                    // 未知状态
                    continue;
                }
            } catch (Exception e) {
                try {
                    // 更新对应模型审核状态
                    updateModeExamine(orderParams.getOrderId(), subExpression, OrderExamineStatus.FAIL);

                    // 异常,重试
                    retry(orderParams, subExpression);
                } catch (Exception exception) {
                    LogUtil.sysError("[PublicCloudPayPush]", exception);
                }

                LogUtil.sysError("[PublicCloudPayPush]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 支付推送
     *
     * @param loanOrderEntity
     * @return
     */
    public JSONObject sendPayPush(LoanOrderEntity loanOrderEntity) throws Exception {
        try {
            // 产品id
            String productId = loanOrderEntity.getProductId();

            // 查询产品信息 FIXME 老表需表合并
            PlatformProductEntity platformProductEntity = platformProductDao.findProduct(productId);

            JSONObject params = new JSONObject();
            params.put("orderNo", loanOrderEntity.getId());
            params.put("merchantId", platformProductEntity.getMerchantId());
            String requestParams = params.toJSONString();

            // 更新节点请求数据
            loanOrderExamineDao.updateOrderExamineRequest(loanOrderEntity.getId(), subExpression, requestParams, new Date());

            // 请求三方
            String result = HttpUtils.POST(riskConfig.getCloudPayUrl(), requestParams);
            if (StringUtils.isEmpty(result)) {
                return null;
            }

            // 更新节点响应数据
            loanOrderExamineDao.updateOrderExamineResponse(loanOrderEntity.getId(), subExpression, result, new Date());

            // 返回响应参数
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            LogUtil.sysError("[PublicCloudPayPush sendPayPush]", e);
            return null;
        }
    }

    /**
     * 支付状态
     *
     * @param loanOrderEntity
     * @return
     */
    public JSONObject sendPayStatusPush(LoanOrderEntity loanOrderEntity) {
        try {
            // 产品id
            String productId = loanOrderEntity.getProductId();

            // 查询产品信息 FIXME 老表需表合并
            PlatformProductEntity platformProductEntity = platformProductDao.findProduct(productId);

            JSONObject params = new JSONObject();
            params.put("orderNo", loanOrderEntity.getId());
            params.put("merchantId", platformProductEntity.getMerchantId());
            String requestParams = params.toJSONString();

            // 更新节点请求数据
            loanOrderExamineDao.updateOrderExamineRequest(loanOrderEntity.getId(), subExpression, requestParams, new Date());

            // 请求三方
            String result = HttpUtils.POST(riskConfig.getCloudPayStatusUrl(), requestParams);
            if (StringUtils.isEmpty(result)) {
                return null;
            }

            // 更新节点响应数据
            loanOrderExamineDao.updateOrderExamineResponse(loanOrderEntity.getId(), subExpression, result, new Date());

            // 返回响应参数
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            LogUtil.sysError("[PublicCloudPayPush sendPayStatusPush]", e);
            return null;
        }
    }

}
