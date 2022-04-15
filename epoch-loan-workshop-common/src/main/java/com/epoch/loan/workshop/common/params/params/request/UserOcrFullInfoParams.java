package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : UserOcrFullInfoParams
 * @createTime : 2022/04/01 19:12
 * @Description:
 */
@Data
public class UserOcrFullInfoParams extends BaseParams {

    /**
     * 用户标识
     */
    private String userId;

    /**
     * 识别类型 AADHAAR_FRON: ad卡正面; AADHAAR_BACK: ad卡背面; PAN_FRONT :pan卡
     */
    private String imageType;

    /**
     * 证件图片
     */
    private byte[] imageData;
}
