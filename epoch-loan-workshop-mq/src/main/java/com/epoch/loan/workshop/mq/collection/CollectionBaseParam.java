package com.epoch.loan.workshop.mq.collection;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-07 15:01
 * @Description: 推送催收、提还请求参数
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection
 */
@Data
public class CollectionBaseParam<T> implements Serializable {

    /**
     * 机构标识
     */
    private String orgId;

    /**
     * 验签
     */
    private String sign;

    /**
     * 业务标识
     */
    private String call;

    /**
     * 请求参数
     */
    private T args;
}
