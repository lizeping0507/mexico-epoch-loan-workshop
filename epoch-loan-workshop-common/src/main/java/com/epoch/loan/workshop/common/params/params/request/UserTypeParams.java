package com.epoch.loan.workshop.common.params.params.request;

import com.epoch.loan.workshop.common.params.params.BaseParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.params.params.request;
 * @className : ApplyParams
 * @createTime : 2022/3/30 15:06
 * @description : 申请借款请求参数封装
 */
@Data
public class UserTypeParams extends BaseParams {
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 产品id
     */
    private String productId;
}
