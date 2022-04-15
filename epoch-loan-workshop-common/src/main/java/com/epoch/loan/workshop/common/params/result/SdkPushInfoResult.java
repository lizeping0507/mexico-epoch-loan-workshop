package com.epoch.loan.workshop.common.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : SdkPushInfoResult
 * @createTime : 22/3/30 15:49
 * @description :
 */
@Data
@NoArgsConstructor
public class SdkPushInfoResult implements Serializable {

    /**
     * 0：银行卡列表接口 1：推荐产品列表接口
     */
    private Integer type;
}
