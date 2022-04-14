package com.epoch.loan.workshop.common.params.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.params.result
 * @className : Result
 * @createTime : 2021/3/10 21:59
 * @description : 访问拦截器
 */
@Data
@NoArgsConstructor
public class Result<T> implements Serializable {
    /**
     * 页大小
     */
    private int pageSize;

    /**
     * 当前页数
     */
    private int page;

    /**
     * 返回码
     */
    private int returnCode;

    /**
     * 数据
     */
    private T data;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 异常信息
     */
    private String ex;

    /**
     * 响应时间时间戳
     */
    private String responseTime;

    /**
     * 请求流水号
     */
    private String serialNo;
}
