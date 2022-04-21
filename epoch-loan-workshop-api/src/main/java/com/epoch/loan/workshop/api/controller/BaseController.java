package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.service.*;
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
    public DynamicRequestService dynamicRequestService;
    /**
     * 支付回调
     */
    @DubboReference(check = false)
    public PaymentCallbackService paymentCallbackService;
    /**
     * 放款
     */
    @DubboReference(check = false)
    public RepaymentService repaymentService;
    /**
     * 用户
     */
    @DubboReference(check = false)
    public UserService userService;
    /**
     * 产品
     */
    @DubboReference(check = false)
    public ProductService productService;
    /**
     * 手机短信
     */
    @DubboReference(check = false)
    public ShortMessageService shortMessageService;
    /**
     * 合同
     */
    @DubboReference(check = false)
    public OrderService orderService;
    /**
     * OCR
     */
    @DubboReference(check = false)
    public OcrService ocrService;
    /**
     * 放款账户
     */
    @DubboReference(check = false)
    public RemittanceService remittanceService;
    /**
     * SDK
     */
    @DubboReference(check = false)
    public SdkService sdkService;
    /**
     * 静态资源
     */
    @DubboReference(check = false)
    StaticResourcesService staticResourcesService;
}
