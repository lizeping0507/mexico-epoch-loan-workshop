package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : Banner
 * @createTime :  2021/10/26 18:42
 * @description : 轮播图
 */
@Data
public class Banner implements Serializable {

    /**
     * 轮播图片链接
     */
    private String logo;
}
