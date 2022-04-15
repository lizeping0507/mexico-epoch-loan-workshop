package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : SdkPushInfoParams
 * @createTime : 22/3/30 15:46
 * @description : SDK推送基本信息接口入参封装
 */
@Data
@NoArgsConstructor
public class SdkPushInfoParams extends BaseParams {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 订单id
     */
    private String orderNo;

    /**
     * 产品id
     */
    private Long productId;

}
