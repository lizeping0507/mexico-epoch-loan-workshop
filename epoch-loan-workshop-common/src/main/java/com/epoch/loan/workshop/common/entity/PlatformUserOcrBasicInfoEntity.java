package com.epoch.loan.workshop.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformUserOcrBasicInfoEntity
 * @createTime : 2021/11/19 16:36
 * @description : 用户Ocr认证信息实体类 TODO 老表
 */
@Data
public class PlatformUserOcrBasicInfoEntity {

    /**
     * id
     */
    private Long id;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * aadar卡号
     */
    private String aadNo;

    /**
     * 税卡卡号
     */
    private String panNo;

    /**
     * 出生年月日
     */
    private String dateOfBirth;

    /**
     * 性别
     */
    private String gender;

    /**
     * ocr识别证件邮政编码
     */
    private String pinCode;

    /**
     * ocr识别证件地址
     */
    private String address;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * app标识
     */
    private Long appId;
}
