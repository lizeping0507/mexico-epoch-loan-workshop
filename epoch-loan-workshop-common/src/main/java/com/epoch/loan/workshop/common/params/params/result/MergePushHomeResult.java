package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.MergePushProductInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : MergePushHomeResult
 * @createTime : 2022/3/24 18:23
 * @description : 多推首页接口返回封装
 */
@Data
public class MergePushHomeResult implements Serializable {

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 可申请额度
     */
    private Long availableCredit;

    /**
     * 总额度
     */
    private Long totalCredit;

    /**
     * 已用额度
     */
    private Long usedCredit;

    /**
     * 按钮状态
     */
    private String buttonStatus;

    /**
     * 额度锁定 0-不锁，1-锁定
     */
    private Integer locked;

    /**
     * 订单标识
     */
    private String orderNo;

    /**
     * 可申请产品数
     */
    private String canLoanNum;

    /**
     * 待还款数
     */
    private Integer repaymentNum;

    /**
     * 产品列表
     */
    private List<MergePushProductInfo> productList;
}
