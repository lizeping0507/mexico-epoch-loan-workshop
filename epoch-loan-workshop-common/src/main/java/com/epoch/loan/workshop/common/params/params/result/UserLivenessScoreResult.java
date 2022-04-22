package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result
 * @className : UserLivenessScoreResult
 * @createTime : 2022/04/19 15:24
 * @Description: 活体分响应信息
 */
@Data
public class UserLivenessScoreResult implements Serializable {

    /**
     * 图片 url 或图片 base64
     */
    private String detectionResult;

    /**
     * 反欺骗的分数，范围从 [0,100]，小于 50 意味着它可能是一次攻击
     */
    private String livenessScore;

}
