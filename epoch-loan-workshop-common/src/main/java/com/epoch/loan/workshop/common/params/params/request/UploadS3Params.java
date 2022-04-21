package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : UploadS3Params
 * @createTime : 2022/3/31 14:34
 * @description : 用户OCR证件信息上传参数封装
 */
@Data
public class UploadS3Params extends BaseParams {

    /**
     * 活体照片
     */
    private byte[] faceImgData;

    /**
     * KFC卡正面
     */
    private byte[] idFrontImgData;

    /**
     * KFC卡反面
     */
    private byte[] idBackImgData;

    /**
     * KFC卡正面 识别出来的信息
     */
    private String frontJson;

    /**
     * KFC卡背面 识别出来的信息
     */
    private String backJson;

    /**
     * rfc
     */
    private String rfc;
}
