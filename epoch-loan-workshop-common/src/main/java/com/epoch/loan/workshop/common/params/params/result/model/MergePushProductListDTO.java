package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-04-13 11:41
 * @Description: 多推--产品列表
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.params.result.model
 */
@Data
public class MergePushProductListDTO implements Serializable {

    /**
     * 产品id
     */
    private Long id;

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 审批额度
     */
    private Integer approvalAmount;

    /**
     * 还款额度
     */
    private Integer repaymentAmount;

    /**
     * 利息
     */
    private Integer interest;

    /**
     * 是否复贷标识
     */
    private Boolean reloanType;

    /**
     * 申请额度标识 1-标识第一个额度对应产品，2-标识第二个额度对应产品，3-标识第三个额度对应的第三个到第N个产品
     */
    private Integer creditType;

    /**
     * 产品icon图标
     */
    private String icon;

    /**
     * 利率
     */
    private String rate;

    /**
     * 利率-纯小数点展示
     */
    private BigDecimal rateN;

    /**
     * 订单号 当前产品对应的上一笔订单号
     */
    private String orderNo;

    /**
     * 订单号 当前产品对应的上一笔订单号的状态
     */
    private String orderStatus;
}
