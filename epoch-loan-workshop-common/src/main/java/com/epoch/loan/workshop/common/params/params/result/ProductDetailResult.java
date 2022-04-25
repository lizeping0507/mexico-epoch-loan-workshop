package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductDetailResult
 * @createTime : 2022/3/25 12:16
 * @description : 产品详情接口响应参数封装
 */
@Data
public class ProductDetailResult implements Serializable {
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 额度范围
     */
    private String amount;

    /**
     * 是否需要sdk抓取并上报数据
     */
    private Boolean needCatchData;

    /**
     * 身份证认证状态 0：未认证，1认证
     */
    private Integer idFlag;

    /**
     * 补充信息认证状态 0：未认证，1认证
     */
    private Integer addInfoFlag;

    /**
     * 基本信息认证状态 0：未认证，1认证
     */
    private Integer baseInfoFlag;

    /**
     * 还款范围
     */
    private String arrivalRange;

    /**
     * 利息范围
     */
    private String interestRange;

    /**
     * 放款到账范围
     */
    private String repaymentRange;

    /**
     * 手续费范围
     */
    private String serviceFeeRange;
}
