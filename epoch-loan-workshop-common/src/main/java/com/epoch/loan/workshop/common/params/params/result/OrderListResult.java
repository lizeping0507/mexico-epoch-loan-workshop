package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResult implements Serializable {

    /**
     * 订单列表
     */
    private List<OrderDTO> list;

}
