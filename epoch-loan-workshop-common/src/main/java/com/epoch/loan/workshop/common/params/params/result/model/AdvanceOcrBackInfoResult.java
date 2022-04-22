package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.params.result.model
 * @className : AdvanceOcrBackInfoResult
 * @createTime : 2022/04/20 19:42
 * @Description: advance证件识别请求响应 INE卡反面结果
 */
@Data
public class AdvanceOcrBackInfoResult implements Serializable {

    /**
     * 第一行 详细信息
     */
    private String line1All;

    /**
     * 第二行 详细信息
     */
    private String line2All;

    /**
     * 第三行 详细信息
     */
    private String line3All;

    /**
     * 人的cic
     */
    private String cic;

    /**
     * 人的 OCR
     */
    private String ocr;

    /**
     * 卡的cic唯一标识
     */
    private String cicVerificationCode;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 性别
     */
    private String gender;

    /**
     * 失效日期
     */
    private String expiryDate;

    /**
     * 有效期验证码
     */
    private String expiryDateVerificationCode;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 卡的发出行
     */
    private String issuanceAndFuar;

    /**
     * 第1行和第2行验证码
     */
    private String line1AndLine2VerificationCode;

    /**
     * 母亲姓氏
     */
    private String motherLastName;
    /**
     * 父亲姓氏
     */
    private String fatherLastName;

    /**
     * 名字
     */
    private String name;


}
