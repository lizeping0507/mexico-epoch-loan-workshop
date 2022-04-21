package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.advance.face
 * @className : UserFaceDetailInfo
 * @createTime : 2022/04/19 15:24
 * @Description: advance 人脸对比信息
 */
@Data
public class AdvanceFaceDetailInfoResult implements Serializable {

    /**
     * 图片中人脸的标识符
     */
    private String id;

    /**
     * 图片左边缘的像素大小
     */
    private Double left;

    /**
     * 图片顶部边缘的像素大小
     */
    private Double top;

    /**
     * 图片右边缘的像素大小
     */
    private Double right;

    /**
     * 图片底部缘的像素大小
     */
    private Double bottom;
}
