package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.MergePushProduct;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-04-13 15:14
 * @Description: 多推订单 - 确认申请出参
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.params.result
 */
@Data
public class ConfirmMergePushApplyResult implements Serializable {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 订单标识
     */
    private String orderNo;

    /**
     * 产品列表
     */
    private List<MergePushProduct> productList;
}
