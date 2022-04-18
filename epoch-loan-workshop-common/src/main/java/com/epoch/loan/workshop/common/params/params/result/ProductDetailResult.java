package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.ProductData;
import com.epoch.loan.workshop.common.params.params.result.model.ProductDetail;
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
     * appId
     */
    private String appId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 产品信息
     */
    private ProductData product;

    /**
     * 产品详细
     */
    private ProductDetail productDetail;

    /**
     * 是否需要sdk抓取并上报数据
     */
    private Boolean needCatchData;

    /**
     * 完善基本信息标识
     */
    private Integer userFlag;

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
}
