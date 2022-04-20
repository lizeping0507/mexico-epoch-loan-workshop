package com.epoch.loan.workshop.common.params.params.result;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.params.params.result.model.AdvanceLicenseResponse;
import com.epoch.loan.workshop.common.params.params.result.model.AdvanceOcrInfoResponse;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result
 * @className : AdvanceOcrInfoResult
 * @createTime : 2022/04/20 19:05
 * @Description: advance证件识别请求响应结果
 */
@Data
public class AdvanceOcrInfoResult<T> implements Serializable {

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
     * 证件信息
     */
    private JSONObject data;

    /**
     * 其他扩展信息
     */
    private String extra;
}
