package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.AppProductDetail;
import com.epoch.loan.workshop.common.params.params.result.model.Banner;
import com.epoch.loan.workshop.common.params.params.result.model.Notifcation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : UserResult
 * @createTime : 2021/4/7 11:12
 * @description : 产品相关接口回参封装
 */
@Data
public class AppModelResult implements Serializable {

    /**
     * APP模式 0:现金贷 1:贷超 2:现金贷-不可申请 3:未认证跳转
     */
    private Integer model;

    /**
     * 余额
     */
    private String amount;

    /**
     * 按钮文案
     */
    private String button;

    /**
     * 认证数量
     */
    private Integer authNumber;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 公告
     */
    private String cashNotice;

    /**
     * 轮播图列表
     */
    private List<Banner> banner;

    /**
     * 公告列表
     */
    private List<Notifcation> list;

    /**
     * 产品信息
     */
    private AppProductDetail productDetailResponse;

    /**
     * 可申请额度
     */
    private String availableCredit;

    /**
     * 总额度
     */
    private String totalCredit;

    /**
     * 已用额度
     */
    private String usedCredit;

    /**
     * 额度锁定 0-不锁，1-锁定
     */
    private Integer locked;

    /**
     * 引导标识 0-不显示该标识 1-显示再贷一笔 2-显示提升额度
     */
    private Integer guideType;

    /**
     * 是否进确认申请页 0 -不进  1- 进
     */
    private String confirmType;

}
