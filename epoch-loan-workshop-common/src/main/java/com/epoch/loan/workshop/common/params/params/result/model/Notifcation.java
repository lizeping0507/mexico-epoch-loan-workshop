package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @createTime :  2021/10/26 18:44
 * @description : 首页通知
 */
@Data
public class Notifcation implements Serializable {

    /**
     * 图片
     */
    private String imgUrl;

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;
}
