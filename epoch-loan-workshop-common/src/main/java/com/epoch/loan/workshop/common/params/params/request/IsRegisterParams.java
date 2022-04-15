package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : IsRegisterParam
 * @createTime : 2022/3/21 11:29
 * @description : 查询手机号是否注册接口参数封装
 */
@Data
@NoArgsConstructor
public class IsRegisterParams extends BaseParams {
    /**
     * 手机号
     */
    private String phoneNumber;
}
