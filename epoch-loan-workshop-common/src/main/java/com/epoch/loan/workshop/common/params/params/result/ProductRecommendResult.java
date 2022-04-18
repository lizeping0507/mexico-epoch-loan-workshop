package com.epoch.loan.workshop.common.params.params.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : ProductRecommendResult
 * @createTime : 22/3/30 18:00
 * @description : 推荐列表接口回参封装
 */
@Data
public class ProductRecommendResult implements Serializable {

    private List<ProductRecommendListResult> list;
}
