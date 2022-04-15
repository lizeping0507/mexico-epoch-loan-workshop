package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : OrderListParams
 * @createTime : 2022/3/30 15:06
 * @description : 申请借款请求参数封装
 */
@Data
public class ApplyLoanParams extends BaseParams {

    /**
     * 产品Id
     */
    private Long productId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 申请借款时的地址
     */
    private String approvalAddr;

    /**
     * 申请借款时的经纬度
     */
    private String approvalGps;

    /**
     * 被申请借款时的排序
     */
    private Integer productSort;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 多推包标识 1-多推包
     */
    private Integer appType;
}
