package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request.forward;
 * @className : StaticResourcesParam
 * @createTime : 2022/3/19 18:41
 * @description : 静态资源请求参数封装
 */
@Data
public class StaticResourcesParam extends BaseParams {
    private String orderNo;
}
