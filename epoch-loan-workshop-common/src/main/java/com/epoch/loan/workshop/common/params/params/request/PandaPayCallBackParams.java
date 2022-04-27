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
public class PandaPayCallBackParams implements Serializable {
    private static final long serialVersionUID = 116541653165465L;

    /**
     *
     */
    private String causaDevolucion;

    /**
     *
     */
    private String empresa;

    /**
     *
     */
    private String estado;

    /**
     *
     */
    private String folioOrigen;

    /**
     *
     */
    private String id;

}
