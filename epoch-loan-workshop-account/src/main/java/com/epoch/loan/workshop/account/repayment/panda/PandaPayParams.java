package com.epoch.loan.workshop.account.repayment.panda;

import lombok.Data;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.panda;
 * @className : PandaPayParams
 * @createTime : 2022/4/25
 * @description : PandaPay待收参数
 */
@Data
public class PandaPayParams {
    /**
     * 姓名
     */
    private String name;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 税号
     */
    private String rfc;
    /**
     * 身份证
     */
    private String curp;
    /**
     * 标记系统中的唯一clabe,可以传商户订单号
     */
    private String externalId;
}