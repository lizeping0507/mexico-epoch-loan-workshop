package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : ljy
 * @packageName : com.epoch.loan.workshop.common.params.params.request
 * @className : ProductRecommendListParams
 * @createTime : 22/3/30 17:30
 * @description : 推荐列表接口入参封装
 */
@Data
public class ProductRecommendListParams extends BaseParams {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 产品id
     */
    private Long productId;

    /**
     *
     */
    private Integer tag;

}
