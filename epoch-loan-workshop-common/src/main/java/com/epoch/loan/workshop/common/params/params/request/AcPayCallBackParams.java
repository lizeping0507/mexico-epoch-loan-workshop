package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : AcPayCallBackParams
 * @createTime : 2022/3/01 16:10
 * @description : AcPay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class AcPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;

    /**
     * 商户号
     */
    private String merId;
    /**
     * 代付单号
     */
    private String orderId;
    /**
     * 代付订单金额
     */
    private String money;
    /**
     * 代付订单状态:0=申请中,1=已支付，其他状态都为失败
     */
    private String status;
    /**
     * 代付结果，如果是错误，则返回错误信息
     */
    private String msg;
    /**
     * 随机字符串
     */
    private String nonceStr;
    /**
     * 签名
     */
    private String sign;
    /**
     * 附加信息
     */
    private String attch;
}
