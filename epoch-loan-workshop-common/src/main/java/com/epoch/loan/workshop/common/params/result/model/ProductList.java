package com.epoch.loan.workshop.common.params.result.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.result;
 * @className : ProductList
 * @createTime : 2022/3/24 18:23
 * @description : 列表产品
 */
@Data
public class ProductList implements Serializable {

    /**
     * 产品Id
     */
    private Long id;

    /**
     * 商户Id
     */
    private Long merchantId;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 审批金额
     */
    private String approvalAmount;

    /**
     * 审批天数
     */
    private String approvalTerm;

    /**
     * 还款金额描述
     */
    private String remark;

    /**
     * 利率
     */
    private String rate;

    /**
     * logo
     */
    private String logo;

    /**
     * 产品类型
     */
    private Integer type;

    /**
     * H5推广链接
     */
    private String url;

    /**
     * 订单状态
     * 0   初始状态         70  待一推            80  一推成功待绑卡
     * 85  绑卡成功待申请    90  申请成功待审核     100 审批通过
     * 110 审核拒绝         115 用户确认待放款     161 放款取消
     * 169 放款失败         170 放款成功待还款     180 逾期 200 还款成功
     */
    private String orderStatus = "0";

    /**
     * 产品标签集合
     */
    private List<String> tagList;

    /**
     * 是否可申请 0是 1否
     */
    private Integer status = 0;

    /**
     * 订单描述
     */
    private String orderDesc = "Application";

    /**
     * 可申请数量
     */
    private String applyQuantity;

    /**
     * 订单编号
     */
    private String orderNo = "";

    /**
     * 按钮文案
     */
    private String orderStatusStr;

    /**
     * 通过率
     */
    private String passRate;

    /**
     * 产品Icon图标
     */
    private String icon;

    /**
     * 产品标签
     */
    private String tag;

    /**
     * 处理时间
     */
    private Date processTime;
}
