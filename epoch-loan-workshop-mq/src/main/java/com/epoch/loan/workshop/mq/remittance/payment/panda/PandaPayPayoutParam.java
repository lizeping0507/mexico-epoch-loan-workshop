package com.epoch.loan.workshop.mq.remittance.payment.panda;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.panda;
 * @className : PandaPayPayoutParam
 * @createTime : 2022/4/25
 * @description : PandaPay放款参数
 */
@Data
public class PandaPayPayoutParam {
    /**
     * 商户号
     */
    private String claveRastreo;
    /**
     * 付款备注
     */
    private String conceptoPago;
    /**
     * 收款人账户
     */
    private String cuentaBeneficiario;
    /**
     * 付款银行账户
     */
    private String cuentaOrdenante;
    /**
     * 收款银行code
     */
    private String institucionContraparte;
    /**
     * 付款银行code
     */
    private Integer institucionOperante;
    /**
     * 金额
     */
    private String monto;
    /**
     * 收款人姓名
     */
    private String nombreBeneficiario;
    /**
     * 付款参考号（7位数）
     */
    private Integer referenciaNumerica;
    /**
     * 收款人的curp或rfc(没有的话包含“ND”)
     */
    private String rfcCurpBeneficiario;
    /**
     * 付款人的curp
     */
    private String rfcCurpOrdenante;
    /**
     * 收款人账户类型
     */
    private Integer tipoCuentaBeneficiario;
    /**
     * 付款人账户类型
     */
    private Integer tipoCuentaOrdenante;
    /**
     * 付款类型
     */
    private Integer tipoPago;
}
