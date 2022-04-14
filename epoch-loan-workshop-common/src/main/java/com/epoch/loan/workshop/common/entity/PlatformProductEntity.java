package com.epoch.loan.workshop.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformProductEntity
 * @createTime : 2021/11/24 11:07
 * @description : 产品实体类
 */
@Data
public class PlatformProductEntity {
    /**
     * 主键
     */
    private Long id;

    /**
     * 所属商家id
     */
    private String merchantId;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 审批金额是否固定 0=固定金额；固定默认为0 ; 1=金额范围（如500-1000元）；范围默认为1
     */
    private Integer amountType;

    /**
     * 审批金额 1. 即审批本金，参与各种利息管理费手续费等各项费用计算的金额，而非用户拿到钱的金额；
     */
    private BigDecimal approvalAmount;

    /**
     * 期限类型 1=单期产品（按天计息），单期默认为1 ; 2=多期产品（按月计息），多期默认为2
     */
    private Integer termUnit;

    /**
     * 审批期限是否固定 0=固定期限；固定默认为0 ; 1=可选期限（如7天、14天等);可选默认为1
     */
    private Integer termType;

    /**
     * 审批天（月）数-固定 此字段代表审批天数；
     */
    private Integer approvalTerm;

    /**
     * 总还款额 1. 用户的总还款额（包括本金利息管理费手续费等一切费用）；
     */
    private BigDecimal payAmount;

    /**
     * 总还款额组成说明 示机构情况传参，说明机构的总还款金额组成，金额加起来为总还款额。
     */
    private String remark;

    /**
     * 总到账金额 1. 实际打款到银行卡的金额；2. 保留小数点后4位，单位元；
     */
    private BigDecimal receiveAmount;

    /**
     * 审批天数-可选 此字段代表审批天数；string需要按照如"[1,2,3]"格式返回
     */
    private String loanTermOption;

    /**
     * 审批金额最大值 1. 此字段在amount_type=1（金额范围）返回； 2. 保留小数点后4位,单位元；
     */
    private BigDecimal maxLoanAmount;

    /**
     * 审批金额最小值  1.此字段在amount_type=1（金额可选）返回；2. 保留小数点后4位,单位元；
     */
    private BigDecimal minLoanAmount;

    /**
     * 金额变更粒度 此字段在amount_type=1（金额范围）返回，代表在最大审批金额和最小审批金额间的最小变更金额； 例如，审批金额最大值为200，审批金额最小值为100，金额变更粒度为50，则表示审批范围值为：100,150,200。
     */
    private Integer rangeAmount;

    /**
     * 每期应还金额/首期应还金额 1. 保留小数点后4位；2. 若多期账单在还款时某期（如首期）需要额外付一些手续费，则此费用不计算在每期应还金额中；3.对于等额本金的方式，该字段为首期应还金额。
     */
    private BigDecimal periodAmount;

    /**
     * app产品列表图片
     */
    private String image;

    /**
     * 产品上下架状态 0:下架 ; 1:上架
     */
    private Integer status;

    /**
     * app展示排序 desc 越大排序越靠前
     */
    private Integer sort;

    /**
     * 结算类型. 1 : A , 2 : S
     */
    private Integer settleType;

    /**
     * 利息
     */
    private Double integererest;

    /**
     * 费率
     */
    private Double rate;

    /**
     * 还款方式
     */
    private String repaymentMode;

    /**
     * 提前还款方式
     */
    private String earlyRepayment;

    /**
     * 放款时间
     */
    private String loanTime;

    /**
     * 逾期描述
     */
    private String overdueDesc;

    /**
     * 借款期限，1：天，2：月
     */
    private Integer loanTermUnit;

    /**
     * 申请条件
     */
    private String appConditions;

    /**
     * 贷款流程
     */
    private String loanProcess;

    /**
     * 费用描述
     */
    private String costDesc;

    /**
     * 还款期数
     */
    private Integer repaymentStage;

    /**
     * 建立时间
     */
    private Date createTime;

    /**
     * 是否是测试产品  0 : 是, 1 : 否
     */
    private Integer test;

    /**
     * 接口类型，0:api。1:h5
     */
    private Integer integererfaceType;

    /**
     * 接口地址
     */
    private String integererfaceUrl;

    /**
     * 产品标签
     */
    private String tag;

    /**
     * 最大申请期限
     */
    private Integer maxApprovalTerm;

    /**
     * 最小申请期限
     */
    private Integer minApprovalTerm;

    /**
     * 首页展示费率单位,1:天,2:月,3:年
     */
    private Integer firstApprovalUnit;

    /**
     * 首页展示费率
     */
    private Double firstRate;
}
