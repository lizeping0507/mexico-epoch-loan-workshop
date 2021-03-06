package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : OrderListParams
 * @createTime : 2022/3/30 15:06
 * @description : 订单列表请求参数封装
 */
@Data
public class OrderListParams extends BaseParams {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单查询条件
     * 1：待完成订单
     * 2：待还款订单
     * 3：全部订单
     */
    private Integer orderQueryReq;
}
