package com.epoch.loan.workshop.mq.collection.order;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-08 15:18
 * @Description:
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection.order
 */
@Data
public class CollectionEmerContact implements Serializable {
    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 与借款人关系
     */
    private String relation;
}
