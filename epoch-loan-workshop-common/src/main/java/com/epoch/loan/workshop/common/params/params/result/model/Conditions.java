package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Shangkunfeng
 * @Packagename : com.epoch.loan.workshop.common.params.result.model
 * @className : Conditions
 * @createTime : 2022/03/30 14:56
 * @description: 个人信息相关说明
 */
@Data
public class Conditions implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 说明
     */
    private String desc;
}
