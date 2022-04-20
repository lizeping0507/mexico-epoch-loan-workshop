package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result.model
 * @className : AdvanceOcrInfoResponse
 * @createTime : 2022/04/20 19:35
 * @Description: advance证件识别请求响应的证件信息
 */
@Data
public class AdvanceOcrInfoResponse<T> implements Serializable {

    /**
     * 卡的种类: INE_OR_IFE_FRONT or INE_OR_IFE_BACK
     */
    private String cardType;

    /**
     * 卡信息
     */
    private T values;

}
