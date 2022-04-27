package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : YeahPayRepamentCallbackParam
 * @createTime : 2022/3/9 17:46
 * @description : yeahPay 还款回调参数
 */
@Data
public class PandaRepaymentCallbackParam implements Serializable {
    private String id;
    private String fechaOperacion;
    private String institucionOrdenante;
    private String institucionBeneficiaria;
    private String claveRastreo;
    private String monto;
    private String nombreOrdenante;
    private String tipoCuentaOrdenante;
    private String cuentaOrdenante;
    private String rfcCurpOrdenante;
    private String nombreBeneficiario;
    private String tipoCuentaBeneficiario;
    private String cuentaBeneficiario;
    private String rfcCurpBeneficiario;
    private String conceptoPago;
    private String referenciaNumerica;
    private String empresa;
    private String callbackTime;
}
