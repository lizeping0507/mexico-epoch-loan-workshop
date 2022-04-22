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
 * @Description: 更新密码参数封装
 */
@Data
@NoArgsConstructor
public class ModifyPasswordParams extends BaseParams {
    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 用户旧密码md5值
     */
    private String oldPassword;

    /**
     * 用户新密码md5值
     */
    private String newPassword;

    /**
     * 用户新密码确认md5值
     */
    private String enterPassword;

    /**
     * 手机号
     *
     * @return
     */
    public boolean isPhoneNumberLegal() {
        if (StringUtils.isEmpty(this.phoneNumber)) {
            return false;
        }
        return true;
    }

    /**
     * 验证旧密码是否合法
     *
     * @return
     */
    public boolean isOldPasswordLegal() {
        if (StringUtils.isEmpty(this.oldPassword)) {
            return false;
        }
        return true;
    }

    /**
     * 验证新密码是否合法
     *
     * @return
     */
    public boolean isNewPasswordLegal() {
        if (StringUtils.isEmpty(this.newPassword)) {
            return false;
        }
        return true;
    }

    /**
     * 验证确认密码是否合法
     *
     * @return
     */
    public boolean isEnterPasswordLegal() {
        if (StringUtils.isEmpty(this.enterPassword)) {
            return false;
        }
        return true;
    }
}
