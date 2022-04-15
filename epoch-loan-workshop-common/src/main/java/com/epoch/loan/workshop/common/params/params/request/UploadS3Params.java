package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : UploadS3Params
 * @createTime : 2022/3/31 14:34
 * @description : S3文件上传参数封装
 */
@Data
public class UploadS3Params extends BaseParams {

    /**
     * 税卡正面
     */
    private byte[] panImgData;

    /**
     * 活体照片
     */
    private byte[] livingImgData;

    /**
     * ad卡正面
     */
    private byte[] frontImgData;

    /**
     * ad卡反面
     */
    private byte[] backImgData;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * ad卡编号
     */
    private String idNo;

    /**
     * 税卡卡号
     */
    private String panCode;

    /**
     * 出生日期
     */
    private String dateOfBirth;

    /**
     * ad卡背面信息
     */
    private String adBackJson;

    /**
     * 性别
     */
    private String gender;

    /**
     * 邮政编码
     */
    private String pinCode;

    /**
     * ad卡地址
     */
    private String idAddr;

    /**
     * 用户id
     */
    private String userId;

    /**
     * ad卡正面信息
     */
    private String adFrontJson;

    /**
     * 税卡正面信息
     */
    private String panJson;

    /**
     * 产品id
     */
    private String productId;
}
