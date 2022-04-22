package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : BindRemittanceAccountParams
 * @createTime : 2022/4/22 15:15
 * @description : 绑定放款账户
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

    /**
     * 验证 放款账户id 是否合法
     *
     * @return true或false
     */
    public boolean isRemittanceAccountIdLegal() {
        if (StringUtils.isEmpty(this.remittanceAccountId)) {
            return false;
        }
        return true;
    }

    /**
     * 验证 订单id 是否合法
     *
     * @return true或false
     */
    public boolean isOrderIdLegal() {
        if (StringUtils.isEmpty(this.orderId)) {
            return false;
        }
        return true;
    }
}
