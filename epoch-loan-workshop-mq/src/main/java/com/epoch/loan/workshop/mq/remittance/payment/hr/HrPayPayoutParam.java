package com.epoch.loan.workshop.mq.remittance.payment.hr;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.hr;
 * @className : HrPayPayoutParam
 * @createTime : 2022/3/29 18:18
 * @description : HrPay放款参数
 */
@Data
public class HrPayPayoutParam {
    /**
     * 商户号: 平台分配商户号
     */
    private String memberid;
    /**
     * 代付单号: 订单号必须唯一, 16 - 32 个字符
     */
    private String orderid;
    /**
     * 支付渠道
     */
    private String bankcode;
    /**
     * 服务端通知: 如返回地址有?或&号请先做urlencode
     */
    private String notifyurl;
    /**
     * 订单金额
     */
    private String amount;
    /**
     * 收款人手机
     */
    private String mobile;
    /**
     * 	收款人邮箱
     */
    private String email;
    /**
     * 银行名称: 不允许传中文（实在没有就传字符串bank）
     */
    private String bankname;
    /**
     * 收款人卡号
     */
    private String cardnumber;
    /**
     * 收款人姓名
     */
    private String accountname;
    /**
     * 收款人IFSC CODE
     */
    private String ifsc;
    /**
     * MD5签名
     */
    private String sign;
}
