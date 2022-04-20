package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.MergePushProduct;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : MergePushListResult
 * @createTime : 2022/3/24 18:23
 * @description : 多推产品列表接口返回封装
 */
@Data
public class MergePushListResult implements Serializable {

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 上一笔订单
     */
    private String lastOrder;

    /**
     * 订单标识
     */
    private String orderNo;

    /**
     * 银行账户
     */
    private String bankCard;

    /**
     * 开户行ifscCode
     */
    private String openBank;

    /**
     * 还款时间
     */
    private String repaymentTime;

    /**
     * 可申请额度
     */
    private List<Long> creditList;

    /**
     * 产品列表
     */
    private List<MergePushProduct> productList;
}
