package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.request
 * @className : UserFaceComparisonParams
 * @createTime : 2022/04/01 17:41
 * @Description: 人脸相似度入参
 */
@Data
public class UserFaceComparisonParams extends BaseParams {

    /**
     * 证件图片
     */
    private byte[] idImageData;

    /**
     * 人脸图片
     */
    private byte[] faceImageData;

    /**
     * 用户id
     */
    private String userId;

}
