package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.advance.face
 * @className : UserFaceComparisonResponse
 * @createTime : 2022/04/19 15:24
 * @Description: advance 响应的人脸对比信息
 */
@Data
public class AdvanceFaceComparisonResponse implements Serializable {

    /**
     * 2张上传图片的人脸相似度，比率值始终在0-100之间，越接近100表示​​两张人脸相似度越高
     */
    private String similarity;

    /**
     * 第一张图片中的人脸对比信息
     */
    private AdvanceFaceDetailInfoResult firstFace;

    /**
     * 第二张图片中的人脸对比信息
     */
    private AdvanceFaceDetailInfoResult secondFace;
}
