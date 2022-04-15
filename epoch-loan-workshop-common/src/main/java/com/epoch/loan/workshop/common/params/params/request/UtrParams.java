package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : UtrParams
 * @createTime : 2022/03/29 19:15
 * @Description: utr调用入参
 */
@Data
@NoArgsConstructor
public class UtrParams extends BaseParams {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户输入的utr
     */
    private String utr;
}
