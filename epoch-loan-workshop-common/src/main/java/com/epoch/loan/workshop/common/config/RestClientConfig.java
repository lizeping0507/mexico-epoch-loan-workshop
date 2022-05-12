package com.epoch.loan.workshop.common.config;

import com.epoch.loan.workshop.common.util.LogUtil;
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
import org.springframework.util.StringUtils;

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

    /**
     * 高可用构建
     *
     * @return
     */
    @Override
    public RestHighLevelClient elasticsearchClient() {
        /* 使用密码
        final CredentialsProvider credentialsProvider=new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("",""));
        */

        LogUtil.sysInfo("RestHighLevelClient install");

        // 重新构建ES
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost("loan-es", 9200, "http")).setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                .setDefaultIOReactorConfig(IOReactorConfig.custom()
                        .setSoKeepAlive(true)
                        .build()));

        // 构建高可用方式
        return new RestHighLevelClient(restClientBuilder);
    }
}
