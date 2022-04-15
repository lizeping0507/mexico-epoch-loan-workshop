package com.epoch.loan.workshop.common.params.result;

import com.epoch.loan.workshop.common.params.result.model.ProductProcess;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductViewDetailParamsResult
 * @createTime : 2022/3/25 12:16
 * @description : 产品详情页接口响应参数封装
 */
@Data
public class ProductViewDetailResult implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 所属商家id
     */
    private Long merchantId;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 审批金额 1. 即审批本金，参与各种利息管理费手续费等各项费用计算的金额，而非用户拿到钱的金额；
     * 3000元
     */
    private String approvalAmount;

    /**
     * 审批天（月）数-固定 此字段代表审批天数；
     * 28天
     */
    private String approvalTerm;

    /**
     * 总还款额 1. 用户的总还款额（包括本金利息管理费手续费等一切费用）；
     * 3084元
     */
    private String payAmount;

    /**
     * 总到账金额 1. 实际打款到银行卡的金额；2. 保留小数点后4位，单位元；
     * 2400元
     */
    private String receiveAmount;

    /**
     * 利息
     * 58.64元
     */
    private String interest;

    /**
     * 费率
     * 日费率0.098%
     */
    private String rate;

    /**
     * 放款时间
     * 1小时放款
     */
    private String loanTime;

    /**
     * 申请条件
     */
    private List<String> appConditions;

    /**
     * 贷款流程
     */
    private List<ProductProcess> loanProcess;

    /**
     * 产品简介
     */
    private String introduction;

    /**
     * 还款方式
     */
    private String repaymentWay;

    /**
     * 提前还款
     */
    private String advRepayment;

    /**
     * 逾期政策
     */
    private String overdueDesc;

    /**
     * 是否请求费用详情接口标识
     */
    private Integer descFlag;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 座机号码
     */
    private String phoneNumber;

    /**
     * email
     */
    private String email;

    /**
     * 工作时间
     */
    private String workingHours;

    /**
     * 过程费
     */
    private String processingFee;

    /**
     * 申请资格
     */
    private String eligibility;

    /**
     * 描述
     */
    private String attention;

    /**
     * 产品图片
     */
    private String image;
}
