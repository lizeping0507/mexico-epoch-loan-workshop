package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 用户id
     */
    private String userId;

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
}
