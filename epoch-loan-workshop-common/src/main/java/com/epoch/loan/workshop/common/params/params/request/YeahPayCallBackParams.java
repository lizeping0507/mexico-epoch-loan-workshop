package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : YeahpayCallBackParams
 * @createTime : 2022/2/14 10:47
 * @description : Yeahpay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class YeahPayCallBackParams implements Serializable {
    private static final long serialVersionUID = 116541653165465L;
    /**
     * 付款单详情记录ID
     */
    private String id;
    /**
     * 商户ID
     */
    private String merchantId;
    /**
     * 付款单ID
     */
    private String payoutId;
    /**
     * 国家码：IN
     */
    private String countryCode;
    /**
     * 币种 :INR
     */
    private String currency;
    /**
     * 收款账户
     */
    private String payeeAccount;
    /**
     * 收款人名字
     */
    private String payeeName;
    /**
     * 收款人手机号
     */
    private String phone;
    /**
     * 收款人邮箱
     */
    private String email;
    /**
     * 收款人身份证号
     */
    private String idCardNum;
    /**
     * 商户侧代付订单ID
     */
    private String merchantPayoutId;
    /**
     * 付款方式，1电子钱包付款，2银行付款方式
     */
    private String payType;
    /**
     * 单笔费用
     */
    private String singleCharge;
    /**
     * IFSC银行编码
     */
    private String ifsc;
    /**
     * 支付渠道ID
     */
    private Integer channelId;
    /**
     * 本笔支付金额
     */
    private BigDecimal amount;
    /**
     * 状态，0等待处理中，1处理成功，2失败
     */
    private int status;
    /**
     * 状态说明
     */
    private String msg;
    /**
     * 签名
     */
    private String sign;
    /**
     * 备注
     */
    private String comment;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
