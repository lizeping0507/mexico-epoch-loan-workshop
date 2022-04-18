package com.epoch.loan.workshop.common.params.params.result.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-04-11 17:32
 * @Description:
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.params.result.model
 */
@Data
public class PayH5Result implements Serializable {

    /**
     * 打开还款连接方式
     */
    private String h5Type;

    /**
     * 还款连接
     */
    private String path;

    /**
     * 0-不弹UTR输入框，1-弹UTR输入框
     */
    private Integer isUtr;

}
