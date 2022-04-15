package com.epoch.loan.workshop.mq.remittance.payment.glob;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.glob;
 * @className : GlobPayPayoutParam
 * @createTime : 2022/3/31 10:52
 * @description : GlobPay放款参数
 */
@Data
public class GlobPayPayoutParam {
    /**
     * 支付接口类型: 0或1,代收固定= 0
     */
    private Integer type;
    /**
     * 商户ID
     */
    private Integer mchId;
    /**
     * 商户订单号
     */
    private String mchOrderNo;
    /**
     * 支付产品ID, 请联系运营获取: 15
     */
    private Integer productId;
    /**
     * 订单金额，单位分，不能带小数,实际金额100，这里需要传10000
     */
    private Integer orderAmount;
    /**
     * 订单回调地址
     */
    private String notifyUrl;
    /**
     * 客户端ip
     */
    private String clientIp;
    /**
     * ios	客户端设备信息。只能是android、ios、pc
     */
    private String device;
    /**
     * 玩家ID
     */
    private Integer uid;
    /**
     * 玩家姓名
     */
    private String customerName;
    /**
     * 电话号码
     */
    private String tel;
    /**
     * E-MAIL地址
     */
    private String email;
    /**
     * 返回类型:json
     */
    private String returnType;
    /**
     * 签名
     */
    private String sign;
    /**
     * 银行卡开户姓名
     */
    private String accountname;
    /**
     * 银行卡号
     */
    private String cardnumber;
    /**
     * 模式: IMPS
     */
    private String mode;
    /**
     * 银行编码（编码）
     */
    private String ifsc;
    /**
     * 银行名称: productId为16和17时，此字段为必传。
     */
    private String bankname;
}
