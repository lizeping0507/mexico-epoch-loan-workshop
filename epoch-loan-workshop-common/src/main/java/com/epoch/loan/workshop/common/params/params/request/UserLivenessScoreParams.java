package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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
     * 用户面部照片的标识符
     */
    private String livenessId;

    /**
     * 用户面部照片的标识符 是否合法
     *
     * @return true或false
     */
    public boolean isLivenessIdLegal() {
        if (StringUtils.isEmpty(this.livenessId)) {
            return false;
        }
        return true;
    }
}
