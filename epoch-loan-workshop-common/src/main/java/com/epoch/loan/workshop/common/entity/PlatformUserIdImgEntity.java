package com.epoch.loan.workshop.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity
 * @className : PlatformUserIdImgEntity
 * @createTime : 2021/11/25 10:44
 * @description : 用户照片实体类
 */
@Data
public class PlatformUserIdImgEntity {

    /**
     * 用户id,对应user表id
     */
    private Long userId;

    /**
     * ad卡正面照aws的bucket
     */
    private String aadPositiveBucket;

    /**
     * ad卡正面照aws的filename
     */
    private String aadPositiveFilename;

    /**
     * ad卡正面照的url
     */
    private String aadPositiveUrl;

    /**
     * ad卡反面照aws的bucket
     */
    private String aadNegativeBucket;

    /**
     * ad卡反面照aws的filename
     */
    private String aadNegativeFilename;

    /**
     * ad卡反面照的url
     */
    private String aadNegativeUrl;

    /**
     * 建立时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 活体aws的bucket
     */
    private String livingBucket;

    /**
     * 活体aws的filename
     */
    private String livingFilename;

    /**
     * 活体照的url
     */
    private String livingUrl;

    /**
     * pan卡照aws的bucket
     */
    private String panBucket;

    /**
     * pan卡照aws的filename
     */
    private String panFilename;

    /**
     * pan卡照的url
     */
    private String panUrl;

}
