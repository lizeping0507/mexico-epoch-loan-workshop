package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : RepayDetailParams
 * @createTime : 2022/03/30 20:07
 * @Description: 还款详情请求参数封装
 */
@Data
public class RepayDetailParams extends BaseParams {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private String userId;

}
