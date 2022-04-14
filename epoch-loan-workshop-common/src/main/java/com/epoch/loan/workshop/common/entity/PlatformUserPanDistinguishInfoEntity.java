package com.epoch.loan.workshop.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformUserPanDistinguishInfoEntity
 * @createTime : 2021/11/19 18:33
 * @description : Pan卡识别信息表 TODO 老表
 */
@Data
public class PlatformUserPanDistinguishInfoEntity {
    /**
     * 用户标识
     */
    private Long userId;
    /**
     * Pan卡号
     */
    private String panNo;
    /**
     * Pan卡号识别分数
     */
    private Double panNoConf;
    /**
     * 父亲名字
     */
    private String father;
    /**
     * 父亲名称识别分数
     */
    private Double fatherConf;
    /**
     * 名字
     */
    private String name;
    /**
     * 名字识别分数
     */
    private Double nameConf;
    /**
     * 出生日期
     */
    private String date;
    /**
     * 出生日期识别分数
     */
    private Double dateConf;
    /**
     * Pan卡签发时间
     */
    private String dateOfIssue;
    /**
     * Pan卡签发时间识别分数
     */
    private Double dateOfIssueConf;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
}
