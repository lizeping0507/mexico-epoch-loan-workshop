package com.epoch.loan.workshop.common.params.result;

import com.epoch.loan.workshop.common.params.result.model.ProductBanner;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : BannerListResult
 * @createTime : 2022/03/30 14:56
 * @Description: 产品列表banner响应结果
 */
@Data
public class BannerListResult implements Serializable {

    /**
     * 产品列表
     */
    private List<ProductBanner> banner;
}
