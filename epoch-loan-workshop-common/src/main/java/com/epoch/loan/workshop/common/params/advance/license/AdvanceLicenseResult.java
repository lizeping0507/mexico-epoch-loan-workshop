package com.epoch.loan.workshop.common.params.advance.license;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.advance.license
 * @className : AdvanceLicenseResult
 * @createTime : 2022/04/19 15:24
 * @Description: advance 获取license响应参数
 */
@Data
public class AdvanceLicenseResult implements Serializable {

    /**
     * 授权状态码
     */
    private String code;

    /**
     * 状态码说明
     */
    private String message;

    /**
     * 请求码
     */
    private String transactionId;

    /**
     * 请求是否收费，枚举类型： FREE--免费, PAY--收费
     */
    private String pricingStrategy;

    /**
     * 授权码信息
     */
    private AdvanceLicenseResponse data;

    /**
     * 其他扩展信息
     */
    private String extra;
}
