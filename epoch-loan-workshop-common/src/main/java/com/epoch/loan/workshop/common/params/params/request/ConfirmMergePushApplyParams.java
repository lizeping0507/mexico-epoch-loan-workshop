package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import com.epoch.loan.workshop.common.params.params.result.model.MergePushProduct;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @author 魏玉强
 * @version 1.0
 * @date 2022-04-13 15:07
 * @Description: 多推订单 - 确认申请入参
 * @program: epoch-loan-workshop
 * @packagename: com.epoch.loan.workshop.common.params.result
 */
@Data
public class ConfirmMergePushApplyParams extends BaseParams {

    /**
     * 用户标识
     */
    private String userId;

    /**
     * 订单标识
     */
    private String orderNo;

    /**
     * 申请的产品列表
     */
    private List<MergePushProduct> productList;

}
