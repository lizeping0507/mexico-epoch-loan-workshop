package com.epoch.loan.workshop.common.params.result;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : ProductRecommendListResult
 * @createTime : 22/3/30 17:29
 * @description : 推荐列表接口回参封装
 */
@Data
public class ProductRecommendListResult implements Serializable {

    private Long id;

    private Long merchantId;

    private String name;

    private String approvalAmount;

    private String approvalTerm;

    private String rate;

    private String logo;

    private String icon;

    private String remark;

    private Integer type;

    private String url;

    private String orderStatus = "0";

    private List<String> tagList;

    private Integer status = 0;

    private String orderDesc = "Application";

    private String applyQuantity;

    private String tag;

    private String orderNo = "";

    private String orderStatusStr;

    private Date processTime;
}
