package com.epoch.loan.workshop.common.params.advance.liveness;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.advance.liveness
 * @className : ScoreResponse
 * @createTime : 2022/04/19 15:24
 * @Description: advance 响应的活体分信息
 */
@Data
public class ScoreResponse implements Serializable {

	/**
	 * 图片 url 或图片 base64
	 */
	private String detectionResult;

	/**
	 * 反欺骗的分数，范围从 [0,100]，小于 50 意味着它可能是一次攻击
	 */
	private String livenessScore;

}
