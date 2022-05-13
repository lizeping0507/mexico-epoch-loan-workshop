package com.epoch.loan.workshop.mq.collection.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-08 14:49
 * @Description:
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection.order
 */
@Data
public class CollectionUserInfoParam implements Serializable {

    /**
     * 三方用户id
     */
    private String thirdUserId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String aadharrNo;

    /**
     * 电话
     */
    private String phone;

    /**
     * 设备类型
     */
    private String phoneType;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 银行卡所属银行
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankAccount;

    /**
     * 银行账户名
     */
    private String bankAccountName;

    /**
     * 身份证正面
     */
    private String aadFrontImg;

    /**
     * 身份证反面
     */
    private String aadBackImg;

    /**
     * 生活照片
     */
    private String livingImg;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 系统定位地址
     */
    private String sysAddress;

    /**
     * 公司名
     */
    private String companyName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 职位
     */
    private String position;

    /**
     * 薪资
     */
    private String salary;

    /**
     * 语言
     */
    private Integer language;

    /**
     * 注册时间
     */
    private Date registerTime;
}
