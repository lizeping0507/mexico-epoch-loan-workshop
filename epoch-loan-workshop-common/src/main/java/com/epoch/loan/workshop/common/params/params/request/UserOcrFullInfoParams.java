package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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
     * 识别类型 AADHAAR_FRON: ad卡正面; AADHAAR_BACK: ad卡背面; PAN_FRONT :pan卡
     */
    private String imageType;

    /**
     * 证件图片
     */
    private byte[] imageData;

    /**
     * 验证 识别类型 是否合法
     *
     * @return true或false
     */
    public boolean isImageTypeLegal() {
        if (StringUtils.isEmpty(this.imageType)) {
            return false;
        }
        return true;
    }

}
