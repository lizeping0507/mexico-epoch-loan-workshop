package com.epoch.loan.workshop.mq.collection.order;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-08 15:17
 * @Description: 通讯录
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.mq.collection.order
 */
@Data
public class CollectionContact implements Serializable {

    /**
     * 通讯录姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 电话在短信中出现的次数
     */
    private String msgTimes;
}
