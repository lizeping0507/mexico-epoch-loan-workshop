package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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
    private String mobile;

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

    /**
     * 验证AF 推广id是否合法
     *
     * @return
     */
    public boolean isImeiLegal() {
        if (StringUtils.isEmpty(this.imei)) {
            return false;
        }
        return true;
    }

    /**
     * 验证AF 推广id是否合法
     *
     * @return
     */
    public boolean isGaIdLegal() {
        if (StringUtils.isEmpty(this.gaId)) {
            return false;
        }
        return true;
    }

    /**
     * 验证安卓id是否合法
     *
     * @return
     */
    public boolean isAndroidIdLegal() {
        if (StringUtils.isEmpty(this.androidId)) {
            return false;
        }
        return true;
    }

    /**
     * 验证手机系统是否合法
     *
     * @return
     */
    public boolean isPlatformLegal() {
        if (StringUtils.isEmpty(this.platform)) {
            return false;
        }
        return true;
    }

    /**
     * 验证验证码是否合法
     *
     * @return
     */
    public boolean isSmsCodeLegal() {
        if (StringUtils.isEmpty(this.smsCode)) {
            return false;
        }
        return true;
    }

    /**
     * 验证密码是否合法
     *
     * @return
     */
    public boolean isPasswordLegal() {
        if (StringUtils.isEmpty(this.password)) {
            return false;
        }
        if (this.password.length() < 6 || this.password.length()>16){
            return false;
        }
        return true;
    }

    /**
     * 验证手机号是否合法
     *
     * @return
     */
    public boolean isMobileLegal() {
        if (StringUtils.isEmpty(this.mobile)) {
            return false;
        }
        return true;
    }
}
