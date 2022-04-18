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
     * 用户真实姓名
     */
    private String realName;

    /**
     * aadar卡号
     */
    private String idNo;

    /**
     * 税卡卡号
     */
    private String panCode;

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
     * ocr识别证件邮政编码
     */
    private String pinCode;

    /**
     * ocr识别证件地址
     */
    private String idAddr;

    /**
     * pan卡访问地址
     */
    private String panImg;

    /**
     * ad卡正面访问地址
     */
    private String frontImg;

    /**
     * 人脸照片访问地址
     */
    private String livingImg;

    /**
     * ad卡背面访问地址
     */
    private String backImg;
}
