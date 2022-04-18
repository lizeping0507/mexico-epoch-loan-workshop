package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : BindBankCardResult
 * @createTime : 22/3/29 15:39
 * @description : 绑新卡接口回参封装
 */
@Data
public class BindBankCardResult implements Serializable {
    /**
     * 是否绑卡成功 0-失败 1-成功
     */
    private Integer bindStatus;
}
