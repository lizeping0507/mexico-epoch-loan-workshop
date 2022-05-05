package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : UserOcrBasicInfoResult
 * @createTime : 2022/03/28 18:47
 * @Description: 保存的用户OCR信息
 */
@Data
public class UserOcrBasicInfoResult implements Serializable {

    /**
     * 父亲姓氏
     */
    private String fatherName;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 母亲姓氏
     */
    private String motherName;

    /**
     * 证件编号
     */
    private String idNumber;

    /**
     * 税卡卡号
     */
    private String rfc;

    /**
     * 出生年月日
     */
    private String dateOfBirth;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * ocr识别证件地址
     */
    private String idAddr;

    /**
     *证件正面访问地址
     */
    private String frontImgUrl;

    /**
     * 证件背面访问地址
     */
    private String backImgUrl;

    /**
     * 人脸照片访问地址
     */
    private String faceImgUrl;

}
