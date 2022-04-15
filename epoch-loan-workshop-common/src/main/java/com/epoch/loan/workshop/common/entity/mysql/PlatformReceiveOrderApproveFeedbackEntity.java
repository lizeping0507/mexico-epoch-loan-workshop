package com.epoch.loan.workshop.common.entity.mysql;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : PlatformReceiveOrderApproveFeedbackEntity
 * @createTime : 2022/2/15 11:19
 * @description : 审批结果反馈接口表
 */
@Data
public class PlatformReceiveOrderApproveFeedbackEntity {
    /**
     * 主键
     */
    private Long id;
    /**
     * 订单编号 反馈订单的订单编号
     */
    private String orderNo;
    /**
     * 审批结论 10=审批通过 40=审批不通过 30-审批需重填资料
     */
    private Integer conclusion;
    /**
     * 审批通过时间 反馈订单审批通过的时间戳,10位数字
     */
    private Integer approvalTime;
    /**
     * 审批金额是否固定
     */
    private Integer amountType;
    /**
     * 审批金额 1. 即审批本金，参与各种利息管理费手续费等各项费用计算的金额，而非用户拿到钱的金额；
     */
    private BigDecimal approvalAmount;
    /**
     * 期限类型 1=单期产品（按天计息），默认为1 ; 2=多期产品（按月计息），默认为2
     */
    private Integer termUnit;
    /**
     * 审批期限是否固定 0=固定期限；默认为0
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
     * 审批拒绝时间 反馈订单审批被拒的时间戳 10位数字
     */
    private Integer refuseTime;
    /**
     * 需重填的资料项 key说明:仅限于补充信息推送接口推送的用户填写项（抓取项除外）抓取项：contacts，is_simulator，device_info等
     * value(string类型)说明：枚举指['C01','C02','C03'].
     * C01:资料项为空（融360未传）.
     * C02:身份证图片模糊／姿势不对／超过有效期.
     * C03:信息不完整／不规范（如邮箱格式不对，地址不详细）
     */
    private String supplements;
    /**
     * 2客群分组
     */
    private String userTypeGroup;

}
