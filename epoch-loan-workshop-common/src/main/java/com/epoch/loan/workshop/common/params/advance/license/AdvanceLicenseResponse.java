package com.epoch.loan.workshop.common.params.advance.license;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.advance.license
 * @className : UserLicenseResponse
 * @createTime : 2022/04/19 15:24
 * @Description: advance 授权码信息
 */
@Data
public class AdvanceLicenseResponse implements Serializable {

    /**
     * license
     */
    private String license;

    /**
     * 过期时间点
     */
    private Long expireTimestamp;
}
