package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : UserFaceComparisonResult
 * @createTime : 2022/04/01 18:52
 * @Description: 获取证件和人脸相似度响应参数
 */
@Data
public class UserFaceComparisonResult implements Serializable {

    /**
     * advance响应码
     */
    private String code;

    /**
     * advance响应信息
     */
    private String message;

    /**
     * 本次请求唯一标识
     */
    private String transactionId;

    /**
     * 请求是否收费，枚举类型：FREE、PAY
     */
    private String pricingStrategy;

    /**
     * advance响应的扩展信息
     */
    private String extra;

    /**
     * 2张上传图片的人脸相似率，比率值始终在0-100之间，比率越接近100，表示两张人脸越相似
     */
    private String similarity;

}
