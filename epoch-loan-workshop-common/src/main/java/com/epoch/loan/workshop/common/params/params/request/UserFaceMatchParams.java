package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : UserFaceMatchParams
 * @createTime : 2022/03/28 16:42
 * @Description: 用户OCR识别结果信息
 */
@Data
public class UserFaceMatchParams extends BaseParams {

    /**
     * 用户id
     */
    private String userId;

    /**
     * aadhar卡与人脸匹配分
     */
    private Integer aadharFaceMatch;

    /**
     * pan卡与人脸匹配分
     */
    private Integer panFaceMatch;

    /**
     * 走的哪个 ocr认证 2-ACC  3-闪云金科 4-advance
     */
    private Integer ocrChannelType;
}
