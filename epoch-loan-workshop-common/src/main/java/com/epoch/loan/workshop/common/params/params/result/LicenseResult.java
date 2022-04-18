package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : LicenseResult
 * @createTime : 2022/03/28 17:13
 * @Description: 获取第三方license
 */
@Data
public class LicenseResult implements Serializable {

    /**
     * license 权限码
     */
    private String license;
}
