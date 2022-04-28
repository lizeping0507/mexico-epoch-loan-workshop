package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : PreRepaymentParams
 * @createTime : 2022/4/28 18:31
 * @description : 发起还款接口参数
 */
@Data
public class PreRepaymentParams implements Serializable {

    /**
     * 账单Id
     */
    private String id;
    /**
     * 还款方式
     */
    private String payType;
}
