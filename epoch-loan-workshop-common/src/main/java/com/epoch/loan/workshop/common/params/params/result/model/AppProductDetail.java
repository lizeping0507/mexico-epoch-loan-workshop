package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-04-13 11:19
 * @Description: 产品信息
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.params.result.model
 */
@Data
public class AppProductDetail implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 是否需要sdk抓取并上报数据
     */
    private boolean needCatchData;

    /**
     * app标识
     */
    private String appId;

}
