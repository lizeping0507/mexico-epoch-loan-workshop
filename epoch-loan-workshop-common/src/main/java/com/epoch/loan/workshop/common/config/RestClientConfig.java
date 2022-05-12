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

    @Value("${spring.elasticsearch.rest.uris}")
    private String clusterNodes;

    private List<HttpHost> httpHosts = new ArrayList<>();

    /**
     * 高可用构建
     *
     * @return
     */
    @Override
    @Bean
    @ConditionalOnMissingBean
    public RestHighLevelClient elasticsearchClient() {
        if (clusterNodes.isEmpty() && clusterNodes.contains(";") && clusterNodes.split(";").length > 0) {
            throw new RuntimeException("集群节点不允许为空");
        }

        List<String> list = Arrays.asList(clusterNodes.split(";"));

        list.forEach(node -> {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.notNull(parts, "Must defined");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                httpHosts.add(new HttpHost(parts[0], Integer.parseInt(parts[1]), "http"));
            } catch (Exception e) {
                throw new IllegalStateException("Invalid ES nodes " + "property '" + node + "'", e);
            }
        });
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));
        return getRestHighLevelClient(builder);
    }

    /**
     * 重新构建ES
     * @return
     */
    private RestHighLevelClient getRestHighLevelClient(RestClientBuilder builder) {
        LogUtil.sysInfo("RestHighLevelClient install");
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom()
                    .setSoKeepAlive(true)
                    .build());
            // 3分钟
            httpClientBuilder.setKeepAliveStrategy((response, context) -> 3 * 1000 * 60);
            return httpClientBuilder;
        });
        return new RestHighLevelClient(builder);
    }
}
