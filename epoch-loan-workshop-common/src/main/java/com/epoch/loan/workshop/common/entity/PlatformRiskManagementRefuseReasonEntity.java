package com.epoch.loan.workshop.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformRiskManagementRefuseReasonEntity
 * @createTime : 2022/1/26 10:41
 * @description : 风控规则监控
 */
@Data
public class PlatformRiskManagementRefuseReasonEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户id,对应user表id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String loginName;

    /**
     * 来源. 1 : 贷超申请 ,2 : 系统规则, 3 : 贷超领款
     */
    private Integer source;

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 商品id
     */
    private Long productId;
    /**
     * 是否通过
     */
    private Integer pass;

    /**
     * 拒绝原因
     */
    private String refuseReason;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户客群
     */
    private Integer userType;

    /**
     * app标识
     */
    private Integer appId;

    /**
     * 渠道标识
     */
    private Integer channelId;

    /**
     * 渠道标识
     */
    private Integer moudleTag;
}
