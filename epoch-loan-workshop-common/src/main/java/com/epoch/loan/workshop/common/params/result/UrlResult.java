package com.epoch.loan.workshop.common.params.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.params.result
 * @className : UrlResult
 * @createTime : 2022/03/29 16:51
 * @Description: 链接地址返回封装类
 */
@Data
public class UrlResult implements Serializable {

    /**
     * 链接地址
     */
    private String url;
}
