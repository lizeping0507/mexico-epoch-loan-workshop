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
public class PandaAuthorizeParam implements Serializable {

    private static final long serialVersionUID = 116541653165465L;
    private boolean payable;

}
