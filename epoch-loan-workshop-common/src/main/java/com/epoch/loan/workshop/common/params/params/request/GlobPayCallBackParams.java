package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : GlobPayCallBackParams
 * @createTime : 2022/3/31 15:57
 * @description : GlobPay支付回调参数封装
 */
@Data
@NoArgsConstructor
public class GlobPayCallBackParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;
    private Long code;
    private Long mchId;
    private String mchOrderNo;
    private Long productId;
    private Long orderAmount;
    private String payOrderId;
    private String paySuccessTime;
    private String sign;
}
