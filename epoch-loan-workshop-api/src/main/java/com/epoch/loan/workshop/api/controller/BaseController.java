package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.service.PaymentCallbackService;
import com.epoch.loan.workshop.common.service.RepaymentService;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.controller
 * @className : BaseController
 * @createTime : 2022/2/17 14:31
 * @description : Controller基础类
 */
public class BaseController {
    /**
     * 支付回调
     */
    @DubboReference(check = false)
    protected PaymentCallbackService paymentCallbackService;
    /**
     * 放款
     */
    @DubboReference(check = false)
    protected RepaymentService repaymentService;
}
