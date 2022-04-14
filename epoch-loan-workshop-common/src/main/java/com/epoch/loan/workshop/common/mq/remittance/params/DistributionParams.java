package com.epoch.loan.workshop.common.mq.remittance.params;

import lombok.Data;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq.remittance.params
 * @className : DistributionParams
 * @createTime : 2021/12/16 11:06
 * @description : 支付队列入列参数
 */
@Data
public class DistributionParams {
    /**
     * 支付ID
     */
    private String id;

    /**
     * 支付模型
     */
    private String groupName;

    /**
     * 支付渠道过滤列表
     */
    private List<String> paymentFilter;
}
