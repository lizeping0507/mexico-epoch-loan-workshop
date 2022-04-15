package com.epoch.loan.workshop.common.params.params.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-03-22 15:22
 * @Description: 合同请求参数
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.params.params.request
 */
@Data
public class ContractParams implements Serializable {

    private static final long serialVersionUID = 116541653165465L;

    /**
     * 订单编号
     */
    private String orderNo;

}
