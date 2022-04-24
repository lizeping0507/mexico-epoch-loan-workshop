package com.epoch.loan.workshop.common.params.params.result;

import com.epoch.loan.workshop.common.params.params.result.model.Banner;
import com.epoch.loan.workshop.common.params.params.result.model.Notifcation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : AppMaskModelResult
 * @createTime : 2021/4/7 11:12
 * @description : 产品相关接口回参封装
 */
@Data
public class AppMaskModelResult implements Serializable {

    /**
     * APP模式 0:现金贷 1:贷超 2:现金贷-不可申请 3:未认证跳转
     */
    private Integer maskModel;

    /**
     * 身份认证信息 0: 未认证 1:认证
     */
    private Integer identityAuth;

    /**
     * Ocr认证信息 0: 未认证 1:认证
     */
    private Integer ocrAuth;

    /**
     * 补充信息 0: 未认证 1:认证
     */
    private Integer addInfoAuth;

    /**
     * 基本信息 0: 未认证 1:认证
     */
    private Integer basicInfoAuth;

    /**
     * 放款账户 0: 未认证 1:认证
     */
    private Integer remittanceAccountAuth;

    /**
     * 余额
     */
    private String amount;

    /**
     * 按钮文案
     */
    private String button;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 公告
     */
    private String cashNotice;

    /**
     * 轮播图列表
     */
    private List<Banner> banner;

    /**
     * 公告列表
     */
    private List<Notifcation> list;


}
