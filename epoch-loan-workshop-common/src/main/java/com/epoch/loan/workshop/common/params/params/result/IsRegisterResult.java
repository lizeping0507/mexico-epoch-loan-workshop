package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : IsRegisterResult
 * @createTime : 2021/4/7 11:12
 * @description : 用户相关接口回参封装
 */
@Data
@NoArgsConstructor
public class IsRegisterResult implements Serializable {

    /**
     * 是否存在
     */
    private String isExists;
}
