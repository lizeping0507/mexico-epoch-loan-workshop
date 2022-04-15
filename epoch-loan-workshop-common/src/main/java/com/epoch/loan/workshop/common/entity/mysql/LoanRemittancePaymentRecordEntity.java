package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanPaymentLogEntity
 * @createTime : 2021/12/18 17:49
 * @description : 订单支付记录详情记录表
 */
@Data
public class LoanRemittancePaymentRecordEntity {

    /**
     * id
     */
    private String id;

    /**
     * 渠道Id
     */
    private String paymentId;

    /**
     * 业务Id
     */
    private String businessId;

    /**
     * 业务Id
     */
    private String configTag;

    /**
     * 队列参数，重入队列时更新
     */
    private String queueParam;

    /**
     * 业务Id
     */
    private Integer status;

    /**
     * 支付记录Id
     */
    private String remittanceOrderRecordId;

    /**
     * 请求数据
     */
    private String request;

    /**
     * 响应数据
     */
    private String response;

    /**
     * 查询请求数据
     */
    private String searchRequest;

    /**
     * 查询响应数据
     */
    private String searchesponse;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
