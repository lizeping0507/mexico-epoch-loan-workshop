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

    private static final long serialVersionUID = 116541653165465L;

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
     * 请求流水号
     */
    private String serialNo;
}
