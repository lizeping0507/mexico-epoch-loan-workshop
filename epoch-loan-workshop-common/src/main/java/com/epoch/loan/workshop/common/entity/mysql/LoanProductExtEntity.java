package com.epoch.loan.workshop.common.entity.mysql;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.entity.mysql
 * @className : LoanProductEntity
 * @createTime : 2022/2/11 11:28
 * @description : 产品实体类
 */
@Data
public class LoanProductExtEntity {

    /**
     * 产品id
     */
    private String productId;

    /**
     * 0--不推送 1--只推送提还系统    2-- 只推送催收系统  3--推送提还和催收
     */
    private Integer reactType;

    /**
     * 与催收、提还交互的 用于生成验证码的 key
     */
    private String productKey;
}
