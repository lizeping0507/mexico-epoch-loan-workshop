package com.epoch.loan.workshop.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author ljy
 * @packagename : com.epoch.loan.workshop.common.config
 * @className : AfConfig
 * @createTime : 2022/07/18 14:24
 * @Description:
 */
@RefreshScope
@Configuration
@Data
public class AfConfig {

    /**
     * 请求af地址
     */
    @Value("${af.requester.url}")
    private String afRequesterUrl;
}
