package com.epoch.loan.workshop.common.config;

import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.common.config
 * @className : RestClientConfig
 * @createTime : 2022/5/12 18:37
 * @description : ES高可用构建方式
 */
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.rest.uris}")
    private String clusterNodes;

    @Value("${spring.elasticsearch.rest.ssl}")
    private String clusterSSL;

    /**
     * 高可用构建
     *
     * @return
     */
    @Override
    public RestHighLevelClient elasticsearchClient() {
        LogUtil.sysInfo("RestHighLevelClient install");

        String[] split = clusterNodes.split(":");

        // 重新构建ES
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(split[0], Integer.parseInt(split[1]) , clusterSSL)).setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                .setDefaultIOReactorConfig(IOReactorConfig.custom()
                        .setSoKeepAlive(true)
                        .build()).setKeepAliveStrategy((response, context) -> 3 * 1000 * 60));

        // 构建高可用方式
        return new RestHighLevelClient(restClientBuilder);
    }
}
