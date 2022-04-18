package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.OrderList;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : OrderListResult
 * @createTime : 2022/3/25 12:16
 * @description : 订单列表接口结果封装
 */
@Data
public class OrderListResult implements Serializable {

    /**
     * 订单列表
     */
    private List<OrderList> list;

}
