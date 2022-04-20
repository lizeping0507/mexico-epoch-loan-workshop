package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * 手机号
     * @return
     */
    public boolean isPhoneNumberLegal() {
        if (StringUtils.isEmpty(this.phoneNumber)) {
            return false;
        }
        return true;
    }
    /**
     * 手机号
     * @return
     */
    public boolean isPasswdLegal() {
        if (StringUtils.isEmpty(this.passwd)) {
            return false;
        }
        return true;
    }
    /**
     * 手机号
     * @return
     */
    public boolean isSmsCodeLegal() {
        if (StringUtils.isEmpty(this.smsCode)) {
            return false;
        }
        return true;
    }
}
