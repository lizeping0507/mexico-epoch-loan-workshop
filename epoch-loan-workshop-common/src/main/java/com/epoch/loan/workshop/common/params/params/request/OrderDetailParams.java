package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : OrderDetailParams
 * @createTime : 2022/3/30 15:06
 * @description : 订单详情请求参数封装
 */
@Data
public class OrderDetailParams extends BaseParams {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 验证 订单号 是否合法
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
