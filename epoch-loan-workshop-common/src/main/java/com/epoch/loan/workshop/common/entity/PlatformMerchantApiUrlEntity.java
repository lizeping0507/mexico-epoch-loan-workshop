package com.epoch.loan.workshop.common.entity;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformMerchantApiUrlEntity
 * @createTime : 2021/11/24 14:40
 * @description : 机构api地址实体类
 */
@Data
public class PlatformMerchantApiUrlEntity {
    /**
     * 主键 商户ID
     */
    private Long merchantId;

    /**
     * reloan 可申请用户接口url
     */
    private String reloanUrl;

    /**
     * 订单基本信息
     */
    private String orderDetailInfoUrl;

    /**
     * append_info 补充信息接口url
     */
    private String appendInfoUrl;

    /**
     * bank_card_list 银行卡列表接口url
     */
    private String bankCardListUrl;

    /**
     * bind_card 绑卡接口url
     */
    private String bindCardUrl;

    /**
     * bind_card_verification_code_url 绑卡验证码接口url
     */
    private String bindCardVerificationCodeUrl;

    /**
     * pull_order_approve 拉取审批结论接口url
     */
    private String pullOrderApproveUrl;

    /**
     * comfirm_approve 审批确认接口url
     */
    private String comfirmApproveUrl;

    /**
     * comfirm_approve_yzm  审批确认验证码接口url
     */
    private String comfirmApproveYzmUrl;

    /**
     * contract 合同接口url
     */
    private String contractUrl;

    /**
     * pull_repayment 拉取还款计划接口url
     */
    private String pullRepaymentUrl;

    /**
     * pull_order_status 拉取订单状态接口url
     */
    private String pullOrderStatusUrl;

    /**
     * active_repayment 主动还款-普通还款-还款执行接口url
     */
    private String activeRepaymentCommonUrl;

    /**
     * active_repayment 主动还款-验证码还款-还款执行接口url
     */
    private String activeRepaymentVerificationCodeUrl;

    /**
     * active_repayment 主动还款-验证码还款-还款验证码接口url
     */
    private String activeRepaymentVerificationCodeSendUrl;

    /**
     * active_repayment 主动还款-跳转机构提供页面还款-还款执行接口url
     */
    private String activeRepaymentJumpUrl;

    /**
     * active_repayment 主动还款-跳转机构提供页面还款-还款验证码接口url
     */
    private String activeRepaymentJumpSendUrl;

    /**
     * repay_detail 还款详情获取接口url
     */
    private String repayDetailUrl;

    /**
     * defer 展期详情拉取接口url
     */
    private String deferDetailUrl;

    /**
     * defer 展期执行接口url
     */
    private String deferExecuteUrl;

    /**
     * trial 试算接口url
     */
    private String trialUrl;

    /**
     * second_assay 二次活体信息推送接口url
     */
    private String secondAssayUrl;

    /**
     * zhima 芝麻授权跳转接口url
     */
    private String zhimaUrl;

    /**
     * repay_search 线下还款查询接口url
     */
    private String repaySearchUrl;

    /**
     * open_account 开户执行接口url
     */
    private String openAccountUrl;

    /**
     * 发起还款接口url
     */
    private String initiateRepaymentUrl;

    /**
     * 获取电子签章信息接口url
     */
    private String eSignatureUrl;

    /**
     * 获取电子签章验证码接口url
     */
    private String eSignatureSmsCodeUrl;

    /**
     * 校验电子签章验证码接口url
     */
    private String eSignatureSmsCodeVerifyUrl;

    /**
     * depository_business 提现执行接口有url
     */
    private String depositoryBusinessUrl;

    /**
     * credit_inquiry 征信查询接口url
     */
    private String creditInquiryUrl;

    /**
     * 还款失败推送接口url
     */
    private String orderRepayFailUrl;

    /**
     * epoch模型分生成推送接口
     */
    private String epochCatchDataUrl;
}
