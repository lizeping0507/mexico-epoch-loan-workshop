package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : ForgotPwdParams
 * @createTime : 2022/03/23 14:41
 * @Description: 忘记密码参数封装
 */
@Data
@NoArgsConstructor
public class ForgotPwdParams extends BaseParams {
    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 新密码
     */
    private String passwd;

    /**
     * 短信验证码
     */
    private String smsCode;
}
