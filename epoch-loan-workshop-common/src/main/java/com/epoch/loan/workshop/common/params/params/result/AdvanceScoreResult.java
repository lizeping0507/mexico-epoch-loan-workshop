package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result
 * @className : AdvanceScoreResult
 * @createTime : 2022/04/19 15:24
 * @Description: advance 响应活体检测信息
 */
@Data
public class AdvanceScoreResult implements Serializable {

    /**
     * 活体检测advance响应状态码
     */
    private String code;

    /**
     * 状态码描述信息
     */
    private String message;

    /**
     * 请求id
     */
    private String transactionId;

    /**
     * 请求是否收费，枚举类型： FREE-免费, PAY-付费
     */
    private String pricingStrategy;

    /**
     * 其他扩展信息
     */
    private String extra;

    /**
     * 活体分信息
     */
    private UserLivenessScoreResult data;
}
