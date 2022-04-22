package com.epoch.loan.workshop.common.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatfromOrderPushRepaymentPlanEntity
 * @createTime : 2021/11/24 11:07
 * @description : 还款计划推送接口表-计划子表 ?
 */
@Data
public class PlatfromOrderPushRepaymentPlanEntity {

    /**
     * 业务无关 方便数据管理
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 关联还款计划主表id
     */
    private Long repaymentId;

    /**
     * 订单编号	string	否	查询账单的订单编号
     */
    private String orderNo;

    /**
     * 到期时间	int	否	到期时间，时间戳
     */
    private Long dueTime;

    /**
     * 应该还的金额
     * 1. 逾期后，需要加入逾期费用
     * 2. 注意：当该期完成还款后，该金额与之前保持一致，不要传0
     */
    private BigDecimal amount;

    /**
     * 是否可以展期	int	是	支持展期时传递，必须传1。否则传0或不传
     */
    private Integer isAbleDefer;

    /**
     * 已还金额	float	是	当机构有部分扣款的时候，必传。已经还成功的金额，amount-paid_amount计算出待还金额
     */
    private BigDecimal paidAmount;

    /**
     * 期数	int	否	具体的第几期
     */
    private Integer periodNo;

    /**
     * 支持的还款方式类型	int	否	机构当前支持的还款方式，
     * 1=主动还款；2=跳转H5；4=银行代扣；8=跳转支付宝还款
     * 除了3,7,11,15外，支持以上数字的其他组合，例如：5=1+4同时支持主动还款和银行代扣; 6=2+4同时支持跳转H5和银行代扣;
     */
    private Integer payType;

    /**
     * 描述	string	否	当期还款金额的相关描述,需可读。
     * 例如：含本金473.10元，利息&手续费172.40元，初审费15.00元，逾期费20.00元
     */
    private String remark;

    /**
     * 可以还款时间	int	否	时间戳，秒，当期最早可以还款的时间
     */
    private Long canRepayTime;

    /**
     * 还款成功时间	int	是	时间戳，秒，当账单状态bill_status为2时必传
     */
    private Long successTime;

    /**
     * 账单状态	int	否	1未到期；2已还款；3逾期
     */
    private Integer billStatus;

}
