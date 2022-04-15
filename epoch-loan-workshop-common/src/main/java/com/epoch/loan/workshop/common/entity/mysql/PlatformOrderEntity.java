package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformOrderEntity
 * @createTime : 2022/04/02 18:15
 * @Description: 旧订单表类
 */
@Data
public class PlatformOrderEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 下单用户id
     */
    private Long userId;

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 订单商品
     */
    private Long productId;

    /**
     * 下单时间
     */
    private Date orderTime;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 0	初始状态
     * 80 待补充材料
     * 86 变身包拦截
     * 90 审批中
     * 100	审批通过	审批通过的状态\r\n
     * 105     审批通过去领款
     * 110	审批不通过	审批不通过的状态\r\n
     * 115	用户确认待放款
     * 120	待开户	\r\n
     * 151待放款
     * 161	贷款取消	1. 因各种原因此用户贷款终止（机构终止or用户主动取消等）；\r\n2. 此状态为终结状态，贷款取消后不再允许流转至其它状态；\r\n3.目前融360APP端不支持用户操作贷款取消，机构端有贷款取消操作和状态时才需回传该状态。\r\n
     * 169	放款失败	1. 用户已进入放款环节，但因资方等各种问题导致最终无法给用户打款,中途放款失败可重新放款的请不要回传该状态；\r\n2. 此状态为终结状态，放款失败后不再允许流转至其它状态；\r\n
     * 171	待提现	1. 机构放款到用户的存管账户才需要推送待提现\r\n2. 若机构使用171状态，则171前不得推送170或175\r\n
     * 170	放款成功	1. 款项必须已成功打至用户卡中才可推送放款成功；\r\n2. 进入放款成功的订单必须推送还款结果，且仅能流转至：200（到期结清）、180（已逾期），若用户已展期订单状态仍为170即可，但需更新还款计划；\r\n
     * 175	还款中	1. 仅针对多期产品，才有该状态；\r\n2. 当某期逾期后结清时，并还需要还款剩余款项，则需要更新订单状态为该状态；\r\n
     * 180	逾期	1. pay_day产品，超过了最后还款日仍有未还清的借款且未办理延期还款；\r\n2. 多期产品，当某期超过了当期最后还款日仍有未还清的借款，则认为订单逾期，当期逾期后结清，需更新为还款中状态，更新时间为状态变更时间；\r\n
     * 200	贷款结清	用户还清全部欠
     */
    private Integer orderStatus;

    /**
     * 收款银行卡关联id
     */
    private Long cardId;

    /**
     * 是否续贷，1：续贷，0：新贷
     */
    private Integer isReloan;

    /**
     * 结算类型. 1 : A , 2 : S
     */
    private Integer settleType;

    /**
     * 渠道
     */
    private Integer channel;

    /**
     * 订单基本信息是否成功推送, 0 : 失败 ; 1:成功
     */
    private Integer detailInfo;

    /**
     * 订单补充信息是否成功推送, 0 : 失败 ; 1:成功
     */
    private Integer appendInfo;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 客群
     */
    private Integer userType;

    /**
     * 审批时间
     */
    private Date approvalTime;

    /**
     * 审批金额
     */
    private BigDecimal approvalAmount;

    /**
     * 实际到账金额
     */
    private BigDecimal receiveAmount;

    /**
     * 放款时间
     */
    private Date loanTime;

    /**
     * 预计还款时间
     */
    private Date expectedRepaymentTime;

    /**
     * 实际还款时间
     */
    private Date actualRepaymentTime;

    /**
     * 实际还款金额
     */
    private BigDecimal actualRepaymentAmount;

    /**
     * appId
     */
    private Long appId;

    /**
     * 订单金额
     */
    private Integer approvalT;
}
