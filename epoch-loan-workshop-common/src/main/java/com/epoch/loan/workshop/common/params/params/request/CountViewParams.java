package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : CountViewParams
 * @createTime : 2022/04/01 20:27
 * @Description: UV统计入参
 */
@Data
public class CountViewParams extends BaseParams {

    /**
     * imei
     */
    private String imei;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 聚道
     */
    private String channelCode;

    /**
     * 用户id
     */
    private Long userId;
}
