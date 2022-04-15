package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : MergePushListParams
 * @createTime : 2022/3/21 11:29
 * @description : 多推产品列表接口请求数据封装
 */
@Data
public class MergePushListParams extends BaseParams {

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 页面类型 0-复贷，1-多推
     */
    private Integer pageType;
}
