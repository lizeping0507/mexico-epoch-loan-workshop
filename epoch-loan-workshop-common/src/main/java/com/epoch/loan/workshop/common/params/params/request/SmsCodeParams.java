package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : sendRegisterMessageParams
 * @createTime : 2022/3/21 11:29
 * @description : 查询手机号是否注册接口参数封装
 */
@Data
public class SmsCodeParams extends BaseParams {
    /**
     * 手机号
     */
    private String mobile;



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
