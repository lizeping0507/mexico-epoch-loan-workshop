package com.epoch.loan.workshop.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.epoch.loan.workshop.common.constant.DynamicRequest;
import com.epoch.loan.workshop.common.entity.mysql.LoanDynamicRequestEntity;
import com.epoch.loan.workshop.common.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.api.controller;
 * @className : ServerManagerController
 * @createTime : 2022/4/1 17:34
 * @description : 服务管理接口
 */
@RestController
@RequestMapping("/server")
public class ServerManagerController extends BaseController {

    /**
     * 刷新动态映射配置缓存
     */
    @GetMapping("/fresh/mappingCache")
    public Result freshUrlMappingCache() {
        Result result = new Result<>();

        List<LoanDynamicRequestEntity> dynamicRequests = dynamicRequestService.findAll();
        for (LoanDynamicRequestEntity dynamicRequest : dynamicRequests) {
            DynamicRequest.URL_MAPPING_CACHE.put(dynamicRequest.getMappingUrl(), dynamicRequest.getUrl());
            try {
                DynamicRequest.REQUEST_MAPPING_MAPPING_CACHE.put(
                        dynamicRequest.getMappingUrl(),
                        JSON.parseObject(dynamicRequest.getMappingRequestParams(), new TypeReference<Map<String, String>>() {
                        }));
                DynamicRequest.RESPONSE_MAPPING_MAPPING_CACHE.put(
                        dynamicRequest.getMappingUrl(),
                        JSON.parseObject(dynamicRequest.getMappingResponseParams(), new TypeReference<Map<String, String>>() {
                        }));
                DynamicRequest.RESPONSE_MAPPING_VIRTUAL_PARAMS_CACHE.put(
                        dynamicRequest.getMappingUrl(),
                        JSON.parseObject(dynamicRequest.getMappingVirtualParams(), new TypeReference<List<String>>() {
                        }));
                result.setMessage("success");
            } catch (Exception e) {
                LogUtil.sysError("[ServerManagerController freshUrlMappingCache]", e);
                result.setMessage("Error");
            }
        }

        return result;
    }
}
