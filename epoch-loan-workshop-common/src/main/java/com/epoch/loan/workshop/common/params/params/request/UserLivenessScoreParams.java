package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : UserLivenessScoreParams
 * @createTime : 2022/03/28 17:40
 * @Description: advance 获取活体分请求参数
 */
@Data
public class UserLivenessScoreParams extends BaseParams {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 证件类型，advance获取活体分唯一标识
     */
    private String livenessId;
}
