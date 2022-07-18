package com.epoch.loan.workshop.common.mq.collection.params;

import lombok.Data;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.mq.collection.params
 * @className : CollectionParams
 * @createTime : 2022/2/27 17:07
 * @description : 催收还提入列参数
 */
@Data
public class CollectionParams {

    /**
     * 订单id
     */
    private String orderId;
}
