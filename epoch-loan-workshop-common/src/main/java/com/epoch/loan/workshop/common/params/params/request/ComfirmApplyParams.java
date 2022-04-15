package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : ComfirmApplyParams
 * @createTime : 2022/3/30 15:06
 * @description : 申请确认页请求参数封装
 */
@Data
public class ComfirmApplyParams extends BaseParams {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户Id
     */
    private Long userId;
}
