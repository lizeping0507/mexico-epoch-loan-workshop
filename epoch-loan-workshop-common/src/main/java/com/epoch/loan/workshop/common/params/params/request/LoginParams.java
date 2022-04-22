package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : LoginParams
 * @createTime : 2022/3/21 11:29
 * @description : 注册
 */
@Data
@NoArgsConstructor
public class LoginParams extends BaseParams {
    /**
     * 登录名
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;


    /**
     * 验证手机号是否合法
     *
     * @return
     */
    public boolean isLoginNameLegal() {
        if (StringUtils.isEmpty(this.loginName)) {
            return false;
        }
        return true;
    }

    /**
     * 验证密码是否合法
     *
     * @return
     */
    public boolean isPasswordNameLegal() {
        if (StringUtils.isEmpty(this.password)) {
            return false;
        }
        return true;
    }
}
