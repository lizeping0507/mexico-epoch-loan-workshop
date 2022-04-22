package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : BindRemittanceAccountParams
 * @createTime : 2022/4/22 15:15
 * @description : TODO 一句话描述该类的功能
 */
@Data
public class BindRemittanceAccountParams extends BaseParams {
    /**
     * 订单id
     */
    private String orderId;

    /**
     * 放款账户id
     */
    private String remittanceAccountId;
}
