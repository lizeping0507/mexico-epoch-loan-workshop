package com.epoch.loan.workshop.common.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : MineResult
 * @createTime : 2022/03/25 11:16
 * @Description: 我的个人中心响应参数
 */
@Data
@NoArgsConstructor
public class MineResult implements Serializable {
    /**
     * 掩码后的手机号  例如：123****123
     */
    private String phoneNumber;

    /**
     * 未完成的订单---状态小于110状态的订单数量
     */
    private Integer uncompletedOrder;

    /**
     * 待还款订单数量-- 状态在 170、175还款中、180的订单数量
     */
    private Integer penRepaymentOrder;

    /**
     * 用户所有状态的订单数量
     */
    private Integer allRepaymentOrder;

    /**
     * 帮助中心地址
     */
    private String helpUrl;
}
