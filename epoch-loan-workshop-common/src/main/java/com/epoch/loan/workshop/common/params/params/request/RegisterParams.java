package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : RegisterParams
 * @createTime : 2022/3/21 11:29
 * @description : 注册
 */
@Data
@NoArgsConstructor
public class RegisterParams extends BaseParams {
    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 密码
     */
    private String password;

    /**
     * 短信验证码
     */
    private String smsCode;

    /**
     * 手机系统
     */
    private String platform;

    /**
     * AF id
     */
    private String afId;

    /**
     * AF 推广id
     */
    private String gaId;

    /**
     * 安卓 id
     */
    private String androidId;

    /**
     * 手机imei
     */
    private String imei;
}
