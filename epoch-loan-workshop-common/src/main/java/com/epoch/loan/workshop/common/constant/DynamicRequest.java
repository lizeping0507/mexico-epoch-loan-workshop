package com.epoch.loan.workshop.common.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.constant
 * @className : UrlMapping
 * @createTime : 2022/3/18 11:04
 * @description : 动态请求
 */
public class DynamicRequest {
    /**
     * 地址映射缓存
     */
    public final static Map<String, String> URL_MAPPING_CACHE = new HashMap<>();

    /**
     * 请求参数映射缓存
     */
    public final static Map<String, Map<String, String>> REQUEST_MAPPING_MAPPING_CACHE = new HashMap<>();

    /**
     * 响应参数映射缓存
     */
    public final static Map<String, Map<String, String>> RESPONSE_MAPPING_MAPPING_CACHE = new HashMap<>();

    /**
     * 混淆参数列表缓存
     */
    public final static Map<String, List<String>> RESPONSE_MAPPING_VIRTUAL_PARAMS_CACHE = new HashMap<>();
}
