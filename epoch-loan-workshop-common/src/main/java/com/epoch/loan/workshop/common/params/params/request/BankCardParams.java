package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : BankCardParams
 * @createTime : 22/3/28 17:57
 * @description : 银行卡列表接口入参封装
 */
@Data
public class BankCardParams extends BaseParams {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    private String orderNo;

    /**
     * 类型标识 1-多推
     */
    private Integer appType;
}
