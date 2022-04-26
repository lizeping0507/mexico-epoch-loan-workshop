package com.epoch.loan.workshop.account.repayment.panda;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author : lyf
 * @packageName : com.epoch.loan.workshop.common.repayment.oxxopanda;
 * @className : OxxoPandaPayParams
 * @createTime : 2022/4/25
 * @description : OxxoPandaPay待收参数
 */
@Data
public class OxxoPandaPayParams {
    /**
     * 姓名
     */
    private String name;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 支付方式
     */
    private List<JSONObject> payment_sources;
    /**
     * 身份证
     */
    private JSONObject metadata;
}