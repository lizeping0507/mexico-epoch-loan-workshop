package com.epoch.loan.workshop.mq.remittance.payment.qe;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.qe;
 * @className : QePayPayoutParam
 * @createTime : 2022/3/29 14:38
 * @description : QePay放款参数
 */
@Data
public class QePayPayoutParam {
    /**
     * 签名方式:固定值MD5，不参与签名
     */
    private String sign_type;
    /**
     * 签名:不参与签名
     */
    private String sign;
    /**
     * 商户代码:平台分配唯一
     */
    private String mch_id;
    /**
     * 商家转账订单号:保证每笔订单唯一
     */
    private String mch_transferId;
    /**
     * 	转账金额
     */
    private String transfer_amount;
    /**
     * 申请时间:时间格式：yyyy-MM-dd HH:mm:ss
     */
    private String apply_date;
    /**
     * 收款银行代码:详见商户后台银行代码表
     */
    private String bank_code;
    /**
     * 收款银行户名:银行户名
     */
    private String receive_name;
    /**
     * 收款银行账号
     */
    private String receive_account;
    /**
     * 备注:印度代付必填IFSC码
     */
    private String remark;
    /**
     * 异步通知地址:若填写则需参与签名,不能携带参数
     */
    private String back_url;
    /**
     * 收款人手机号码:若填写则需参与签名(墨西哥、肯尼亚代付必填)
     */
    private String receiver_telephone;
}
