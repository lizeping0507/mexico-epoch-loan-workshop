package com.epoch.loan.workshop.mq.remittance.payment.yeah;

import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment.yeah;
 * @className : YeahPayToken
 * @createTime : 2022/3/8 11:53
 * @description : YeahPayToken
 */
@Data
public class YeahPayToken {
    /**
     * token 字符串
     */
    private String accessToken;

    /**
     * 超时时间
     */
    private long expirationTimestmp;
}
